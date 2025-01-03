package cn.gyyx.im.utils;

import cn.gyyx.aoplog.utils.SnowflakeIdWorker;
import cn.gyyx.im.beans.StringConstant;
import cn.gyyx.im.beans.UserSession;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

//已支持：支持多端登录，支持多端发送消息，支持多pod
@Slf4j
public class SessionUtil {

    /**
     * channel通道中绑定用户信息的key
     */
    public static final AttributeKey<UserSession> SESSION = AttributeKey.newInstance("session");
    /**
     * channel通道中绑定ws参数信息
     */
    public static final AttributeKey<Map<String, String>> urlParam = AttributeKey.newInstance("url");

    //sessionKey，多端登录
    private static Map<String, Map<String, Channel>> userChannelMap = new ConcurrentHashMap<>();

    /**
     * 发送消息缓存，用于消息重试
     */
    private static Map<String,Triple<Long,Channel,String>> sendMessageMap = new ConcurrentHashMap<>();
    private static SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(System.currentTimeMillis()%31);

    /**
     * 用户下线
     * @param channel
     * @return 是否是全下线 true用户所有端都不在线， false 用户仍有端连接
     */
    public static boolean loginOut(Channel channel) {
        //去除用户缓存
        UserSession userSession = channel.attr(SESSION).get();
        if(userSession==null){
            return true;
        }
        String sessionKey = getSessionKey(userSession);

        Map<String, Channel> channelMap = userChannelMap.get(sessionKey);
        if (channelMap != null && channelMap.size() > 0) {
            channelMap.remove(userSession.getDevice());
        }

        boolean result = false;
        if (channelMap !=null && channelMap.size() == 0) {
            log.info("用户：{}，登出IM系统",userSession);
            //用户所有端均无登录状态，则删除在线状态
            userChannelMap.remove(sessionKey);
            result = true;
        }else {
            log.info("用户：{}，登出，踢出其中一端设备",userSession);
        }

        channel.attr(SESSION).set(null);

        return result;
    }

    public static String getSessionKey(UserSession userSession) {
        return getSessionKey(userSession.getAppId(), userSession.getUserId());
    }

    public static String getSessionKey(String appId, String userId) {
        return appId + "&" + userId;
    }

    /**
     * 用户登录
     *
     * @param channel
     */
    public static void login(Channel channel) {
        UserSession userSession = channel.attr(SESSION).get();

        String sessionKey = getSessionKey(userSession);
        Map<String, Channel> channelMap = userChannelMap.get(sessionKey);

        if (channelMap != null) {
            //检查平台是否支持多端登录
//            PlatformEnum platformEnum = PlatformEnum.searchString(userSession.getAppId());
//            if (platformEnum.isAllowMultiLogin()) {
//                //支持多端登录时检查已登录数量
//                if(channelMap.size()==platformEnum.getAllowMultiLoginCount()){
//                    //多端登录数量过多时，踢最早登录的端
//                    Object[] keyArr = channelMap.keySet().toArray();
//                    Channel channel1 = channelMap.get(keyArr[0]);
//                    loginOut(channel1, redisTemplate);
//                }
//            }else {
//                //如果平台不支持多端登录，则需要踢已登录账号
//                channelMap.values().forEach(channel1 -> {
//                    loginOut(channel1, redisTemplate);
//                });
//            }
            channelMap.put(userSession.getDevice(), channel);
        } else {
            channelMap = new ConcurrentHashMap<>();
            channelMap.put(userSession.getDevice(), channel);
            userChannelMap.put(sessionKey, channelMap);
        }
    }

    public static void sendMessage(String appId, String userId, String msg) {
        Map<String, Channel> channelMap = userChannelMap.get(getSessionKey(appId, userId));
        if (channelMap != null && channelMap.size() > 0) {
            channelMap.values().forEach(channel -> sendRetryMessage(channel,msg));
        }
    }

    public static void sendMessage(String appId, String userId,String deviceId, String msg) {
        Map<String, Channel> channelMap = userChannelMap.get(getSessionKey(appId, userId));
        if (channelMap != null && channelMap.size() > 0) {
            Channel channel = channelMap.get(deviceId);
            if(channel!=null){
                sendRetryMessage(channel,msg);
            }
        }
    }

    private static void sendRetryMessage(Channel channel,String msg){
        JSONObject returnMsg = JSONObject.parseObject(msg);
        String unique = String.valueOf(snowflakeIdWorker.nextId());
        //5秒之后
        long currentTimeMillis = System.currentTimeMillis()+5000L;
        returnMsg.put(StringConstant.unique,unique);
        String result = returnMsg.toJSONString();
        sendMessageMap.putIfAbsent(unique,Triple.of(currentTimeMillis,channel, result));
        channel.writeAndFlush(new TextWebSocketFrame(result));
    }

    public static void retrySendMessage(){
        long currentTimeMillis = System.currentTimeMillis();
        sendMessageMap.values().forEach(value ->{
            if(value.getLeft()<=currentTimeMillis){
                //5秒之前的消息进行重试
                Channel channel = value.getMiddle();
                if(channel!=null && channel.isActive()) {
                    channel.writeAndFlush(new TextWebSocketFrame(value.getRight()));
                }
            }
        });
    }

    public static void clearUnique(String unique){
        sendMessageMap.remove(unique);
    }

    /**
     * 获取用户所有channel
     * @param appId
     */
    public static Map<String,Channel> getChannel(String appId, String userId) {
        return userChannelMap.get(getSessionKey(appId, userId));
    }

    /**
     * 检查用户是否在线 true 在线，false 不在线
     * @param appId
     */
    public static boolean checkOnline(String appId, String userId) {
        Map<String, Channel> channelMap = userChannelMap.get(getSessionKey(appId, userId));
        return channelMap!=null && channelMap.size()>0;
    }

    /**
     * pod关闭事件
     */
    public static void podDestroy() {
        userChannelMap.forEach((key,value)->{
            for (Channel channel : value.values()) {
                loginOut(channel);
            }
        });
    }
}
