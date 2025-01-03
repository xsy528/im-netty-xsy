package cn.gyyx.im.wshandler;

import cn.gyyx.im.service.UserService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author 邢少亚
 * @date 2024/2/23  18:30
 * @description 心跳存活检查
 */
@Slf4j
public class IMIdleStateHandler extends IdleStateHandler {

    private int READER_IDLE_TIME;
    private final UserService userService;

    public IMIdleStateHandler(int readTimeout, UserService userService) {
        super(readTimeout, 0, 0, TimeUnit.SECONDS);
        READER_IDLE_TIME = readTimeout;
        this.userService = userService;
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) {
        log.info(READER_IDLE_TIME + "秒内未交互数据");
        // 读空闲 断开连接
        if (evt.state() == IdleState.READER_IDLE) {
            log.info("客户端长时间未和后端通讯，服务端断开链接");
            userService.loginOut(ctx);
            ctx.channel().close();
        }
    }
}
