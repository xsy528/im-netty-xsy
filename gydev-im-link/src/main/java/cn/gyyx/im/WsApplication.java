package cn.gyyx.im;

import cn.gyyx.im.config.netty.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 项目启动类
 */
@SpringBootApplication
@EnableFeignClients
public class WsApplication {

    public static void main(String[] args){
        ConfigurableApplicationContext ctx = SpringApplication.run(WsApplication.class, args);
        NettyServer nettyServer = ctx.getBean(NettyServer.class);
        nettyServer.start();
    }
}
