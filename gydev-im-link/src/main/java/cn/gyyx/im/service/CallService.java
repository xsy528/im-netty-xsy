package cn.gyyx.im.service;

import org.springframework.scheduling.annotation.Async;

/**
 * 调用业务处理，使用接口，后续支持其他实现
 */
@Async
public interface CallService {

    /**
     * 登录事件
     * @param appId
     * @param userId
     */
    void login(String appId,String userId,String deviceId);

    /**
     * 登出事件
     * @param appId
     * @param userId
     */
    void logout(String appId,String userId,String deviceId);

    /**
     * 发送消息
     * @param message
     */
    void handleMsg(String appId, String userId,String deviceId,String message);
}
