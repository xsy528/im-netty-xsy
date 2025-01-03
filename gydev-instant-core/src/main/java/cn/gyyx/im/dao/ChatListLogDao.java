package cn.gyyx.im.dao;


import cn.gyyx.im.beans.entity.ChatListLog;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface ChatListLogDao {

    /**
     * 查询所有聊天列表,查询所有
     * @param userId
     * @return
     */
    @Select("select * from chat_list_log " +
            "where platform=#{appId} and is_delete=0 and (sender=#{userId} or receiver=#{userId}) " +
            "order by update_time desc limit 40")
    List<ChatListLog> getAllChatList(@Param("userId") String userId, @Param("appId") String appId);


    /**
     * 更新聊天时间
     * @param platform
     * @return
     */
    @Select("select * from chat_list_log " +
            "where (receiver = #{receiver} and sender =#{sender} and platform = #{platform}) or" +
            " (receiver = #{sender} and sender =#{receiver} and platform = #{platform})")
    List<ChatListLog> getBothChatList(@Param("sender")String sender,@Param("platform")String platform, @Param("receiver")String receiver);


    /**
     * 更新聊天时间
     * @param platform
     * @return
     */
    @Update("update chat_list_log set update_time=now(),is_delete=0 " +
            "where receiver = #{receiver} and sender =#{sender} and platform = #{platform}")
    Integer updateChatList(@Param("sender")String sender,@Param("platform")String platform, @Param("receiver")String receiver);


    /**
     * 删除聊天
     * @param platform
     * @return
     */
    @Update("update chat_list_log set update_time=now(),is_delete=1 " +
            "where receiver = #{receiver} and sender =#{sender} and platform = #{platform}")
    Integer deleteChatList(@Param("receiver")String receiver,@Param("platform")String platform, @Param("sender")String sender);

    /**
     * 保存聊天列表
     * @param sender
     * @param platform
     * @return
     */
    @Insert("insert into chat_list_log (receiver,sender,platform,create_time,update_time) " +
            "values (#{receiver},#{sender},#{platform},now(),now())")
    Integer insertChatList(String sender,String platform,String receiver);

}
