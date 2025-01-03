package cn.gyyx.im.platform.impl;

import cn.gydev.lib.bean.ResultBean;
import cn.gydev.lib.utils.JsonUtils;

import cn.gyyx.im.beans.ResultData;
import cn.gyyx.im.enums.PlatformEnum;
import cn.gyyx.im.platform.feign.DaoUserFeign;
import cn.gyyx.im.platform.PlatformCallBack;
import cn.gyyx.im.service.BadWordService;
import cn.gyyx.im.service.NewChatLogService;
import cn.gyyx.im.service.RedisService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class LunTanPlatformCallBack implements PlatformCallBack {
    private final DaoUserFeign daoUserFeign;

    private final NewChatLogService newChatLogService;

    private final RedisService redisService;

    public LunTanPlatformCallBack(DaoUserFeign daoUserFeign, NewChatLogService newChatLogService, RedisService redisService) {
        this.daoUserFeign = daoUserFeign;
        this.newChatLogService = newChatLogService;
        this.redisService = redisService;
    }

    @Override
    @Async
    public void sendMessageCallback(JSONObject param) {
        //通知论坛
        log.info("开始回调论坛");
        ResponseEntity<Object> objectResponseEntity = daoUserFeign.gyReceive(param);
        log.info("回调结果为:{}", JsonUtils.objToJson(objectResponseEntity));
    }

    @Override
    public PlatformEnum getPlatform() {
        return PlatformEnum.LUNTAN;
    }

    @Override
    public void userReadMessage(String appId, String receiver, String sender, Integer messageId) {
        // 消息已读
        String unreadKey = newChatLogService.getUnreadMessageCacheKey(appId, receiver, sender);
        redisService.del(unreadKey);
    }

    @Override
    public List<String> getChatList(String userId) {
        ResultBean<List<String>> chatList = daoUserFeign.getChatList(Integer.valueOf(userId));
        return chatList.getData();
    }

    @Override
    public String checkBadWord(BadWordService badWordService, String content, String messageType) {
        return badWordService.replaceBadWord(content, messageType);
    }

    private List<String> customList = new ArrayList<>();
    @Override
    public ResultData video(String userId, String userType, String operateType) {
        switch (operateType){
            case "login":
                if("kefu".equals(userType)){
                    customList.add(userId);
                    return ResultData.success("添加客服成功");
                }
            case "video":
                if(customList.size()>0){
                    String customer = customList.get(0);
                    log.info("客服：{}，进入工作", customer);
                    customList.remove(0);
                    return ResultData.success(customer);
                }
                return ResultData.fail("添加客服成功");
            case "finishVideo":
                if("kefu".equals(userType)){
                    customList.add(userId);
                    return ResultData.success("添加客服成功");
                }
            case "loginOut":
                if("kefu".equals(userType)){
                    customList.remove(userId);
                    return ResultData.success("客服登出成功");
                }
        }

        return PlatformCallBack.super.video(userId,userType,operateType);
    }


}
