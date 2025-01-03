package cn.gyyx.im.beans.response;

import cn.gyyx.im.beans.IBaseMsg;
import lombok.Data;

@Data
public class FriendLineStatusMsg extends IBaseMsg {

    /**
     * 是否是在线 true上线 ，false下线
     */
    private Boolean isOnline;

    private String friendUserId;
}
