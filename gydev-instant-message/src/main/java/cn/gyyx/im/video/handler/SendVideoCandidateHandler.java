package cn.gyyx.im.video.handler;

import cn.gyyx.im.beans.request.AcceptVideoMessage;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendVideoCandidateHandler implements IMsgHandler<AcceptVideoMessage> {
    private final SendMessageService sendMessageService;

    public SendVideoCandidateHandler(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Send_Video_Candidate;
    }

    @Override
    public void handler(AcceptVideoMessage acceptVideoMessage) {
        acceptVideoMessage.setMsgType(MsgTypeEnum.Other_Send_Video_Candidate.getIntValue());
        sendMessageService.sendMessage(acceptVideoMessage.getAppId(),acceptVideoMessage.getRequestUserId(),acceptVideoMessage);
    }
}
