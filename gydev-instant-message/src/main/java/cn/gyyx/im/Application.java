package cn.gyyx.im;

import cn.gyyx.im.config.SpringSensitiveWordConfig;
import cn.gyyx.im.config.BusinessFactory;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.enums.PlatformEnum;
import cn.gyyx.im.service.IMsgHandler;
import cn.gyyx.im.platform.PlatformCallBack;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.Map;


/**
 * 项目启动类
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableFeignClients
public class Application {

    public static void main(String[] args){
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        //初始化敏感词
        SpringSensitiveWordConfig springSensitiveWordConfig = ctx.getBean(SpringSensitiveWordConfig.class);
        springSensitiveWordConfig.sensitiveWordBs();
    }

    @Bean
    public Map<MsgTypeEnum, IMsgHandler> getGameCommonServices() {
        return BusinessFactory.EXECUTE_HANDLER;
    }

    @Bean
    public  Map<PlatformEnum, PlatformCallBack> getPlatformCallBack() {
        return BusinessFactory.PLATFORM_HANDLER;
    }
}
