package cn.gyyx.im.dao;

import cn.gyyx.im.beans.entity.ImChatLog;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface ImChatLogDao {

    /**
     * 查询所有聊天记录
     * @param tableName
     * @param receiver
     * @return
     * 拆分appid,
     * todo 优化sql,错误的sql
     */
    @Select("select * from(" +
            "select *,ROW_NUMBER() OVER(partition by sender order by create_time desc) as Row_Sort  " +
            "from im_chat_log_${tableName} where receiver = #{receiver} and is_delete=0) a " +
            "where a.Row_Sort=1")
    List<ImChatLog> getAllChatLogs(@Param("tableName") String tableName, @Param("receiver") String receiver);

    /**
     * 查询所有聊天记录
     * @param tableName 接收人id 后两位
     * @param receiver 接收人
     * @param sender 发送人
     * @param lastChatId 最早一条聊天记录code
     * @param chatCount 最大拉取条数
     * @return 聊天记录
     */
    @Select({"<script>select * from im_chat_log_${tableName} " +
            "where receiver = #{receiver} and sender=#{sender} and is_delete=0 " +
            "<if test='lastChatId != null'> and code &gt; #{lastChatId} </if>" +
            " order by create_time desc limit #{chatCount}" +
            "</script>"})
    List<ImChatLog> getChatLogs(@Param("tableName") String tableName,@Param("receiver") String receiver
            ,@Param("sender") String sender,@Param("lastChatId") Integer lastChatId,@Param("chatCount") int chatCount);


    /**
     * 查询所有聊天记录
     * @param tableName
     * @param receiver
     * @return
     */
    @Select("select * from im_chat_log_${tableName} where receiver = #{receiver} and sender=#{sender} and create_time>#{startTime} and is_delete=0 " +
            " order by create_time desc")
    List<ImChatLog> getChatLogsByTime(@Param("tableName") String tableName,@Param("receiver") String receiver
            ,@Param("sender") String sender,@Param("startTime") Date startTime);

    /**
     * 保存聊天记录
     * @param tableName
     * @param log
     * @return
     */
    @Insert("insert into im_chat_log_${tableName} (receiver,content,create_time,sender,is_delete,platform,content_type) " +
            "values (#{log.receiver},#{log.content},#{log.createTime},#{log.sender},0,#{log.platform},#{log.contentType})")
    @Options(useGeneratedKeys = true, keyProperty = "log.code",keyColumn = "code")
    Integer insertChatLog(String tableName,ImChatLog log);

    /**
     * 撤回聊天记录
     * @param tableName
     * @param code
     * @return
     */
    @Update("update im_chat_log_${tableName} set is_delete=1 " +
            "where code = #{code} and create_time > #{startTime}")
    int updateChatLog(@Param("tableName") String tableName,@Param("code") Long code,@Param("startTime")String startTime);

    /**
      * 通过id获取聊天记录
      * @author: zxw
      * @date: 2024/3/26/0026 14:12
      * @param tableName
      * @param code
      *  @return imserver.netty.beans.entity.ImChatLog
      **/
    @Select("select * from im_chat_log_${tableName} where code=#{code} and sender=#{sender} and is_delete=0")
    ImChatLog getChatLogById(@Param("tableName")String tableName,@Param("code")Long code,@Param("sender")String sender);

    /**
     * 获取该时间之前所有的聊天记录
     * @author: zxw
     * @date: 2024/5/30/0030 15:33
     * @param appId
     * @param tableNameR 接收人聊天记录表
     * @param tableNameS 发送人聊天记录表
     * @param receiver  接收人
     * @param sender    发送人
     * @param time      时间
     * @return java.util.List<cn.gyyx.im.bean.entity.ImChatLog>
     **/
    @Select({"<script>  select * from (SELECT * FROM im_chat_log_${tableNameR} where  receiver=#{receiver} AND sender=#{sender} and platform=#{appId} and is_delete=0" +
            " UNION ALL" +
            " SELECT * FROM im_chat_log_${tableNameS} where  receiver=#{sender} AND sender=#{receiver} and platform=#{appId} and is_delete=0) message where message.create_time &lt;#{time}" +
            " order by message.create_time DESC LIMIT 0, 100</script>"})
    List<ImChatLog> getChatLogByTime(@Param("appId") String appId, @Param("tableNameR") String tableNameR,@Param("tableNameS") String tableNameS,@Param("receiver") String receiver, @Param("sender") String sender,@Param("time") Date time);


    @Select({"<script>  select * from im_chat_log_${tableName} " +
            "where receiver in (#{receiver},#{sender}) AND sender in (#{receiver},#{sender}) " +
            "and platform=#{appId} and is_delete=0 " +
            "<if test='lastChatId != null'> and code &gt; #{lastChatId} </if>" +
            "order by create_time desc " +
            "limit #{chatCount} </script>"})
    List<ImChatLog> getDoubleChatLog(@Param("appId") String appId, @Param("tableName") String tableName,
                                     @Param("receiver") String receiver, @Param("sender") String sender,
                                     @Param("chatCount") int chatCount,@Param("lastChatId") Integer lastChatId);

    @Select({"<script>  select * from im_chat_log_${tableName} " +
            "where receiver in (#{receiver},#{sender}) AND sender in (#{receiver},#{sender}) " +
            "and platform=#{appId} and is_delete=0 and create_time &lt;#{time}" +
            "<if test='lastChatId != null'> and code &gt; #{lastChatId} </if>" +
            "order by create_time desc " +
            "limit #{chatCount} </script>"})
    List<ImChatLog> getBothChatLog(@Param("appId") String appId, @Param("tableName") String tableName,
                                     @Param("receiver") String receiver, @Param("sender") String sender,
                                     @Param("time") Date time,
                                     @Param("chatCount") int chatCount,@Param("lastChatId") Integer lastChatId);

    @Select({"<script>  select * from im_chat_log_${tableName} " +
            "where receiver in (#{receiver},#{sender}) AND sender in (#{receiver},#{sender}) " +
            "and platform=#{appId} and is_delete=0 " +
            "order by create_time desc </script>"})
    List<ImChatLog> getChatLog(@Param("appId") String appId, @Param("tableName") String tableName,
                                     @Param("receiver") String receiver, @Param("sender") String sender);


    /**
     * 查询所有聊天列表
     * @param userId
     * @return
     */
    @Select("select count(*) from im_chat_log_${tableName} " +
            "where platform=#{appId} and receiver=#{userId} and sender=#{sender} ")
    Integer getChatCount(@Param("userId") String userId, @Param("sender") String sender,
                         @Param("appId") String appId, @Param("tableName") String tableName);

    /**
     * 查询有聊天记录的日期
     * @param appId
     * @param receiver
     * @param sender
     * @param tableName
     * @return
     */
    @Select("select date_format(create_time,'%Y-%m-%d') from im_chat_log_${tableName} " +
            "where platform=#{appId} and (receiver=#{receiver} or sender=#{receiver}) and (sender=#{sender} or receiver=#{sender}) " +
            "and create_time between #{start} and #{end} and is_delete=0 " +
            "group by date_format(create_time,'%Y-%m-%d')")
    List<String> getHasChatDay(@Param("appId") String appId,@Param("receiver") String receiver,
                             @Param("sender") String sender,@Param("tableName") String tableName,
                               @Param("start") Date start,@Param("end") Date end);


    /**
     * 通过id获取聊天记录
     *
     * @param tableName  表名
     * @param appId      app ID
     * @param messageIds 消息id
     * @param sender     发送者
     * @param receiver   接收者
     * @return {@link List}<{@link ImChatLog}>
     */
    @Select({
            "<script>",
            "SELECT * FROM im_chat_log_${tableName} ",
            "WHERE platform=#{appId} AND receiver=#{receiver} AND sender=#{sender} ",
            "AND code IN",
            "<foreach item='item' index='index' collection='messageIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<ImChatLog> getChatLogByIds(@Param("tableName") String tableName,
                                    @Param("appId") String appId,
                                    @Param("messageIds") List<Long> messageIds,
                                    @Param("sender") String sender,
                                    @Param("receiver") String receiver);


    /**
     * 获取最新一条聊天记录
     *
     * @param tableName 表名
     * @param appId     app ID
     * @param sender    发送者
     * @param receiver  接收器
     * @return {@link ImChatLog}
     */
    @Select({
            "SELECT * FROM im_chat_log_${tableName} ",
            "WHERE platform=#{appId} AND receiver=#{receiver} AND sender=#{sender}",
            "order by code desc",
            "limit 1"
    })
    ImChatLog getLastChatLog(@Param("tableName") String tableName,
                                    @Param("appId") String appId,
                                    @Param("sender") String sender,
                                    @Param("receiver") String receiver);



}
