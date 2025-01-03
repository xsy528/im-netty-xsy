package cn.gyyx.im.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum PlatformEnum {

    LUNTAN("1477358668","luntan",true,3,2,"论坛"),

    DAILIAN("0000000001","dailian",true,3,2,"代练"),

    JIASHIJI("1000000002","jiashiji",true,3,1440,"家事记"),

    KEFU("0000000002","kefu",false,0,1440,"家事记"),
    ;
    private String appid;

    private String platform;

    /**
     * 是否支持多端登录
     */
    private boolean isAllowMultiLogin;

    /**
     * 支持多端登录最大数量
     */
    private int allowMultiLoginCount;

    /**
     * 聊天签名过期时间，单位：分钟
     */
    private int chatExpireTime;

    private String desc;

    public static PlatformEnum searchString(String appid) {
        Optional<PlatformEnum> findFirst = Arrays.stream(PlatformEnum.values())
                .filter(p -> String.valueOf(p.getAppid()).equals(appid))
                .findFirst();
        return findFirst.orElse(null);
    }

    public static PlatformEnum searchByPlatform(String platform) {
        Optional<PlatformEnum> findFirst = Arrays.stream(PlatformEnum.values())
                .filter(p -> String.valueOf(p.getPlatform()).equals(platform))
                .findFirst();
        return findFirst.orElse(null);
    }
}
