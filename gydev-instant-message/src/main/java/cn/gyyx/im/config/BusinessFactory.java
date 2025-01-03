package cn.gyyx.im.config;

import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.platform.PlatformCallBack;
import cn.gyyx.im.enums.PlatformEnum;
import cn.gyyx.im.service.IMsgHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 邢少亚
 * @date 2022/12/6  14:42
 * @description 根据业务类型选择不用的实现类
 */
@Component
public class BusinessFactory implements ApplicationContextAware {
    /**
     * 游戏类型实现，根据不同的游戏返回不同的游戏调用实现
     */
    public final static Map<MsgTypeEnum, IMsgHandler> EXECUTE_HANDLER = new HashMap<>(MsgTypeEnum.values().length);

    /**
     * 不同平台处理后回调
     */
    public final static Map<PlatformEnum, PlatformCallBack> PLATFORM_HANDLER = new HashMap<>(PlatformEnum.values().length);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, IMsgHandler> types2 = applicationContext.getBeansOfType(IMsgHandler.class);
        types2.values().forEach(e -> EXECUTE_HANDLER.putIfAbsent(e.getMsgType(), e));

        Map<String, PlatformCallBack> types1 = applicationContext.getBeansOfType(PlatformCallBack.class);
        types1.values().forEach(e -> PLATFORM_HANDLER.putIfAbsent(e.getPlatform(), e));

    }

}
