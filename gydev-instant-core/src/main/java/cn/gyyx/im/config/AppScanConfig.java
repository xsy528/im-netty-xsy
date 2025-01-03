package cn.gyyx.im.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 邢少亚
 * @date 2022/11/29  16:04
 * @description springboot自动扫描
 */
@Configuration
@ComponentScan(basePackages = {"cn.gyyx.im"})
public class AppScanConfig {
}
