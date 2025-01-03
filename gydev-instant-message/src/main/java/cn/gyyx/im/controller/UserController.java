package cn.gyyx.im.controller;

import cn.gydev.lib.bean.ResultBean;
import cn.gyyx.im.enums.PlatformEnum;
import cn.gyyx.im.enums.VersionConstant;
import cn.gyyx.im.platform.impl.UserLoginService;
import cn.gyyx.im.video.enums.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Value("${login.key}")
    private String key;

    private final UserLoginService userLoginService;

    public UserController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @GetMapping("/login")
    public void login(String appId, String userId, String deviceId,String podName) {
        userLoginService.login(appId,userId,deviceId,podName);
    }

    @GetMapping("/loginOut")
    public void loginOut(String appId, String userId, String deviceId) {
        userLoginService.loginOut(appId,userId,deviceId);
    }

    /**
     * 用户登陆
     * @param userId 用户信息
     * @return
     */
    @RequestMapping("/imLoginSign")
    public ResultBean<Object> imSign(String platform, String userId) {
        //加密信息，并返回im系统登录url
        String msgType = "10";

        PlatformEnum platformEnum = PlatformEnum.searchByPlatform(platform);
        if(platformEnum==null){
            return ResultBean.paramError("平台未接入");
        }
        String appId = platformEnum.getAppid();
        Long timeStamp = System.currentTimeMillis();
        String loginId = UUID.randomUUID().toString();
        String sign= DigestUtils.md5DigestAsHex((msgType+appId+userId+loginId+timeStamp+key).getBytes());

        Map<String, Object> result =new HashMap<>(8);
        result.put("appId",appId);
        result.put("msgType",msgType);
        result.put("sign",sign);
        result.put("txNo",timeStamp);
        result.put("timestamp",0);
        result.put("userType",Integer.valueOf(userId)%2==0? RoleType.NormalUser.getRole():RoleType.CustomUser.getRole());
        result.put("userId",userId);
        result.put("loginId",loginId);
        result.put("version", VersionConstant.version);
        return ResultBean.success("成功",result);
    }

    public static void main(String[] args) {
        String msgType = "10";
        String userId = "001";
        String key = "MZc9ZQcHkk9TyCAdTmNJYdC0PP8yzXGf";
        String appId = PlatformEnum.KEFU.getAppid();
        Long timeStamp = System.currentTimeMillis();
        String loginId = UUID.randomUUID().toString();
        String sign= DigestUtils.md5DigestAsHex((msgType+appId+userId+loginId+timeStamp+key).getBytes());
        System.out.println("wss://wslink.gyyx.cn/login?appId="+appId+"&msgType=10&version=1&userId="+userId+"&loginId="+loginId
                +"&sign="+sign+"&txNo="+timeStamp);
    }
}
