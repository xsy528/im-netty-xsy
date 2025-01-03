package cn.gyyx.im.service.handler;

import cn.gydev.lib.utils.DateCommonUtils;
import cn.gydev.lib.utils.JsonUtils;
import cn.gyyx.im.beans.response.ReturnMsgBean;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.beans.request.RetractMessage;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import cn.gyyx.im.utils.LoginUserUtil;
import cn.gyyx.im.service.NewChatLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zxw
 * @Description: 消息撤回
 * @create 2024/3/26/002613:39
 * @Version 1.0
 **/
@Slf4j
@Component
public class RetractMessageHandler implements IMsgHandler<RetractMessage> {

    private final NewChatLogService chatLogService;
    private final SendMessageService sendMessageService;
    @Resource
    private Environment env;

    public RetractMessageHandler(NewChatLogService chatLogService, SendMessageService sendMessageService) {
        this.chatLogService = chatLogService;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Retract_Message;
    }

    @Override
    public void handler(RetractMessage sendMessage) {
        try {
            log.info("开始执行消息撤回,消息信息为{}", JsonUtils.objToJson(sendMessage));
            String messageId = sendMessage.getMessageId();
            //获取配置
            Integer property = env.getProperty("message.retract.time." + sendMessage.getAppId(), Integer.class);

            //防空指针判断
            property = property == null ? 2 : property;
            //获取开始时间，结束时间

            String retractTime = sendMessage.getRetractTime();
            String startTime;
            if (StringUtils.isEmpty(retractTime)) {
                Date startDate = DateCommonUtils.add(new Date(), Calendar.MINUTE, -property);
                startTime = DateCommonUtils.format(startDate, DateCommonUtils.FORMAT_SECOND);
            } else {
                SimpleDateFormat format = new SimpleDateFormat(DateCommonUtils.FORMAT_SECOND);
                Date parse = format.parse(retractTime);
                startTime = DateCommonUtils.format(DateCommonUtils.add(parse, Calendar.MINUTE, -property), DateCommonUtils.FORMAT_SECOND);
            }


            //消息撤回（根据时间段和消息id）
            boolean withdrawChat = chatLogService.withdrawChat(Long.parseLong(messageId), sendMessage.getReceiveUserId(),
                    sendMessage.getUserId(), sendMessage.getAppId(), startTime);
            
            ReturnMsgBean returnMsgBean = new ReturnMsgBean(sendMessage);

            returnMsgBean.setSendUserId(sendMessage.getUserId());
            returnMsgBean.setReceiveUserId(sendMessage.getReceiveUserId());

            if (withdrawChat) {
                log.info("消息撤回成功：消息id为：{}", messageId);
                returnMsgBean.setMsgType(MsgTypeEnum.Retract_Message_Receive_Success.getIntValue());
                returnMsgBean.setMessageId(Long.parseLong(sendMessage.getMessageId()));
                returnMsgBean.setContent("撤回成功");
                returnMsgBean.setResult("success");
                returnMsgBean.setSendTime(sendMessage.getRetractTime());
                returnMsgBean.setMark(sendMessage.getMark());
                //发起端消息通知
                sendMessageService.sendMessage(sendMessage.getAppId(), sendMessage.getReceiveUserId(),sendMessage.getDeviceId(), returnMsgBean);

                //撤回消息通知判断接收人是否在线
                if (LoginUserUtil.checkOnline(sendMessage.getAppId(), sendMessage.getReceiveUserId())) {
                    log.info("通知接收人撤回消息，撤回人：{}，接收人：{}",sendMessage.getUserId(),sendMessage.getReceiveUserId());

                    returnMsgBean.setMsgType(MsgTypeEnum.Retract_Message_Promoter_Success.getIntValue());
                    returnMsgBean.setContent("对方撤回了一条消息");
                    returnMsgBean.setMark(sendMessage.getMark());
                    sendMessageService.sendMessage(sendMessage.getAppId(), sendMessage.getReceiveUserId() , returnMsgBean);
                } else {
                    //todo 接收人不在线，发送消息到mq,上线后通知接收人
                }
            } else {
                returnMsgBean.setMsgType(MsgTypeEnum.Retract_Message_Receive_Success.getIntValue());
                returnMsgBean.setMessageId(Long.parseLong(sendMessage.getMessageId()));
                log.info("消息撤回失败：消息id为：{}", messageId);
                returnMsgBean.setContent("超过" + property + "分钟，无法撤回！");
                returnMsgBean.setResult("false");
                returnMsgBean.setMark(sendMessage.getMark());
                sendMessageService.sendMessage(sendMessage.getAppId(), sendMessage.getReceiveUserId(),sendMessage.getDeviceId(), returnMsgBean);
            }
        }catch (Exception e){
            log.error("撤回消息异常！");
            log.error(e.getMessage(),e);
        }
    }
}
