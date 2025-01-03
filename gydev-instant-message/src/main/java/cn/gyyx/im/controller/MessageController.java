package cn.gyyx.im.controller;

import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.platform.impl.UserLoginService;
import cn.gyyx.im.service.IMsgHandler;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/message")
@Slf4j
@RequiredArgsConstructor
public class MessageController {

    private final UserLoginService userLoginService;
    private final Map<MsgTypeEnum, IMsgHandler> services;

    /**
     * 处理消息
     * @param message
     */
    @PostMapping("/handle")
    public void handleMessage(@RequestParam("appId") String appId,@RequestParam("userId") String userId,
                              @RequestParam(value = "deviceId",required = false) String deviceId,
                              @RequestBody String message) {
        try {
            log.info("开始处理消息，内容：{}", message);
            JSONObject jsonObject = JSON.parseObject(message);
            Integer msgType = jsonObject.getInteger("msgType");
            if(msgType==null){
                log.info("协议错误！");
                userLoginService.loginOut(appId,userId,deviceId);
            }
            MsgTypeEnum search = MsgTypeEnum.search(msgType);
            if(search!=null){
                //传递到下一个业务handler
                IMsgHandler service = services.get(search);
                service.handler(JSON.parseObject(message, search.getClassType()));
            }
        }catch (Exception e){
            log.error("消息转换异常e:{}，消息内容：{}",e,message);
            log.error(e.getMessage(),e);
            userLoginService.loginOut(appId,userId,deviceId);
        }
    }
}
