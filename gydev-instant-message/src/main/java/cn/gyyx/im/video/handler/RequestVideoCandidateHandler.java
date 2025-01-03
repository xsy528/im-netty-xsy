package cn.gyyx.im.video.handler;

import cn.gyyx.im.beans.request.AcceptVideoMessage;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RequestVideoCandidateHandler implements IMsgHandler<AcceptVideoMessage> {
    private final SendMessageService sendMessageService;

    public RequestVideoCandidateHandler(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Request_Video_Candidate;
    }

    @Override
    public void handler(AcceptVideoMessage message) {
        message.setMsgType(MsgTypeEnum.Other_Request_Video_Candidate.getIntValue());
        sendMessageService.sendMessage(message.getAppId(),message.getRequestUserId(),message);
    }
}
