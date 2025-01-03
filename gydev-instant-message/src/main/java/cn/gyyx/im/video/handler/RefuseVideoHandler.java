package cn.gyyx.im.video.handler;

import cn.gyyx.im.beans.request.AcceptVideoMessage;
import cn.gyyx.im.enums.MsgTypeEnum;
import cn.gyyx.im.service.IMsgHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RefuseVideoHandler implements IMsgHandler<AcceptVideoMessage> {
    @Override
    public MsgTypeEnum getMsgType() {
        return MsgTypeEnum.Refuse_Video;
    }

    @Override
    public void handler(AcceptVideoMessage acceptVideoMessage) {

    }
}
