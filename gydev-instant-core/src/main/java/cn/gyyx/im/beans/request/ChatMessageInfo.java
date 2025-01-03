package cn.gyyx.im.beans.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 聊天消息info
 *
 * @author yuanshuai
 * @since 2024/6/22 15:37
 */
@Data
public class ChatMessageInfo implements Serializable {


    /**
     * app ID
     */
    private String appId;

    /**
     * 发送者ID
     */
    private String sender;

    /**
     * 接收者ID
     */
    private String receiver;

    /**
     * 内容
     */
    private String content;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 消息类型
     */
    private String contentType;

    /**
     * 用户是否在线
     */
    private Boolean online;

}
