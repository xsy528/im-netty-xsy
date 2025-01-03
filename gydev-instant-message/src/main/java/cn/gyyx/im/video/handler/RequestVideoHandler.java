package cn.gyyx.im.video.handler;

import cn.gyyx.im.beans.request.AcceptVideoMessage;
import cn.gyyx.im.beans.request.SendMessage;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RequestVideoHandler implements IMsgHandler<AcceptVideoMessage> {
    private final SendMessageService sendMessageService;

    public RequestVideoHandler(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Request_Video;
    }

    @Override
    public void handler(AcceptVideoMessage message) {
        message.setMsgType(MsgTypeEnum.Other_Request_Video.getIntValue());

        //交换userId
        String requestUserId = message.getRequestUserId();
        message.setRequestUserId(message.getUserId());
        message.setUserId(requestUserId);

        sendMessageService.sendMessage(message.getAppId(),requestUserId,message);
    }
}
