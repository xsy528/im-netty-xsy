package cn.gyyx.im.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 探针
 * @author leeway
 * @date 2023/9/13/013 15:09
 */
@RestController
@RequestMapping("/")
@Slf4j
public class HealthController {


    @GetMapping("/health")
    public void health() {
    }
}
