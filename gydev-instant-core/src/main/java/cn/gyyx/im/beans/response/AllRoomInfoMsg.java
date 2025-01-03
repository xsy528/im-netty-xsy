package cn.gyyx.im.beans.response;

import cn.gyyx.im.beans.IBaseMsg;
import cn.gyyx.im.beans.vo.RoomInfo;
import lombok.Data;

import java.util.List;

@Data
public class AllRoomInfoMsg extends IBaseMsg {

    private Integer total;

    private List<RoomInfo> roomInfoMsgs;

}
