package cn.gyyx.im.beans.request;

import cn.gyyx.im.beans.IBaseMsg;
import lombok.Data;

@Data
public class RequestWaitQueue extends IBaseMsg {

    /**
     * 接受用户id
     */
    private String receiveUserId;

    private String role;
    /**
     * 房间号，如果角色是管理员，则必传
     */
    private String roomId;
    /**
     * 加密信息
     */
    private String sign;
}
