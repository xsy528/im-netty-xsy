package cn.gyyx.im.platform.feign;

import cn.gydev.lib.bean.ResultBean;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zxw
 * @Description: 论坛远程调用
 * @create 2024/6/7/000714:30
 * @Version 1.0
 **/
@FeignClient(value = "interface.userwd.gyyx.cn", url = "http://interface-userwd-gyyx-cn:8080")
public interface DaoUserFeign {

    @GetMapping("/gyyxIM/gyimcallback")
    ResponseEntity<Object> gyReceive(JSONObject params);

    @GetMapping("/gyyxIM/chatList")
    ResultBean<List<String>> getChatList(@RequestParam("id")Integer id);
}