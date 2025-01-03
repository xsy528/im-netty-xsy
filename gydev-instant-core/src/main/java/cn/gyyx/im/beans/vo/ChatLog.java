package cn.gyyx.im.beans.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zxw
 * @Description: 返回前端聊天记录实体
 * @create 2024/4/10/001017:29
 * @Version 1.0
 **/
@Data
public class ChatLog implements Serializable {
    /** 消息id */
    private Long messageId;
    /** 接收人id */
    private String receiver;
    /** 发送人id */
    private String sender;
    /** 消息内容 */
    private String content;
    /** 时间 */
    private String createTime;
}
