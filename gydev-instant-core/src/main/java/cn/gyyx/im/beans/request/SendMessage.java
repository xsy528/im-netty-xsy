package cn.gyyx.im.beans.request;

import cn.gyyx.im.beans.IBaseMsg;
import cn.gyyx.im.enums.ChatContentTypeEnum;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 邢少亚
 * @date 2024/2/24  11:39
 * @description 发送消息
 */
@Data
public class SendMessage extends IBaseMsg {
    /**
     * 接收人
     */
    private String receiveSign;
    /**
     * 接收人
     */
    private String receiveUserId;
    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息内容类型
     */
    private String contentType = ChatContentTypeEnum.TEXT.getType();

    /**
     * 发送时间 格式“yyyy-mm-dd hh:mm:ss
     */
    private String sendTime;

    /**
     * 消息id
     */
    private Integer messageId;


    public Date getSendTimeDate() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return format.parse(sendTime);
        }catch (Exception e){
            return new Date();
        }
    }
}
