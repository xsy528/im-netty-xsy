package cn.gyyx.im.destroy;

import cn.gyyx.im.utils.SessionUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @author 邢少亚
 * @date 2024/7/10  15:14
 * @description springboot关闭事件
 */
@Component
public class NettyDisposableEvent implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        SessionUtil.podDestroy();
    }
}
