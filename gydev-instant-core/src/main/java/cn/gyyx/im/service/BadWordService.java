package cn.gyyx.im.service;

import cn.gyyx.im.beans.entity.ImChatLog;
import cn.gyyx.im.beans.vo.ChatLog;
import cn.gyyx.im.enums.ChatContentTypeEnum;
import com.github.houbb.heaven.util.util.DateUtil;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BadWordService {
    private final SensitiveWordBs sensitiveWordBs;

    public BadWordService(SensitiveWordBs sensitiveWordBs) {
        this.sensitiveWordBs = sensitiveWordBs;
    }


    /**
     * 聊天历史记录转换
     * @author: zxw
     * @date: 2024/4/10/0010 17:34
     * @param appId
     * @param chatLogs
     * @return java.util.List<imserver.netty.beans.entity.ChatLog>
     **/
    public List<ChatLog> changeChatLog(String appId, List<ImChatLog> chatLogs){
        if(CollectionUtils.isEmpty(chatLogs)){
            return Collections.emptyList();
        }
        List<ChatLog> chatLogList = new ArrayList<>(chatLogs.size());

        for (ImChatLog imChatlog : chatLogs){
            ChatLog chatLog = new ChatLog();
            //去掉appId前缀
            String createTime = DateUtil.formatDate(imChatlog.getCreateTime(), DateUtil.DATE_TIME_SEC_FORMAT);
            chatLog.setMessageId(imChatlog.getCode());
            chatLog.setSender(imChatlog.getSender());
            chatLog.setReceiver(imChatlog.getReceiver());
            chatLog.setCreateTime(createTime);
            chatLog.setContent(replaceBadWord(imChatlog.getContent(),imChatlog.getContentType()));
            chatLogList.add(chatLog);
        }

        return chatLogList;
    }

    /**
     * 替换屏蔽字
     * @param content
     * @return
     */
    public String replaceBadWord(String content,String messageType){
        if(!ChatContentTypeEnum.TEXT.getType().equals(messageType)){
            return content;
        }

        String replaceContent = content;
        boolean isEnd = false;
        //获取标签位置
        int index = content.indexOf("[img");
        //不能存该标签直接进行屏蔽词过滤
        if(index==-1){
            replaceContent = replaceContent.replace(content,sensitiveWordBs.replace(content));
            //标签未在第一位执行屏蔽词过滤
        }else if(index!=0){
            String msg = content.substring(0, index);
            replaceContent = replaceContent.replace(msg,sensitiveWordBs.replace(msg));
        }
        while (!isEnd) {
            if (index > -1) {
                //截取图片
                int endIndex = content.indexOf("[/", index)+1;
                String img = content.substring(index, endIndex);
                if(!img.contains(".gyyx.cn")){
                    //证明图片来源于桶则不检测屏蔽字
                    replaceContent = replaceContent.replace(img,sensitiveWordBs.replace(img));
                }

                String other = content.substring(endIndex);
                index = other.indexOf("[img");
                if(index != -1){
                    String substring = other.substring(0, index);
                    if(substring.length()>0) {
                        replaceContent = replaceContent.replace(substring, sensitiveWordBs.replace(substring));
                    }
                    index += endIndex;
                    continue;
                }
                if(other.length()>0) {
                    replaceContent = replaceContent.replace(other,sensitiveWordBs.replace(other));
                }
                if(endIndex == content.length()){
                    isEnd = true;
                }
            }else {
                isEnd = true;
            }
        }

        return replaceContent;
    }
}
