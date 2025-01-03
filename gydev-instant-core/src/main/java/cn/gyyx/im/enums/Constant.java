package cn.gyyx.im.enums;

/**
 * @author zxw
 * @Description:常量
 * @create 2024/4/17/001714:01
 * @Version 1.0
 **/
public class Constant {
    //在线状态
    public static final String ONLINE_STATUS = "ONLINE_STATUS:";

    //每日在线状态
    public static final String DAY_LOGIN_STATUS = "DAY_LOGIN_STATUS:";
    //聊天权限状态
    public static final String CHAT_PERMISSION_STATUS="CHAT_PERMISSION_STATUS:";

    /**
     * 每日最大在线人数
     */
    public static final String IM_MAX_ONLINE_NUMBER = "IM_MAX_ONLINE_NUMBER";

    /**
     * 每日最大在线设备数
     */
    public static final String IM_MAX_DEVICE_NUMBER = "IM_MAX_DEVICE_NUMBER";

    /**
     * 每日总发送数量
     */
    public static final String IM_DAY_CHAT_NUMBER = "IM_DAY_CHAT_NUMBER";

    /**
     * 每日总链接次数
     */
    public static final String IM_TOTAL_LINK_NUMBER = "IM_TOTAL_LINK_NUMBER";

    /**
     * 每日总使用人数
     */
    public static final String IM_TOTAL_USE_NUMBER = "IM_TOTAL_USE_NUMBER";



    public static String getOnlineKey(String key){
        return ONLINE_STATUS+key;
    }

    public static String getDayLoginKey(String key){
        return DAY_LOGIN_STATUS+key;
    }

    public static String getChatPermissionKey(String appId,String receiver,String sender){
        return CHAT_PERMISSION_STATUS+appId+receiver+sender;
    }
}
