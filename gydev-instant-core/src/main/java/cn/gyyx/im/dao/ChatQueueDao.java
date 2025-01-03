package cn.gyyx.im.dao;

import cn.gyyx.im.beans.entity.ChatListLog;
import cn.gyyx.im.beans.entity.ChatQueue;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface ChatQueueDao {

    /**
     * 保存聊天列表
     * @param userId
     * @param platform
     * @return
     */
    @Insert("insert into chat_queue_tb (platform,user_id,finish_time,create_time,receiver,status) " +
            "values (#{platform},#{userId},null,now(),null,0)")
    Integer insertChatQueue(String userId,String platform);

    @Update("update chat_queue_tb set status= 2 " +
            "where platform=#{platform} and user_id=#{userId} and status=0")
    Integer changeUserWaitStatus(String userId,String platform);

    @Select("select * from chat_queue_tb " +
            "where platform=#{platform} and user_id=#{userId} and status=1 order by create_time asc limit 1")
    ChatQueue getLastWait(String userId,String platform);

    @Update("update chat_queue_tb set status=0 " +
            "where platform=#{platform} and user_id=#{userId} and status=1")
    Integer waitQueueAgain(String userId,String platform);

    @Select("select * from chat_queue_tb" +
            " where platform=#{appId} and status=0 order by create_time asc limit 1 FOR UPDATE")
    ChatQueue getOneQueue(@Param("appId") String appId);

    @Select("select * from chat_queue_tb" +
            " where platform=#{appId} and user_id=#{userId} and status=0 order by create_time asc limit 1 FOR UPDATE")
    ChatQueue getOldUserQueue(@Param("appId") String appId,@Param("userId") String userId);

    @Select("select * from chat_queue_tb " +
            "where platform=#{appId} and status=0 order by create_time asc")
    List<ChatQueue> getAllQueue(@Param("appId") String appId);

    @Update("update chat_queue_tb set status=#{status},finish_time=now() " +
            "where code=#{code}")
    Integer updateChatQueue(Integer status, Integer code);

    @Update("update chat_queue_tb set status=#{status},finish_time=now() " +
            "where status=0 and user_id=#{userId} and platform=#{platform}")
    Integer quitQueue(String userId,String platform,Integer status);
}
