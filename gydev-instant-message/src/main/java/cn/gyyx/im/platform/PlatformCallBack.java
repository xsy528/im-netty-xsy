package cn.gyyx.im.platform;

import cn.gyyx.im.beans.ResultData;
import cn.gyyx.im.service.BadWordService;
import com.alibaba.fastjson2.JSONObject;
import cn.gyyx.im.enums.PlatformEnum;

import java.util.List;

public interface PlatformCallBack {

    /**
     * 发送消息后回调
     */
    void sendMessageCallback(JSONObject param);


    PlatformEnum getPlatform();


    /**
     * 用户读取消息
     *
     * @param receiver  接收者
     * @param sender    发送者
     * @param appId     app ID
     * @param messageId 消息id
     */
    void userReadMessage(String appId, String receiver,String sender, Integer messageId);


    /**
     * 推送未读消息
     *
     * @param appId    app ID
     * @param receiver 接收者
     */
    default void pushUnreadMessage(String appId, String receiver) {}

    /**
     * 获取好友列表
     * @param userId
     * @return 好友id集合
     *
     */
    List<String> getChatList(String userId);


    /**
     * 敏感词校验
     *
     * @param badWordService 敏感词服务
     * @param content        内容
     * @param messageType    消息类型
     * @return {@link String}
     */
    String checkBadWord(BadWordService badWordService, String content, String messageType);

    /**
     * 视频相关的接口，包含视频用户初始化，用户能否视频检验等，若返回false则会导致流程中止
     * @param userId   用户id
     * @param userType   用户类型 根据平台业务自定义
     * @param operateType  步骤类型，初始化、视频等
     * @return true 业务流程正常，false 业务流程异常
     */
    default ResultData video(String userId, String userType, String operateType){
        return ResultData.success(null);
    }
}
