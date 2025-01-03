package cn.gyyx.im.video.handler;

import cn.gyyx.im.beans.request.AcceptVideoMessage;
import cn.gyyx.im.beans.vo.RoomInfo;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.service.SendMessageService;
import cn.gyyx.im.utils.RoomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FinishVideoHandler implements IMsgHandler<AcceptVideoMessage> {
    private final SendMessageService sendMessageService;

    public FinishVideoHandler(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Finish_Video;
    }

    @Override
    public void handler(AcceptVideoMessage acceptVideoMessage) {
        //断开视频，释放房间资源
        RoomInfo roomInfo = RoomUtil.getRoomInfoByUser(acceptVideoMessage.getAppId(), acceptVideoMessage.getUserId());
        if(roomInfo!=null) {
            //通知房间其他人解散房间
            roomInfo.getUserIds().remove(acceptVideoMessage.getUserId());
            for (String userId : roomInfo.getUserIds()) {
                acceptVideoMessage.setMsgType(MsgTypeEnum.Room_Disband_Info.getIntValue());
                sendMessageService.sendMessage(acceptVideoMessage.getAppId(),userId,acceptVideoMessage);
            }

            RoomUtil.destroyRoom(roomInfo.getRoomId());
        }

        //交换userId
        String requestUserId = acceptVideoMessage.getRequestUserId();
        acceptVideoMessage.setRequestUserId(acceptVideoMessage.getUserId());
        acceptVideoMessage.setUserId(requestUserId);
        acceptVideoMessage.setMsgType(MsgTypeEnum.Other_Refuse_Video.getIntValue());

        sendMessageService.sendMessage(acceptVideoMessage.getAppId(),requestUserId,acceptVideoMessage);
    }
}
