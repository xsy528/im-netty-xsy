package cn.gyyx.im.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 邢少亚
 * @date 2023/7/7  14:44
 */
@FeignClient(value = "im-service-gyyx-cn", url = "http://im-service-gyyx-cn:8081")
public interface IMFeign {

    /**
     * 调用业务处理登录
     * @param appId
     * @param userId
     * @param deviceId
     * @param podName
     */
    @GetMapping("/user/login")
    void login(@RequestParam("appId") String appId,@RequestParam("userId") String userId,
               @RequestParam("deviceId") String deviceId,@RequestParam("podName") String podName);

    /**
     * 调用业务处理登出
     * @param appId
     * @param userId
     * @param deviceId
     */
    @GetMapping("/user/loginOut")
    void loginOut(@RequestParam("appId") String appId,@RequestParam("userId") String userId,@RequestParam("deviceId") String deviceId);

    /**
     * 调用业务处理消息
     * @param appId
     * @param userId
     * @param deviceId
     * @param message
     */
    @PostMapping("message/handle")
    void handleMessage(@RequestParam("appId") String appId,@RequestParam("userId") String userId,
                              @RequestParam("deviceId") String deviceId,
                              @RequestBody String message);
}
