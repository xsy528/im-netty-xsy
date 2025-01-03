package cn.gyyx.im.beans.response;

import cn.gyyx.im.beans.IBaseMsg;
import lombok.Data;

/**
 * @author zxw
 * @Description:返回前端消息体
 * @create 2024/3/21/002110:48
 * @Version 1.0
 **/
@Data
public class ReturnMsgBean extends IBaseMsg {

    public ReturnMsgBean(IBaseMsg baseMsg) {
        setMark(baseMsg.getMark());
    }

    public ReturnMsgBean() {
    }

    /**
     * 13位时间戳，可用于标识本次请求
     */
    private long txNo;
    /**
     4/10000
     实时翻译
     4/10000 real-time translation block messages

     划译
     Block messages
     * 消息id(撤回消息时使用)
     */
    private Long messageId;
    /**
     * 发送人
     */
    private String sendUserId;
    /**
     * 接收人
     */
    private String receiveUserId;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 发送消息结果
     */
    private String result;

    /**
     * 发送时间 格式“yyyy-mm-dd hh:mm:ss
     */
    private String sendTime;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 服务器时间
     */
    private long serverTime;
}
