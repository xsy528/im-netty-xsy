package cn.gyyx.im.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "ws-link-gyyx-cn", url = "http://ws-link-gyyx-cn:8081")
public interface LinkFeign {
    @PostMapping("/link/outLogin")
    String outLogin(@RequestParam("appId") String appId, @RequestParam("userId") String userId,
                         @RequestParam(value = "deviceId",required = false) String deviceId);

    @PostMapping("/link/sendMessage")
    String sendMessage(@RequestParam("appId") String appId, @RequestParam("userId") String userId,
                            @RequestParam(value = "deviceId",required = false) String deviceId,
                            @RequestBody String message);
}
