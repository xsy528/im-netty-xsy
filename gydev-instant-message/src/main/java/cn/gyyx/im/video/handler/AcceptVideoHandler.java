package cn.gyyx.im.video.handler;

import cn.gyyx.im.beans.request.AcceptVideoMessage;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AcceptVideoHandler implements IMsgHandler<AcceptVideoMessage> {
    private final SendMessageService sendMessageService;

    public AcceptVideoHandler(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Accept_Video;
    }

    @Override
    public void handler(AcceptVideoMessage acceptVideoMessage) {
        acceptVideoMessage.setMsgType(MsgTypeEnum.Other_Accept_Video.getIntValue());
        sendMessageService.sendMessage(acceptVideoMessage.getAppId(),acceptVideoMessage.getRequestUserId(),acceptVideoMessage);
    }
}
