package cn.gyyx.im.service;

import cn.gyyx.im.beans.IBaseMsg;
import cn.gyyx.im.feign.LinkFeign;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendMessageService {
    private final LinkFeign feign;

    public SendMessageService(LinkFeign feign) {
        this.feign = feign;
    }

    public void sendMessage(String appId, String userId, IBaseMsg msg){
        //todo 多pod情况下的处理

        String s = feign.sendMessage(appId, userId, null, JSON.toJSONString(msg));
        log.info("踢登录返回：{}",s);
    }

    public void sendMessage(String appId, String userId, String deviceId, IBaseMsg msg){
        //todo 多pod情况下的处理

        String s = feign.sendMessage(appId, userId, deviceId, JSON.toJSONString(msg));
        log.info("发送消息返回：{}",s);
    }
}
