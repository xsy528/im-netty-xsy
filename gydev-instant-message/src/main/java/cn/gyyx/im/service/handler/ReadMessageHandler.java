package cn.gyyx.im.service.handler;

import cn.gyyx.im.beans.response.ReturnMsgBean;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.platform.CallBackFactory;
import cn.gyyx.im.platform.PlatformCallBack;
import cn.gyyx.im.beans.request.SendMessage;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zxw
 * @Description:消息已读
 * @create 2024/3/26/002615:49
 * @Version 1.0
 **/
@Slf4j
@Component
public class ReadMessageHandler implements IMsgHandler<SendMessage> {

    private final CallBackFactory callbackFactory;
    private final SendMessageService sendMessageService;

    public ReadMessageHandler(CallBackFactory callbackFactory, SendMessageService sendMessageService) {
        this.callbackFactory = callbackFactory;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Read_Message;
    }

    @Override
    public void handler(SendMessage sendMessage) {
        // 消息读取
        PlatformCallBack platform = callbackFactory.getPlatformCallBack(sendMessage.getAppId());
        if (null != platform) {
            platform.userReadMessage(sendMessage.getAppId(), sendMessage.getUserId(), sendMessage.getReceiveUserId(), sendMessage.getMessageId());
        }
        ReturnMsgBean returnMsgBean = new ReturnMsgBean(sendMessage);
        returnMsgBean.setMsgType(MsgTypeEnum.Read_Message_Success.getIntValue());
        returnMsgBean.setResult("success");
        returnMsgBean.setContent("消息已读");
        returnMsgBean.setReceiveUserId(sendMessage.getReceiveUserId());
        returnMsgBean.setUserId(sendMessage.getUserId());
        //返回应答给发起人
        sendMessageService.sendMessage(sendMessage.getAppId(), sendMessage.getReceiveUserId(),sendMessage.getDeviceId(), returnMsgBean);
        //通知消息发送人
        //sendClientMessage(sendMessage.getAppId(), sendMessage.getReceiveUserId(),returnMsgBean);
    }
}
