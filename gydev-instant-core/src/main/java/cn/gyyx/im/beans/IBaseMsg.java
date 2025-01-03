package cn.gyyx.im.beans;

import cn.gyyx.im.enums.VersionConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 邢少亚
 * @date 2024/2/23  17:43
 * @description 所有协议父类
 */
@Data
public class IBaseMsg implements Serializable {

    /**
     * 版本信息
     */
    private Integer version = VersionConstant.version;
    /**
     * 消息类型
     */
    private Integer msgType;
    /**
     * 站点信息，用户标识站点
     */
    private String appId;
    /**
     * 用户标识，首次通讯前从业务端获得
     */
    private String userId;
    /**
     * 设备标识，用户多用户登录
     */
    private String deviceId;
    /**
     * 13位时间戳，可用于标识本次请求,单位：秒
     */
    private Long timestamp;
    /**
     * 消息唯一标识
     */
    private String unique;
    /**
     * 备用字段，这个字段后端处理完会原样返回前端，用于前端特定需求的标识
     */
    private String mark;

    /**
     * 参数校验
     * @return
     */
    public boolean checkParam(){
        return true;
    }

    public void convert(IBaseMsg msg){
        this.version = msg.getVersion();
        this.msgType = msg.getMsgType();
        this.appId = msg.getAppId();
        this.userId = msg.getUserId();
        this.timestamp = msg.getTimestamp();
        this.unique = msg.getUnique();
        this.mark = msg.getMark();
    }
}
