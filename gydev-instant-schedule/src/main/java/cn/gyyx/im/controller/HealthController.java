package cn.gyyx.im.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 存活状态入口
 * @date 2021/7/6/006 10:34
 */
@RestController
@RequestMapping("/")
@RefreshScope
@Slf4j
@CrossOrigin
public class HealthController {
    @GetMapping("/health")
    public String health() {
        return "成功";
    }
}
