package cn.gyyx.im.beans.request;

import cn.gyyx.im.beans.IBaseMsg;
import lombok.Data;

@Data
public class AcceptVideoMessage extends IBaseMsg {

    private String message;

    private String requestUserId;
}
