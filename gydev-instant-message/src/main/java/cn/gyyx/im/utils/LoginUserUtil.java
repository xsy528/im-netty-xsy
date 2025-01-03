package cn.gyyx.im.utils;

import cn.gydev.lib.utils.DateCommonUtils;
import cn.gyyx.im.beans.IBaseMsg;
import cn.gyyx.im.beans.UserSession;
import cn.gyyx.im.enums.PlatformEnum;
import cn.gyyx.im.service.RecordImService;
import cn.gyyx.im.enums.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

//已支持：支持多端登录，支持多端发送消息,支持多pod
@Slf4j
public class LoginUserUtil {

    /**
     * key : sessionKey
     * value : key : deviceId
     *         value : podName
     */
    private static Map<String, Map<String, String>> userChannelMap = new ConcurrentHashMap<>();

    /**
     * 用户下线
     * @param redisTemplate
     * @return 是否是全下线 true用户所有端都不在线， false 用户仍有端连接
     */
    public static boolean loginOut(String appId, String userId, String deviceId, RedisTemplate redisTemplate) {
        String sessionKey = getSessionKey(appId,userId);

        Map<String, String> channelMap = userChannelMap.get(sessionKey);
        if (channelMap != null && channelMap.size() > 0) {
            channelMap.remove(deviceId);
        }

        boolean result = false;
        if (channelMap==null || channelMap.size() == 0) {
            log.info("平台：{},用户：{}，登出IM系统",appId,userId);
            //用户所有端均无登录状态，则删除在线状态
            redisTemplate.delete(Constant.getOnlineKey(sessionKey));
            userChannelMap.remove(sessionKey);
            result = true;
        }else {
            log.info("平台：{},用户：{}，登出，踢出其中一端设备",appId,userId);
        }

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
     */
    public static void login(String appId, String userId, String deviceId,String podName,
                             RedisTemplate<String,String> redisTemplate) {
        //检查平台是否支持多端登录
        PlatformEnum platformEnum = PlatformEnum.searchString(appId);

        String sessionKey = getSessionKey(appId, userId);
        Map<String, String> channelMap = userChannelMap.get(sessionKey);

        if (channelMap != null) {
            if (platformEnum.isAllowMultiLogin()) {
                //支持多端登录时检查已登录数量
                if(channelMap.size()==platformEnum.getAllowMultiLoginCount()){
                    //多端登录数量过多时，踢最早登录的端
                    Set<String> keyArr = channelMap.keySet();
                    Iterator<String> iterator = keyArr.iterator();
                    loginOut(appId,userId,iterator.next(), redisTemplate);
                }
            }else {
                //如果平台不支持多端登录，则需要踢已登录账号
                channelMap.keySet().forEach(otherDeviceId -> {
                    loginOut(appId,userId,otherDeviceId, redisTemplate);
                });
            }
            channelMap.put(deviceId, podName);
        } else {
            channelMap = new ConcurrentHashMap<>();
            channelMap.put(deviceId, podName);
            userChannelMap.put(sessionKey, channelMap);
        }
        //获取当前时间距离24点还剩多少秒
        int leaveSeconds = DateTimeUtil.betweenTime(DateTimeUtil.getTodayEndTime(), new Date(), DateTimeUnit.ONE_SECONDS);

        //记录在线人数在储存在线状态之前
        RecordImService.recordTotalUserNumber(redisTemplate, sessionKey,leaveSeconds);
        //存储在线状态
        redisTemplate.opsForValue().set(Constant.getOnlineKey(sessionKey), String.valueOf(channelMap.size())
                , DateCommonUtils.DAY_HOURS, TimeUnit.HOURS);

        //记录最大登录数
        if (leaveSeconds > 0) {
            redisTemplate.opsForValue().set(Constant.getDayLoginKey(sessionKey), "1", leaveSeconds, TimeUnit.SECONDS);
        }

        //记录最大在线人数
        RecordImService.recordMaxNumber(redisTemplate, userChannelMap.size(), Constant.IM_MAX_ONLINE_NUMBER,leaveSeconds);

        //记录最大在线设备
        int total = 0;
        for(Map<String, String> items : userChannelMap.values()){
            total += items.size();
        }
        RecordImService.recordMaxNumber(redisTemplate, total, Constant.IM_MAX_DEVICE_NUMBER,leaveSeconds);

        RecordImService.recordTotalNumber(redisTemplate, Constant.IM_TOTAL_LINK_NUMBER,leaveSeconds);
    }

    /**
     * 检查用户是否在线 true 在线，false 不在线
     * @param appId
     */
    public static boolean checkOnline(String appId, String userId) {
        Map<String, String> channelMap = userChannelMap.get(getSessionKey(appId, userId));
        return channelMap!=null && channelMap.size()>0;
    }
}
