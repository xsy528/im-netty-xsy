package cn.gyyx.im.beans.request;

import cn.gyyx.im.beans.IBaseMsg;
import lombok.Data;

/**
 * @author zxw
 * @Description: 撤回消息
 * @create 2024/3/26/002614:19
 * @Version 1.0
 **/
@Data
public class RetractMessage extends IBaseMsg {
    /**
     * 接收人
     */
    private String receiveUserId;
    /**
     * 消息id
     */
    private String messageId;

    /**
     * 发送时间 格式“yyyy-mm-dd hh:mm:ss
     */
    private String retractTime;
}
