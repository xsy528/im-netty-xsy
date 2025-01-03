package cn.gyyx.im.service;

import cn.gyyx.im.beans.UserSession;
import cn.gyyx.im.utils.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final CallService callService;

    public UserService(CallService callService) {
        this.callService = callService;
    }

    /**
     * 用户登录
     * @param channelHandlerContext
     * @param userSession
     */
    public void login(ChannelHandlerContext channelHandlerContext, UserSession userSession){
        //登录
        SessionUtil.login(channelHandlerContext.channel());
        callService.login(userSession.getAppId(), userSession.getUserId(),userSession.getDevice());
    }

    /**
     * 用户断开链接
     * @param channelHandlerContext
     */
    public void loginOut(ChannelHandlerContext channelHandlerContext){
        //通知在线好友
        Channel channel = channelHandlerContext.channel();
        UserSession userSession = channel.attr(SessionUtil.SESSION).get();
        if(userSession!=null){
            //下线
            callService.logout(userSession.getAppId(), userSession.getUserId(),userSession.getDevice());
        }
        boolean allLoginOut = SessionUtil.loginOut(channel);

        log.info("用户：{}，登出，是否全部下线：{}",userSession,allLoginOut);
    }
}
