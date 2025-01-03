package cn.gyyx.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 项目启动类
 * @author Administrator
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ScheduleApplication {

    public static void main(String[] args){
        SpringApplication.run(ScheduleApplication.class, args);
    }
}
