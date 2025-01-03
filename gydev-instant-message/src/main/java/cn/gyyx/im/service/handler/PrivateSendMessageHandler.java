package cn.gyyx.im.service.handler;

import cn.gydev.lib.utils.DateCommonUtils;
import cn.gydev.lib.utils.JsonUtils;
import cn.gydev.lib.utils.MD5;
import cn.gyyx.im.beans.entity.ImChatLog;
import cn.gyyx.im.beans.response.ReturnMsgBean;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.beans.request.SendMessage;
import cn.gyyx.im.platform.CallBackFactory;
import cn.gyyx.im.service.BadWordService;
import cn.gyyx.im.service.NewChatLogService;
import cn.gyyx.im.service.SendMessageService;
import cn.gyyx.im.utils.CommonUtil;
import cn.gyyx.im.utils.LoginUserUtil;
import com.alibaba.fastjson2.JSONObject;
import cn.gyyx.im.enums.Constant;
import cn.gyyx.im.enums.PlatformEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.platform.PlatformCallBack;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 一对一发送消息
 *
 * @author: zxw
 * @date: 2024/4/11/0011 16:50
 **/
@Slf4j
@Component
public class PrivateSendMessageHandler implements IMsgHandler<SendMessage> {

    /**
     * 接口密钥
     **/
    @Value("${login.key}")
    private String key;

    @Resource
    private Environment env;

    private final NewChatLogService chatLogService;

    private final RedisTemplate<String, String> redisTemplate;

    private final BadWordService badWordService;

    private final CallBackFactory callbackFactory;
    private final SendMessageService sendMessageService;


    public PrivateSendMessageHandler(NewChatLogService chatLogService,
                                     RedisTemplate<String, String> redisTemplate,
                                     BadWordService badWordService, CallBackFactory callbackFactory,
                                     SendMessageService sendMessageService) {
        this.chatLogService = chatLogService;
        this.redisTemplate = redisTemplate;
        this.badWordService = badWordService;
        this.callbackFactory = callbackFactory;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Send_Message;
    }

    @Override
    public void handler(SendMessage sendMessage) {
        log.info("开始发送消息 userId{},info:{}", sendMessage.getUserId(), JsonUtils.objToJson(sendMessage));
        ReturnMsgBean returnMsgBean = new ReturnMsgBean(sendMessage);
        returnMsgBean.setTxNo(sendMessage.getTimestamp());
        returnMsgBean.setMark(sendMessage.getMark());
        returnMsgBean.setContentType(sendMessage.getContentType());
        returnMsgBean.setSendUserId(sendMessage.getUserId());
        returnMsgBean.setReceiveUserId(sendMessage.getReceiveUserId());
        //获取配置
        Integer property = env.getProperty("sign.expire.time." + sendMessage.getAppId(), Integer.class);
        //验证签名数据
        Boolean checkSign = checkSign(sendMessage,property);
        try {
            if (Boolean.FALSE.equals(checkSign)) {
                returnMsgBean.setMsgType(MsgTypeEnum.Chat_Sign_TimeOut.getIntValue());
                returnMsgBean.setContent(sendMessage.getMessage());
                returnMsgBean.setResult("fail");
                sendMessageService.sendMessage(sendMessage.getAppId(),sendMessage.getUserId(),sendMessage.getDeviceId(), returnMsgBean);
                log.info("发送账号：{}消息验证签名错误,数据为：{}", sendMessage.getUserId(), JsonUtils.objToJson(sendMessage));
                return;
            }
            //空白消息
            if (StringUtils.isBlank(sendMessage.getMessage().trim())) {
                returnMsgBean.setMsgType(MsgTypeEnum.Send_Message_Success.getIntValue());
                returnMsgBean.setContent("请勿发送空白消息");
                returnMsgBean.setResult("fail");
                sendMessageService.sendMessage(sendMessage.getAppId(),sendMessage.getUserId(),sendMessage.getDeviceId(), returnMsgBean);
                log.info("发送账号：{}消息为空", sendMessage.getUserId());
                return;
            }
            //消息内容防止xss注入
            sendMessage.setMessage(CommonUtil.cleanXSS(sendMessage.getMessage()));
            //记录消息
            ImChatLog chatLog = chatLogService.saveChatHistory(sendMessage.getUserId(), sendMessage.getReceiveUserId(),
                    sendMessage.getMessage(), sendMessage.getSendTimeDate(), sendMessage.getAppId(), sendMessage.getContentType());
            //判断接收人是否在线，不在线直接回复发送人
            boolean online = LoginUserUtil.checkOnline(sendMessage.getAppId(), sendMessage.getReceiveUserId());
            // 获取实现
            PlatformCallBack platformCallBack = callbackFactory.getPlatformCallBack(sendMessage.getAppId());
            if (null == platformCallBack) {
                log.error("实现不存在, appId:{}", sendMessage.getAppId());
               return;
            }
            //消息敏感词处理
            String replaced = platformCallBack.checkBadWord(badWordService, sendMessage.getMessage(), sendMessage.getContentType());
            log.info("敏感词校验完成, replaced:{}", replaced);
            if (null == replaced) {
                // 认为敏感词失败
                returnMsgBean.setMsgType(MsgTypeEnum.Message_Bad_Words.getIntValue());
                returnMsgBean.setContent("消息内容包含敏感词");
                returnMsgBean.setResult("fail");
                log.info("发送账号：{}消息包含敏感词", sendMessage.getUserId());
                sendMessageService.sendMessage(sendMessage.getAppId(),sendMessage.getUserId(),sendMessage.getDeviceId(), returnMsgBean);
            } else {
                if (online) {
                    log.info("接收人在线，接收人:{},发送人：{}", sendMessage.getReceiveUserId(), sendMessage.getUserId());
                    //在线直接发送消息并返回成功
                    returnMsgBean.setMsgType(MsgTypeEnum.Push_Message.getIntValue());
                    returnMsgBean.setContent(replaced);
                    returnMsgBean.setMessageId(chatLog.getCode());
                    returnMsgBean.setResult("success");
                    returnMsgBean.setSendTime(sendMessage.getSendTime());
                    // 设置入库时间
                    returnMsgBean.setServerTime(chatLog.getCreateTime().getTime());
                    //发送消息
                    sendMessageService.sendMessage(sendMessage.getAppId(), sendMessage.getReceiveUserId(), returnMsgBean);
                }
                //发送消息成功后回调
                JSONObject params = new JSONObject();
                params.put("appId", sendMessage.getAppId());
                params.put("sender", sendMessage.getUserId());
                params.put("receiver", sendMessage.getReceiveUserId());
                params.put("content", sendMessage.getMessage());
                params.put("messageId", chatLog.getCode());
                params.put("contentType", sendMessage.getContentType());
                params.put("online", online);
                platformCallBack.sendMessageCallback(params);
                log.info("接收人:{}通道信息为空", sendMessage.getReceiveUserId());
                //回复发送人
                ReturnMsgBean replyMsgBean = getReturnMsgBean(sendMessage, chatLog.getCode());
                sendMessageService.sendMessage(sendMessage.getAppId(), sendMessage.getReceiveUserId(),sendMessage.getDeviceId(), replyMsgBean);
            }

        } catch (Exception e) {
            log.error("发送消息异常；{}", e);
        }
    }

    private ReturnMsgBean getReturnMsgBean(SendMessage sendMessage, Long messageId) {
        ReturnMsgBean replyMsgBean = new ReturnMsgBean(sendMessage);
        replyMsgBean.setMark(sendMessage.getMark());
        replyMsgBean.setMsgType(MsgTypeEnum.Send_Message_Success.getIntValue());
        replyMsgBean.setSendUserId(sendMessage.getUserId());
        replyMsgBean.setReceiveUserId(sendMessage.getReceiveUserId());
        //返回消息id
        replyMsgBean.setMessageId(messageId);
        replyMsgBean.setContent(sendMessage.getMessage());
        replyMsgBean.setResult("success");
        replyMsgBean.setSendTime(sendMessage.getSendTime());
        replyMsgBean.setContentType(sendMessage.getContentType());
        return replyMsgBean;
    }

    /**
     * 验证发送消息签名
     *
     * @param msgBean 消息内容
     * @param property 接口请求过期时间
     * @return void
     * @author: zxw
     * @date: 2024/3/21/0021 10:59
     **/
    private Boolean checkSign(SendMessage msgBean,Integer property) {
        //判断redis是否存在
        String cacheKey = Constant.getChatPermissionKey(msgBean.getAppId(), msgBean.getReceiveUserId(), msgBean.getUserId());
        Object checkSignResult = redisTemplate.opsForValue().get(cacheKey);
        if (checkSignResult != null) {
            JSONObject jsonObject = JSONObject.parseObject(checkSignResult.toString());
            Object checkResult = jsonObject.get(msgBean.getReceiveUserId());
            if (checkResult != null) {
                if(!(boolean) checkResult){
                    redisTemplate.delete(cacheKey);
                }
                return (boolean) checkResult;
            }
        }
        //获取当前时间戳
        long currentTime = System.currentTimeMillis();
        long timeStamp = msgBean.getTimestamp();
        long abs = Math.abs(currentTime - timeStamp);
        // 相差五分钟以上 不合法
        if (abs / DateCommonUtils.SECOND_MILLISECONDS > property) {
            log.info("账号{}发送消息签名错误, 时间差超限", msgBean.getUserId());
            return false;
        }
        String sign = MD5.encode(msgBean.getMsgType() + msgBean.getAppId() + msgBean.getReceiveUserId() + timeStamp + key);
        //验证成功记录redis
        if (sign.equals(msgBean.getReceiveSign())) {
            //记录验证成功失败
            Map<String, Boolean> checkSignMap = Map.of(msgBean.getReceiveUserId(), true);

            PlatformEnum platformEnum = PlatformEnum.searchString(msgBean.getAppId());
            redisTemplate.opsForValue().set(cacheKey, JsonUtils.objToJson(checkSignMap), platformEnum.getChatExpireTime(), TimeUnit.MINUTES);
            return true;
        }
        log.info("账号{}发送消息签名错误, 签名错误", msgBean.getUserId());
        return false;
    }
}
