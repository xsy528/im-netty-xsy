package cn.gyyx.im.service.handler;

import cn.gyyx.im.beans.IBaseMsg;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.platform.impl.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zxw
 * @Description: 登出处理
 * @create 2024/3/21/002110:30
 * @Version 1.0
 **/
@Slf4j
@Component
public class LogOutHandler implements IMsgHandler {

    private final UserLoginService userLoginService ;

    public LogOutHandler(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }
    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Login_Out;
    }

    @Override
    public void handler(IBaseMsg iBaseMsg) {
        //注销合法链接
        log.info("平台：{},用户：{}，主动登出",iBaseMsg.getAppId(),iBaseMsg.getUserId());
        try {
            //执行注销操作
            userLoginService.loginOut(iBaseMsg.getAppId(),iBaseMsg.getUserId(),iBaseMsg.getDeviceId());
            log.info("平台：{},用户：{}，主动登出成功",iBaseMsg.getAppId(),iBaseMsg.getUserId());
        }catch (Exception e){
            log.info(e.getMessage(),e);
            log.info("断开连接失败：userId:{},info:{}",iBaseMsg.getUserId(), e);
        }
    }
}
