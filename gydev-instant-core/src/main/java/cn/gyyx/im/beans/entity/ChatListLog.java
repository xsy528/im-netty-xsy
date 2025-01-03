package cn.gyyx.im.beans.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ChatListLog {

    private Integer code;

    private String platform;

    private String sender;

    private String receiver;

    private Date createTime;

    private Date updateTime;

    private Boolean isDelete;
}
