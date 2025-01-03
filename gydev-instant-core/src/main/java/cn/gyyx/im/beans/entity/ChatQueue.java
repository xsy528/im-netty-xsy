package cn.gyyx.im.beans.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ChatQueue {
    private Integer code;

    private String platform;

    private String userId;

    private Date createTime;

    private Date finishTime;

    private String receiver;

    private Short status;
}
