package cn.gyyx.im.beans.entity;

import cn.gyyx.im.beans.EsDocument;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

@Data
public class ImChatLog extends EsDocument implements Serializable {

    @Id
    private Long code;

    private String receiver;

    private String sender;

    private String content;

    private String contentType;

    private Date createTime;

    private Boolean isDelete;

    private String platform;

}
