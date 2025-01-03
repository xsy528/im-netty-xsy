package cn.gyyx.im.platform.impl;

import cn.gyyx.im.beans.vo.RoomInfo;
import cn.gyyx.im.platform.CallBackFactory;
import cn.gyyx.im.service.SendMessageService;
import cn.gyyx.im.utils.RoomUtil;
import cn.gyyx.im.utils.LoginUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 邢少亚
 * @date 2024/6/17  14:55
 * @description 用户登录状态service
 */
@Slf4j
@Service
public class UserLoginService {

    private final RedisTemplate<String, String> redisTemplate;
    private final CallBackFactory callBackFactory;
    private final SendMessageService sendMessageService;

    public UserLoginService(RedisTemplate<String, String> redisTemplate, CallBackFactory callBackFactory,
                            SendMessageService sendMessageService) {
        this.redisTemplate = redisTemplate;
        this.callBackFactory = callBackFactory;
        this.sendMessageService = sendMessageService;
    }

    public void login(String appId,String userId,String deviceId,String podName) {
        //登录
        LoginUserUtil.login(appId,userId,deviceId,podName,redisTemplate);

        //通知在线好友
        //获取所有有聊天的好友
//        PlatformCallBack platformCallBack = callBackFactory.getPlatformCallBack(appId);
//        List<String> chatList = platformCallBack.getChatList(userId);
//        if (CollectionUtils.isNotEmpty(chatList)) {
//
//        }

    }

    public void loginOut(String appId, String userId,String deviceId) {
        //通知在线好友
        //下线
        boolean allLoginOut = LoginUserUtil.loginOut(appId,userId,deviceId,redisTemplate);

        log.info("平台：{}，用户：{}，登出，是否全部下线：{}", appId, userId, allLoginOut);
//        if (allLoginOut) {
//            //获取所有有聊天的好友
//            PlatformCallBack platformCallBack = callBackFactory.getPlatformCallBack(appId);
//            List<String> chatList = platformCallBack.getChatList(userId);
//            if (CollectionUtils.isNotEmpty(chatList)) {
//                FriendLineStatusMsg returnMsgBean = new FriendLineStatusMsg();
//                returnMsgBean.setMsgType(MsgTypeEnum.Friends_Offline.getIntValue());
//                returnMsgBean.setIsOnline(false);
//                returnMsgBean.setFriendUserId(userId);
//
//                for (String friendUserId : chatList) {
//                    sendMessageService.sendMessage(appId, friendUserId, returnMsgBean);
//                }
//            }
//        }

        //判断如果在房间中，则推出房间
        RoomInfo roomInfo = RoomUtil.getRoomInfoByUser(appId, userId);
        if (roomInfo != null) {
            RoomUtil.destroyRoom(roomInfo.getRoomId());
        }
    }
}
