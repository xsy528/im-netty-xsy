package cn.gyyx.im.wshandler;

import cn.gyyx.im.beans.UserSession;
import cn.gyyx.im.service.UserService;
import cn.gyyx.im.utils.SessionUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 初始化 channel时做的一些事情
 * 线程安全的
 * @author guohai
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class InitChannelHandler extends ChannelInboundHandlerAdapter {

    private final UserService userService;

    public InitChannelHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    /**
     * 当有客户端注册时调用
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ChannelConfig config = ctx.channel().config();
        DefaultSocketChannelConfig socketConfig = (DefaultSocketChannelConfig)config;
        // 此处三个参数决定 延迟情况
        // 连接时间 、往返延迟、 带宽。
        // 这三个参数设置的是权重
        // 因为我的连接会保持住 长连接不会频繁断开，所以 把连接时间权限设置的最低为0
        // 因为我们对往返延迟有一些容忍度，所以 第二参数是1
        // 对于带宽我们会有更大的需求，第三个参数设置为2 这就是目前的权重比
        // 延迟和带宽的性能是互斥的 , 延迟低 , 就意味着很小的包就要发送一次 , 其带宽就低了 , 延迟高了 , 每次积累很多数据才发送 , 其带宽就相应的提高了
        socketConfig.setPerformancePreferences(0,1,2);
        // NioSocketChannel在工作过程中，使用PooledByteBufAllocator来分配内存
        socketConfig.setAllocator(PooledByteBufAllocator.DEFAULT);
        super.channelRegistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        UserSession userSession = ctx.attr(SessionUtil.SESSION).get();
        log.info("异常事件：{}",userSession);
        userService.loginOut(ctx);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        UserSession userSession = ctx.attr(SessionUtil.SESSION).get();
        if(userSession==null){
            return;
        }
        log.info("用户：{}，主动关闭浏览器通道",userSession);
        userService.loginOut(ctx);
        super.channelInactive(ctx);
    }
}
