package cn.gyyx.im.platform;

import cn.gyyx.im.enums.PlatformEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 回调服务
 *
 * @author yuanshuai
 * @since 2024/7/5 14:28
 */
@Service
public class CallBackFactory {


    private final Map<PlatformEnum, PlatformCallBack> callBackMap;

    public CallBackFactory(Map<PlatformEnum, PlatformCallBack> callBackMap) {
        this.callBackMap = callBackMap;
    }


    /**
     * 获得回调实现
     *
     * @param appId app ID
     * @return {@link PlatformCallBack}
     */
    public PlatformCallBack getPlatformCallBack(String appId) {
        if (StringUtils.isBlank(appId)) {
            return null;
        }
        PlatformEnum platformEnum = PlatformEnum.searchString(appId);
        if (null == platformEnum) {
            return null;
        }

        return callBackMap.get(platformEnum);
    }
}
