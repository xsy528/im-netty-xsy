package cn.gyyx.im.video.handler;

import cn.gyyx.im.beans.QueueData;
import cn.gyyx.im.beans.request.RequestWaitQueue;
import cn.gyyx.im.beans.response.WaitQueueInfoMsg;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import cn.gyyx.im.video.queue.QueueService;
import cn.gyyx.im.video.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RequestEnterQueueHandler implements IMsgHandler<RequestWaitQueue> {

    private final RoomService roomService;
    private final QueueService queueService;
    private final SendMessageService sendMessageService;

    public RequestEnterQueueHandler(RoomService roomService, QueueService queueService, SendMessageService sendMessageService) {
        this.roomService = roomService;
        this.queueService = queueService;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Request_Enter_Queue;
    }

    @Override
    public void handler(RequestWaitQueue iBaseMsg) {
        roomService.addWait(iBaseMsg.getAppId(), iBaseMsg.getUserId());
        //todo 通知该用户还有多少个等待用户

        List<QueueData> queueData = queueService.allQueue(iBaseMsg.getAppId());
        QueueData queueInfo = queueData.stream().filter(data -> iBaseMsg.getUserId().equals(data.getUserId())).findFirst().orElse(null);
        List<String> userIdList = queueData.stream().map(QueueData -> QueueData.getUserId()).collect(Collectors.toList());

        //发送响应
        WaitQueueInfoMsg waitQueueInfoMsg = new WaitQueueInfoMsg();
        waitQueueInfoMsg.convert(iBaseMsg);
        waitQueueInfoMsg.setMsgType(MsgTypeEnum.All_Queue_Info.getIntValue());
        waitQueueInfoMsg.setWaitTotal(queueData.size());
        waitQueueInfoMsg.setWaitIndex(queueInfo==null?-1:queueInfo.getIndex());
        waitQueueInfoMsg.setUserIdList(userIdList);
        sendMessageService.sendMessage(iBaseMsg.getAppId(),iBaseMsg.getUserId(),iBaseMsg.getDeviceId(), waitQueueInfoMsg);
//        RoomInfo roomInfo = roomService.getRoomInfo(requestWaitQueue.getAppId(), requestWaitQueue.getRoomId());
//        roomService.addRoom(roomInfo,requestWaitQueue.getUserId());
    }
}
