package cn.gyyx.im.service;

import cn.gyyx.im.beans.IBaseMsg;
import cn.gyyx.im.enums.MsgTypeEnum;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;



@Component
public interface IMsgHandler<T extends IBaseMsg> {

    MsgTypeEnum getMsgType();

    /**
     * 消息处理
     */
    @Async
    void handler(T t);
}
