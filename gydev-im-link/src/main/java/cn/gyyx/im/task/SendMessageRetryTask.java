package cn.gyyx.im.task;

import cn.gyyx.im.utils.SessionUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 消息重试类
 */
@Configuration
@EnableScheduling
public class SendMessageRetryTask {

    @Scheduled(fixedRate = 5000)
    private void retrySendMessage(){
        SessionUtil.retrySendMessage();
    }
}
