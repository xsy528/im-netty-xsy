package cn.gyyx.im.video.handler;
import cn.gyyx.im.beans.vo.RoomInfo;
import cn.gyyx.im.video.service.RoomService;
import com.google.common.collect.Lists;

import cn.gyyx.im.beans.request.RequestWaitQueue;
import cn.gyyx.im.beans.response.AllRoomInfoMsg;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AllRoomInfoHandler implements IMsgHandler<RequestWaitQueue> {
    private final SendMessageService sendMessageService;
    private final RoomService roomService;

    public AllRoomInfoHandler(SendMessageService sendMessageService, RoomService roomService) {
        this.sendMessageService = sendMessageService;
        this.roomService = roomService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Request_All_Room_Info;
    }

    @Override
    public void handler(RequestWaitQueue requestWaitQueue) {
        //检验权限   返回所有房间列表
        List<RoomInfo> allRoomInfo = roomService.getAllRoomInfo(requestWaitQueue.getAppId());

        AllRoomInfoMsg msg = new AllRoomInfoMsg();
        msg.setTotal(allRoomInfo.size());
        msg.setRoomInfoMsgs(allRoomInfo);
        msg.convert(requestWaitQueue);
        msg.setMsgType(MsgTypeEnum.All_Room_Info.getIntValue());

        sendMessageService.sendMessage(requestWaitQueue.getAppId(),requestWaitQueue.getUserId(),msg);
    }
}
