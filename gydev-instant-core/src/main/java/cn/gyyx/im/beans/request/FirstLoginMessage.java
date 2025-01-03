package cn.gyyx.im.beans.request;

import cn.gyyx.im.beans.IBaseMsg;
import lombok.Data;

@Data
public class FirstLoginMessage extends IBaseMsg {

    private String url;
}
