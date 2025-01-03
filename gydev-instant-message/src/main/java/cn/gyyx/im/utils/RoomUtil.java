package cn.gyyx.im.utils;

import cn.gyyx.im.beans.vo.RoomInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 房间工具类
 */
public class RoomUtil {

    private static Map<String,RoomInfo> roomMap = new ConcurrentHashMap<>();

    public static void addRoom(RoomInfo info){
        roomMap.put(info.getRoomId(),info);
    }

    public static void destroyRoom(String roomId){
        roomMap.remove(roomId);
    }

    public static boolean checkRoom(String roomId){
        return roomMap.containsKey(roomId);
    }

    public static RoomInfo getRoomInfoByUser(String appid,String userId){
        return roomMap.values().stream().filter(RoomInfo->appid.equals(RoomInfo.getAppId())&&RoomInfo.getUserIds().contains(userId))
                .findFirst().orElse(null);
    }

    public static RoomInfo getRoomInfo(String roomId){
        return roomMap.get(roomId);
    }

    public static List<RoomInfo> getAllRoomInfo(){
        return new ArrayList<>(roomMap.values());
    }


}
