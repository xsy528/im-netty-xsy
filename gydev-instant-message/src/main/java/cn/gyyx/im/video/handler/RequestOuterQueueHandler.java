package cn.gyyx.im.video.handler;

import cn.gyyx.im.beans.QueueData;
import cn.gyyx.im.beans.request.RequestWaitQueue;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import cn.gyyx.im.video.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RequestOuterQueueHandler implements IMsgHandler<RequestWaitQueue> {

    private final RoomService roomService;
    private final SendMessageService sendMessageService;

    public RequestOuterQueueHandler(RoomService roomService, SendMessageService sendMessageService) {
        this.roomService = roomService;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Request_Outer_Queue;
    }

    @Override
    public void handler(RequestWaitQueue requestWaitQueue) {
        QueueData queueData = roomService.reduceWait(requestWaitQueue.getAppId(),requestWaitQueue.getReceiveUserId());

        requestWaitQueue.setMsgType(MsgTypeEnum.Request_Outer_Queue_Success.getIntValue());
        sendMessageService.sendMessage(requestWaitQueue.getAppId(), queueData.getUserId(), requestWaitQueue);

        roomService.createRoom(requestWaitQueue.getAppId(), requestWaitQueue.getUserId(),queueData.getUserId());
    }
}
