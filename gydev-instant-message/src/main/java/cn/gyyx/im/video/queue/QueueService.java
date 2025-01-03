package cn.gyyx.im.video.queue;

import cn.gyyx.im.beans.QueueData;

import java.util.List;

/**
 * 队列抽象接口，第一版由于采用数据库实现队列的方式，因而采用抽象类的方式，方便后续使用其他途径实现队列
 */
public interface QueueService {

    /**
     * 入队列
     * @param data
     * @return
     */
    boolean addQueue(QueueData data);

    /**
     * 出队列
     * @return
     */
    QueueData outQueue(String appId,String receiveUserId);

    /**
     * 所有队列信息
     * @param appId
     * @return
     */
    List<QueueData> allQueue(String appId);

    /**
     * 离开排队
     * @param appId
     * @param userId
     * @return
     */
    boolean quitWait(String appId, String userId);
}
