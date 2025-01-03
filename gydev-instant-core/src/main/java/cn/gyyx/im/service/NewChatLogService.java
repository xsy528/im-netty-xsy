package cn.gyyx.im.service;

import cn.gydev.lib.bean.ResultBean;
import cn.gyyx.im.beans.entity.ChatListLog;
import cn.gyyx.im.beans.entity.ImChatLog;
import cn.gyyx.im.beans.vo.ChatLog;
import cn.gyyx.im.dao.ChatListLogDao;
import cn.gyyx.im.dao.ImChatLogDao;
import cn.gyyx.im.enums.Constant;
import cn.gyyx.im.utils.DateTimeUnit;
import cn.gyyx.im.utils.DateTimeUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NewChatLogService {

    private final ImChatLogDao imChatLogDao;
    private final ChatListLogDao chatListLogDao;
    private final RedisTemplate<String, String> redisTemplate;
    private final BadWordService badWordService;
    private final RedisService redisService;

    private final int chatLogHoldDay = 90;
    private final int defaultLoadChatCount = 50;
    private final int USER_TABLE_LENGTH=2;

    private final String INDEX_NAME = "year_all_personal_chat_log";
    private final String CHAT_HISTORY_LIMIT_DATE_BEFORE = "before";
    private final String CHAT_HISTORY_LIMIT_DATE_AFTER = "after";

    public NewChatLogService(ImChatLogDao imChatLogDao, ChatListLogDao chatListLogDao,
                             RedisTemplate redisTemplate, BadWordService badWordService,
                             RedisService redisService) {
        this.imChatLogDao = imChatLogDao;
        this.chatListLogDao = chatListLogDao;
        this.redisTemplate = redisTemplate;
        this.badWordService = badWordService;
        this.redisService = redisService;
    }

    /**
     * 处理通过接收人判断是哪个表的
     * @param receiver
     * @return
     */
    public String getTableName(String sender,String receiver){
        String result = sender.compareTo(receiver)>0?sender:receiver;
        result = result.length()<=1?"0"+result:result;
        return result.substring(result.length()-USER_TABLE_LENGTH);
    }

    /**
     * 处理群组表名
     * @param groupId
     * @return
     */
    private String getGroupTableName(String groupId){
        String result =  groupId.length()<=1?"0"+groupId:groupId;
        return result.substring(result.length()-USER_TABLE_LENGTH);
    }

    /**
     * 获取未读消息缓存键
     *
     * @param appId    app ID
     * @param receiver 接收者
     * @param sender   发送者
     * @return {@link String}
     */
    public String getUnreadMessageCacheKey(String appId,String receiver, String sender){
        return appId + ":" + receiver + ":" + sender;
    }

    /**
     * 获取历史聊天记录，用于用户往上翻阅历史记录
     * @param receiver 接收人
     * @param sender 发送人
     * @param lastChatId 最早一个消息的消息id/code
     * @return
     */
    public List<ImChatLog> loadChatHistory(String receiver, String sender, Integer lastChatId, String appId){
        int messageCount = defaultLoadChatCount;
        String tableName = getTableName(sender,receiver);
        //获取接收人是的接收消息
        return imChatLogDao.getDoubleChatLog(appId,tableName, receiver, sender, messageCount,lastChatId);
    }

    /**
     * 获取历史聊天记录，用于用户往上翻阅历史记录
     * @param receiver 接收人
     * @param sender 发送人
     * @param appId 最早一个消息的消息id/code
     * @return
     */
    public PageInfo<ChatLog> loadChatHistoryByPage(String receiver, String sender, String appId, Integer pageIndex, Integer pageSize){

        String tableName = getTableName(sender,receiver);
        PageHelper.startPage(pageIndex, pageSize);
        //获取接收人是的接收消息
        List<ImChatLog> chatLog = imChatLogDao.getChatLog(appId, tableName, receiver, sender);
        PageInfo<ImChatLog> imChatLogPageInfo = new PageInfo<>(chatLog);

        //更改排序，先用时间导向
        chatLog = chatLog.stream().sorted(Comparator.comparing(ImChatLog::getCreateTime)).collect(Collectors.toList());

        //参数转换 entity->vo
        PageInfo<ChatLog> result = new PageInfo<>();
        result.setList(badWordService.changeChatLog(appId,chatLog));
        result.setTotal(imChatLogPageInfo.getTotal());
        result.setPageNum(pageIndex);
        result.setPageSize(pageSize);
        result.setSize(imChatLogPageInfo.getSize());
        result.setStartRow(imChatLogPageInfo.getStartRow());
        result.setEndRow(imChatLogPageInfo.getEndRow());
        result.setPages(imChatLogPageInfo.getPages());
        result.setPrePage(imChatLogPageInfo.getPrePage());
        result.setNextPage(imChatLogPageInfo.getNextPage());
        result.setIsFirstPage(imChatLogPageInfo.isIsFirstPage());
        result.setIsLastPage(imChatLogPageInfo.isIsLastPage());
        result.setHasPreviousPage(imChatLogPageInfo.isHasPreviousPage());
        result.setHasNextPage(imChatLogPageInfo.isHasNextPage());
        result.setNavigatePages(imChatLogPageInfo.getNavigatePages());
        result.setNavigatepageNums(imChatLogPageInfo.getNavigatepageNums());
        result.setNavigateFirstPage(imChatLogPageInfo.getNavigateFirstPage());
        result.setNavigateLastPage(imChatLogPageInfo.getNavigateLastPage());

        return result;
    }

    /**
     * 获取用户和某人的详细消息记录
     * @param receiver 接收人
     * @param sender 发送人
     * @return
     * todo 长时间未取聊天记录，聊天记录过多考虑截取条数
     */
    public List<ImChatLog> loadChatHistory(String receiver,String sender,String appId,boolean isHasHistory){
        int messageCount = defaultLoadChatCount;

        if(isHasHistory) {
            // 获取未读消息数量
            int senderUnViewCount = getUnreadMessageCount(appId, receiver);
            messageCount = Math.min(senderUnViewCount, defaultLoadChatCount);
        }

        //接收人表名
        String tableName = getTableName(sender,receiver);

        //获取接收人发送人当前时间之前的100条消息记录
        return imChatLogDao.getDoubleChatLog(appId, tableName,receiver,sender,messageCount,null);
    }


    /**
     * 获取未读消息数(批量)
     *
     * @param appId    app ID
     * @param receiver 接收者
     * @return {@link Integer}
     */
    public Integer getUnreadMessageCount(String appId, String receiver) {
        // 获取未读消息数量
        Set<String> keys = redisService.getKeys(getUnreadMessageCacheKey(appId, receiver, "*"));
        return Math.toIntExact(redisService.pipelineBitCount(new ArrayList<>(keys)));
    }

    /**
     * 获取未读消息计数(单个)
     *
     * @param appId    app ID
     * @param receiver 接收者
     * @param sender   发送者
     * @return {@link Integer}
     */
    public Integer getUnreadMessageCount(String appId, String receiver, String sender) {
        // 获取未读消息数量
        return Math.toIntExact(redisService.bitCount(getUnreadMessageCacheKey(appId, receiver, sender)));
    }


    private String getEsIndex(String receiver,String sender,String appId,Long code){
        return appId+"&"+receiver+"&"+sender+"&"+code;
    }

    /**
     * 撤回消息
     * 逻辑删除表，同时清除接收人未读消息列表钟的一条
     * @param msgId 消息code
     * @param receiver 接收人
     * @param sender 发送人
     * @return 是否成功
     */
    public boolean withdrawChat(Long msgId,String receiver,String sender,String appId,String startTime){
        String tableName = getTableName(sender,receiver);
        boolean isSuccess = imChatLogDao.updateChatLog(tableName,msgId,startTime) > 0;
        if(isSuccess){
            // 直接将对应未读消息清空
            String unreadKey = getUnreadMessageCacheKey(appId, receiver, sender);
            redisService.setBit(unreadKey, msgId, false);
        }
        return isSuccess;
    }


    /**
     * 保存消息
     * @param sender 发送人
     * @param receiver 接收人
     * @param content 消息内容
     * @param date 发送时间，客户端传入，因使用后端时间会有一定误差导致时间不够准确，所以建议前端进行传入
     * @return 保存结果
     */
    public ImChatLog saveChatHistory(String sender, String receiver, String content,Date date,String appId,String contentType) throws Exception {
        // 写入日志表
        ImChatLog chatLog = new ImChatLog();
        chatLog.setReceiver(receiver);
        chatLog.setSender(sender);
        chatLog.setContent(content);
        chatLog.setCreateTime(new Date());
        chatLog.setPlatform(appId);
        chatLog.setContentType(contentType);

        String tableName = getTableName(sender,receiver);
        imChatLogDao.insertChatLog(tableName, chatLog);

        // 保存未读消息id列表
        String unreadKey = getUnreadMessageCacheKey(appId, receiver, sender);
        redisService.setBit(unreadKey, chatLog.getCode(), true);

        //获取当前时间距离24点还剩多少秒
        int leaveSeconds = DateTimeUtil.betweenTime(DateTimeUtil.getTodayEndTime(), new Date(), DateTimeUnit.ONE_SECONDS);
        //记录每日发送消息数量
        RecordImService.recordTotalNumber(redisTemplate, Constant.IM_DAY_CHAT_NUMBER,leaveSeconds);

        return chatLog;
    }

    /**
     * 保存消息
     * @param sender 发送人
     * @param groupId 群组id
     * @param content 消息内容
     * @param date 发送时间，客户端传入，因使用后端时间会有一定误差导致时间不够准确，所以建议前端进行传入
     * @return 保存结果
     */
    public Long saveGroupChatHistory(String sender, String groupId, String content,Date date,String appId){
        //todo 根据groupId获取群组中其他接收人
        ImChatLog chatLog = new ImChatLog();
        chatLog.setReceiver(groupId);
        chatLog.setSender(sender);
        chatLog.setContent(content);
        chatLog.setCreateTime(date);
        chatLog.setPlatform(appId);

        String tableName = getGroupTableName(groupId);
        imChatLogDao.insertChatLog(tableName, chatLog);
        return chatLog.getCode();
    }


    /**
     * 获取key
     * @param receiver
     * @param sender
     * @param appId
     * @return
     */
    private String getMapKey(String receiver,String sender, String appId){
        return appId+"&"+receiver+"&"+sender;
    }

    /**
     * 获取聊天的所有用户id，按顺序排列
     * @param userId
     * @param appId
     * @return
     */
    public ResultBean<Object> getChatUserIdList(String userId, String appId) {
        //获取所有有记录的聊天列表
        List<ChatListLog> chatList = chatListLogDao.getAllChatList(userId,appId);
        if(chatList==null || chatList.size()==0){
            return ResultBean.success(new ArrayList<>());
        }

        Map<String, ChatListLog> senderAndReceiverMap = chatList.stream().
                collect(Collectors.toMap(ChatListLog->getMapKey(ChatListLog.getReceiver(),ChatListLog.getSender(),ChatListLog.getPlatform()), ChatListLog->ChatListLog));

        List<String> userIdList = new ArrayList<>(chatList.size());

        for (ChatListLog log : chatList){
            if(log.getReceiver().equals(userId)){
                //用户是消息接收人
                if(log.getSender().equals(userId)){
                    //如果是自己跟自己聊天
                    continue;
                }
                if(Boolean.FALSE.equals(log.getIsDelete())){
                    //未被删除聊天列表,检查发送方是否有消息
                    if(senderAndReceiverMap.get(getMapKey(log.getSender(),userId,log.getPlatform()))!=null){
                        //互相发送过信息
                        userIdList.add(log.getSender());
                    }else {
                        String tableName = getTableName(log.getSender(),userId);
                        Integer chatCount = imChatLogDao.getChatCount(userId, log.getSender(), appId, tableName);
                        if(chatCount!=null && chatCount>0){
                            //发送人发送过消息，则展示在列表中
                            userIdList.add(log.getSender());
                        }
                    }
                }else {
                    //用户主动删除聊天列表
                }
            }else {
                //用户是消息发送人
                ChatListLog chatListLog = senderAndReceiverMap.get(getMapKey(userId,log.getReceiver(),log.getPlatform()));
                if(chatListLog==null && log.getIsDelete()){
                    //如果userId点击了B但并未聊天，且userId删除了聊天列表
                    continue;
                }else if(chatListLog!=null && chatListLog.getIsDelete()){
                    //A和B双方聊天过，userId用户主动删除了聊天列表，则不暂时在聊天列表中
                    continue;
                }
                userIdList.add(log.getReceiver());
            }
        }

        List<String> result = userIdList.stream().distinct().filter(id -> !userId.equals(id)).collect(Collectors.toList());
        return ResultBean.success(result);
    }

    /**
     * 新增聊天列表
     * @param userId 发送人
     * @param appId  平台id
     * @param receiver 接收人
     * @return
     */
    public ResultBean<Object> addChatUserId(String userId, String appId, String receiver) {
        List<ChatListLog> bothChatList = chatListLogDao.getBothChatList(userId, appId, receiver);
        Integer i;
        if(bothChatList==null || bothChatList.size()==0){
            i = chatListLogDao.insertChatList(userId, appId,receiver);
        }else if(bothChatList.size()==2){
            i = chatListLogDao.updateChatList(receiver, appId, userId);
        }else {
            ChatListLog chatListLog = bothChatList.get(0);
            if (userId.equals(chatListLog.getReceiver())) {
                //userId作为接收人，需新增一条userId作为发送人的记录
                i = chatListLogDao.insertChatList(userId, appId,receiver);
            }else {
                //userId作为发送人，说明之前主动删除过聊天记录，又重新添加了，则更新删除状态
                i = chatListLogDao.updateChatList(userId, appId,receiver);
            }
        }

        if(i!=null && i>0){
            return ResultBean.success("新建会话成功");
        }
        return ResultBean.serverError();
    }

    public ResultBean<Object> removeChatUserId(String userId, String appId, String receiver) {
        List<ChatListLog> bothChatList = chatListLogDao.getBothChatList(userId, appId, receiver);
        if(bothChatList==null){
            return ResultBean.paramError("无聊天记录");
        }
        Integer i = 0;
        //存在两条记录时代表双方有真实的聊天，如果只有一条，代表有一方只是拉起了聊天框。
        if(bothChatList.size()==2){
            i = chatListLogDao.deleteChatList(userId, appId, receiver);
        }else {
            i = chatListLogDao.deleteChatList(receiver, appId, userId);
        }

        log.info("平台“{}，用户：{} 删除和 {} 的聊天记录,结果：{}",appId,userId,receiver,i!=null && i>0);

        if(i!=null && i>0){
            return ResultBean.success("删除会话成功");
        }
        return ResultBean.statusError("无会话记录");
    }

    /**
     * 获取指定时间之前的100条聊天记录
     * @author: zxw
     * @date: 2024/5/30/0030 16:13
     * @param sender 发送人
     * @param appId
     * @param receiver 接收人
     * @param time  时间
     * @return cn.gydev.lib.bean.ResultBean<java.util.List<cn.gyyx.im.bean.entity.ImChatLog>>
     **/
    public ResultBean<List<ImChatLog>> getChatHistoryByTime(String sender, String appId, String receiver, Date time){
        //接收人表名
        String tableName = getTableName(sender,receiver);
        //发送人表名
        //获取接收人发送人某一时间之前的100条消息记录
        List<ImChatLog> chatLogByTime = imChatLogDao.getBothChatLog(appId, tableName, receiver, sender, time,100,null);
        if(chatLogByTime!=null && chatLogByTime.size()>0){
            List<ImChatLog> collect = chatLogByTime.stream().sorted(Comparator.comparing(ImChatLog::getCreateTime)).collect(Collectors.toList());
            return ResultBean.success("获取消息记录成功", collect);
        }
        return ResultBean.success("暂无历史消息",null);
    }

    /**
     *
     * @param appId
     * @param receiver
     * @param sender
     * @return
     */
    public ResultBean<Object> getHasChatDay(String appId, String receiver, String sender,String year) throws ParseException {
        String tableName = getTableName(sender,receiver);
        String startDay = year+"-01-01";
        String endDay = year+"-12-31";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date start = format.parse(startDay);
        Date end = format.parse(endDay);
        List<String> chatDays = imChatLogDao.getHasChatDay(appId,receiver,sender,tableName,start,end);
        return ResultBean.success(chatDays);
    }
}
