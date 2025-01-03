package cn.gyyx.im.video.queue.impl;

import cn.gyyx.im.beans.QueueData;
import cn.gyyx.im.beans.entity.ChatQueue;
import cn.gyyx.im.dao.ChatQueueDao;
import cn.gyyx.im.utils.DateTimeUnit;
import cn.gyyx.im.utils.DateTimeUtil;
import cn.gyyx.im.video.queue.QueueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DBQueueServiceImpl implements QueueService {
    private final ChatQueueDao chatQueueDao;

    public DBQueueServiceImpl(ChatQueueDao chatQueueDao) {
        this.chatQueueDao = chatQueueDao;
    }

    @Override
    public boolean addQueue(QueueData data) {
        ChatQueue lastWait = chatQueueDao.getLastWait(data.getUserId(), data.getAppId());
        if(lastWait!=null && lastWait.getFinishTime()!=null && DateTimeUtil.betweenTime(new Date(),lastWait.getFinishTime(), DateTimeUnit.ONE_MINUTE)<5){
            //5分钟之内有等待的队列，则从五分钟前开始等待
            Integer i = chatQueueDao.waitQueueAgain(data.getUserId(), data.getAppId());
            return i > 0;
        }else {
            chatQueueDao.changeUserWaitStatus(data.getUserId(), data.getAppId());
            Integer i = chatQueueDao.insertChatQueue(data.getUserId(), data.getAppId());
            return i > 0;
        }
    }

    @Override
    public QueueData outQueue(String appId,String receiveUserId) {
        ChatQueue oneQueue;
        if(StringUtils.isNotEmpty(receiveUserId)){
            oneQueue = chatQueueDao.getOldUserQueue(appId,receiveUserId);
        }else {
            oneQueue = chatQueueDao.getOneQueue(appId);
        }
        if(oneQueue==null){
            return new QueueData();
        }

        Integer i = chatQueueDao.updateChatQueue(1, oneQueue.getCode());

        QueueData data = new QueueData();
        data.setAppId(oneQueue.getPlatform());
        data.setUserId(oneQueue.getUserId());
        data.setIndex(1);

        return data;
    }

    @Override
    public List<QueueData> allQueue(String appId) {
        List<ChatQueue> allQueue = chatQueueDao.getAllQueue(appId);
        List<QueueData> result = new ArrayList<>(allQueue.size());
        for (int i = 0;i<allQueue.size();i++) {
            ChatQueue chatQueue = allQueue.get(i);
            QueueData data = new QueueData();
            data.setAppId(chatQueue.getPlatform());
            data.setUserId(chatQueue.getUserId());
            data.setIndex(i+1);

            result.add(data);
        }

        return result;
    }

    @Override
    public boolean quitWait(String appId, String userId) {
        Integer i = chatQueueDao.quitQueue(userId, appId, 2);
        return i>0;
    }
}
