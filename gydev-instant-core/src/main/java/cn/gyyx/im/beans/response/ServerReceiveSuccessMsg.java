package cn.gyyx.im.beans.response;

import cn.gyyx.im.beans.IBaseMsg;
import lombok.Data;

@Data
public class ServerReceiveSuccessMsg extends IBaseMsg {

    private Integer receiveMsgType;
}
