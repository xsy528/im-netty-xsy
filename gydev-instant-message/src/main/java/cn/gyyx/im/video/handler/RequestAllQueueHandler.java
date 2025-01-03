package cn.gyyx.im.video.handler;

import cn.gyyx.im.beans.IBaseMsg;
import cn.gyyx.im.beans.QueueData;
import cn.gyyx.im.beans.response.WaitQueueInfoMsg;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import cn.gyyx.im.video.queue.QueueService;
import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestAllQueueHandler implements IMsgHandler<IBaseMsg> {
    private final QueueService queueService;
    private final SendMessageService sendMessageService;

    public RequestAllQueueHandler(QueueService queueService, SendMessageService sendMessageService) {
        this.queueService = queueService;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Request_All_Queue;
    }

    @Override
    public void handler(IBaseMsg iBaseMsg) {
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
    }
}
