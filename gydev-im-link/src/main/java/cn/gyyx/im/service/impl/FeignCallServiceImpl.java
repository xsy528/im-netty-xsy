package cn.gyyx.im.service.impl;

import cn.gyyx.im.config.ImConfig;
import cn.gyyx.im.feign.IMFeign;
import cn.gyyx.im.service.CallService;
import org.springframework.stereotype.Service;

@Service
public class FeignCallServiceImpl implements CallService {
    private final IMFeign feign;
    private final ImConfig imConfig;

    public FeignCallServiceImpl(IMFeign feign, ImConfig imConfig) {
        this.feign = feign;
        this.imConfig = imConfig;
    }

    @Override
    public void login(String appId, String userId,String deviceId) {
        feign.login(appId, userId, deviceId,imConfig.getPodName());
    }

    @Override
    public void logout(String appId, String userId,String deviceId) {
        feign.loginOut(appId,userId,deviceId);
    }

    @Override
    public void handleMsg(String appId, String userId,String deviceId,String message) {
        feign.handleMessage(appId, userId, deviceId, message);
    }
}
