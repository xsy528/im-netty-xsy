package cn.gyyx.im.beans.response;

import cn.gyyx.im.beans.IBaseMsg;
import lombok.Data;

@Data
public class CurrentRoomInfoMsg extends IBaseMsg {

    private String roomId;

    private String creator;
    /**
     * 保留字段，用于存放附加，额外信息
     */
    private String info;
}
