package cn.gyyx.im.video.handler;

import cn.gyyx.im.beans.IBaseMsg;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import cn.gyyx.im.video.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class QuitRoomHandler implements IMsgHandler<IBaseMsg> {
    private final RoomService roomService;
    private final SendMessageService sendMessageService;

    public QuitRoomHandler(RoomService roomService, SendMessageService sendMessageService) {
        this.roomService = roomService;
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Request_Quit_Room;
    }

    @Override
    public void handler(IBaseMsg iBaseMsg) {
        //todo 用户离开房间/退出排队
        roomService.quitWait(iBaseMsg.getAppId(), iBaseMsg.getUserId());
        List<String> leaveUser = roomService.leaveRoom(iBaseMsg.getAppId(), iBaseMsg.getUserId());
        for (String other : leaveUser) {
            iBaseMsg.setMsgType(MsgTypeEnum.Quit_Room.getIntValue());
            sendMessageService.sendMessage(iBaseMsg.getAppId(), other, iBaseMsg);
        }
    }
}
