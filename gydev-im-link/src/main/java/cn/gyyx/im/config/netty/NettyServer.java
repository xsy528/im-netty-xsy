package cn.gyyx.im.config.netty;

import cn.gyyx.im.config.BusinessHandlerRegister;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NettyServer {
    
    @Value("${server.netty.port}")
    private int nettyPort;
    private ServerBootstrap bootstrap;

    /**
     * 主事件，负责连接
     */
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    /**
     * 业务处理线程
     */
    private final EventLoopGroup workGroup = new NioEventLoopGroup(2);
    private Channel channel;
    
    private final BusinessHandlerRegister register;

    public NettyServer(BusinessHandlerRegister register) {
        this.register = register;
    }

    private void open(int port) throws Exception {
        bootstrap = new ServerBootstrap();

        bootstrap
                .group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(register)
                // 等待处理的队列大小，
                .option(ChannelOption.SO_BACKLOG, 128)
                // 接收缓存区
                .option(ChannelOption.SO_RCVBUF,64*1024)
                // 表示连接保活，相当于心跳机制，默认为7200s
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        ;
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        log.info("netty 服务启动成功， 端口 = {}", port);
        channel = channelFuture.channel();
        
        //关闭监听服务器
        channel.closeFuture().sync();
    }
    
    
    public void start() {
        try {
            open(nettyPort);
        } catch (Exception e) {
            doClose();
        }

    }

    public void doClose() {
        log.info("服务器关闭......");
        // 关闭服务器连接
        try {
            if (channel != null) {
                channel.close();
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
        // 关闭连接到本服务器的连接
        // 关闭服务器
        try {
            if (bootstrap != null) {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
        log.info("服务器关闭完成");
    }
    
}
