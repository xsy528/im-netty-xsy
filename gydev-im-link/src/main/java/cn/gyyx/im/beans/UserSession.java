package cn.gyyx.im.beans;

import lombok.Data;

/**
 * @author 邢少亚
 * @date 2024/2/23  18:27
 * @description 用户信息和通道关系,信息来源于首次握手后存储
 */
@Data
public class UserSession {

    /**
     * 站点信息，用户标识站点
     */
    private String appId;
    /**
     * 用户标识，首次通讯前从业务端获得
     */
    private String userId;

    /**
     * 用户设备，用户多端登录时使用
     */
    private String device;

    public String getDevice() {
        return device==null?"default":device;
    }
}
