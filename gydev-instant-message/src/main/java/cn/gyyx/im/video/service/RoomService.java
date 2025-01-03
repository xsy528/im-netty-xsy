package cn.gyyx.im.video.service;
import cn.gyyx.im.beans.IBaseMsg;
import cn.gyyx.im.beans.response.WaitQueueInfoMsg;
import cn.gyyx.im.service.SendMessageService;
import com.alibaba.fastjson2.JSON;

import cn.gyyx.aoplog.utils.SnowflakeIdWorker;
import cn.gyyx.im.beans.QueueData;
import cn.gyyx.im.beans.vo.RoomInfo;
import cn.gyyx.im.beans.response.CurrentRoomInfoMsg;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.utils.RoomUtil;
import cn.gyyx.im.video.queue.QueueService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final QueueService queueService;
    private final SendMessageService sendMessageService;
    private final SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0);

    /**
     * 添加队列
     * @param appId
     * @param userId
     * @return
     */
    public boolean addWait(String appId,String userId) {
        QueueData data = new QueueData();
        data.setAppId(appId);
        data.setUserId(userId);
        return queueService.addQueue(data);
    }

    /**
     * 添加队列
     * @param appId
     * @param userId
     * @return
     */
    public boolean quitWait(String appId,String userId) {
        return queueService.quitWait(appId,userId);
    }

    /**
     * 出队列
     * @param appId
     * @return
     */
    public QueueData reduceWait(String appId,String receiveUserId) {
        QueueData queueData = queueService.outQueue(appId,receiveUserId);
        if(queueData!=null){
            //通知所有排队的人
            notifyAllWait(appId);
        }
        return queueData;
    }

    /**
     * 通知所有在等待的人群
     * @param appId
     */
    public void notifyAllWait(String appId){
        List<QueueData> queueData = queueService.allQueue(appId);
        if(CollectionUtils.isNotEmpty(queueData)){
            long currentTimeMillis = System.currentTimeMillis();
            queueData.stream().sorted(Comparator.comparing(QueueData::getIndex)).forEach(data->{
                //通知
                WaitQueueInfoMsg queue = new WaitQueueInfoMsg();
                queue.setWaitTotal(queueData.size());
                queue.setWaitIndex(data.getIndex());
                queue.setMsgType(MsgTypeEnum.All_Room_Info.getIntValue());
                queue.setAppId(appId);
                queue.setUserId(data.getUserId());
                queue.setTimestamp(currentTimeMillis);
                sendMessageService.sendMessage(appId,data.getUserId(),queue);
            });
        }
    }

    private String getRoomId(){
        String roomId = String.valueOf(snowflakeIdWorker.nextId());
        //检查roomId是否加锁
        boolean exist = RoomUtil.checkRoom(roomId);
        //如若加锁则重新获取roomId
        if(exist){
            return getRoomId();
        }
        return roomId;
    }

    /**
     * 创建房间
     * @return
     */
    public boolean createRoom(String appId,String creator,String... user) {
        //获取roomId
        String roomId = getRoomId();

        //对房间加锁，并注册房间内用户
        RoomInfo info = new RoomInfo();
        info.setRoomId(roomId);
        info.setAppId(appId);
        info.setCreator(creator);
        List<String> temp = Arrays.asList(user);
        List<String> userList = new ArrayList<>(temp);
        userList.add(creator);
        info.setUserIds(userList);

        RoomUtil.addRoom(info);

        //通知房间内所有人
        CurrentRoomInfoMsg returnMsg = new CurrentRoomInfoMsg();
        returnMsg.setRoomId(roomId);
        returnMsg.setCreator(creator);
        returnMsg.setInfo(JSON.toJSONString(userList));
        returnMsg.setMsgType(MsgTypeEnum.Current_Room_Info.getIntValue());
        returnMsg.setAppId(appId);
        returnMsg.setUserId(creator);
        returnMsg.setTimestamp(System.currentTimeMillis());

        sendMessageService.sendMessage(appId,creator,returnMsg);
        if(user!=null && user.length>0){
            for(String userId : user){
                returnMsg.setUserId(userId);
                sendMessageService.sendMessage(appId,userId,returnMsg);
            }
        }

        return true;
    }

    /**
     * 获取房间信息
     * @param appId
     * @param roomId
     * @return
     */
    public RoomInfo getRoomInfo(String appId, String roomId) {
        return RoomUtil.getRoomInfo(roomId);
    }

    public List<RoomInfo> getAllRoomInfo(String appId) {
        List<RoomInfo> allRoomInfo = RoomUtil.getAllRoomInfo();
        if(allRoomInfo==null){
            return new ArrayList<>(0);
        }
        return allRoomInfo.stream().filter(RoomInfo->appId.equals(RoomInfo.getAppId())).collect(Collectors.toList());
    }

    /**
     * 加入房间
     * @param roomInfo
     * @param userId
     * @return
     */
    public boolean addRoom(RoomInfo roomInfo, String userId) {
        //进入房间
        roomInfo.getUserIds().add(userId);

        //发送进入房间消息
        CurrentRoomInfoMsg returnMsg = new CurrentRoomInfoMsg();
        returnMsg.setRoomId(roomInfo.getRoomId());
        returnMsg.setCreator(roomInfo.getCreator());
        returnMsg.setInfo(JSON.toJSONString(roomInfo.getUserIds()));
        returnMsg.setMsgType(MsgTypeEnum.Current_Room_Info.getIntValue());
        returnMsg.setAppId(roomInfo.getAppId());
        returnMsg.setUserId(userId);
        returnMsg.setTimestamp(System.currentTimeMillis());

        sendMessageService.sendMessage(roomInfo.getAppId(),userId,returnMsg);

        return true;
    }

    public List<String> leaveRoom(String appId,String userId){
        RoomInfo roomInfo = RoomUtil.getRoomInfoByUser(appId, userId);
        if(roomInfo != null) {
            //删除用户自身
            roomInfo.getUserIds().remove(userId);

            if(roomInfo.getUserIds().size()==0){
                //如果房间没人，则自动删除房间
                RoomUtil.destroyRoom(roomInfo.getRoomId());
            }
        }
        return roomInfo.getUserIds();
    }
}
