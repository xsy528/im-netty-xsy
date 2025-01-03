package cn.gyyx.im.wshandler;

import cn.gyyx.im.beans.MsgTypeConstant;
import cn.gyyx.im.beans.StringConstant;
import cn.gyyx.im.beans.UserSession;
import cn.gyyx.im.service.CallService;
import cn.gyyx.im.service.UserService;
import cn.gyyx.im.utils.SessionUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ChannelHandler.Sharable
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final UserService userService;
    private final CallService callService;

    public TextWebSocketFrameHandler(UserService userService, CallService callService) {
        this.userService = userService;
        this.callService = callService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String content = textWebSocketFrame.text();
        try {
            JSONObject messageContent = JSONObject.parseObject(content);
            Integer msgType = messageContent.getInteger(StringConstant.msgType);
            if(msgType==null){
                log.info("异常消息：{}",content);
                //异常消息
                messageContent.put(StringConstant.msgType,MsgTypeConstant.Param_Error);
                channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(messageContent.toJSONString()));
                return;
            }else if(msgType== MsgTypeConstant.Client_Receive_Success){
                //前端响应成功处理
                SessionUtil.clearUnique(messageContent.getString(StringConstant.unique));
                return;
            }else if(msgType== MsgTypeConstant.Heartbeat){
                //心跳事件特殊处理
                messageContent.put(StringConstant.msgType,MsgTypeConstant.Heartbeat_SUCCESS);
                messageContent.put("result","success");
                messageContent.put("content","心跳成功");
                channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(messageContent.toJSONString()));
                return;
            }

            log.info("处理消息：{}",content);
            UserSession userSession = channelHandlerContext.attr(SessionUtil.SESSION).get();
            messageContent.put("appId",userSession.getAppId());
            messageContent.put("userId",userSession.getUserId());
            messageContent.put("deviceId",userSession.getDevice());

            callService.handleMsg(userSession.getAppId(),userSession.getUserId(),userSession.getDevice(),messageContent.toJSONString());

            //发送前端响应
            messageContent.put(StringConstant.receiveMsgType,msgType);
            messageContent.put(StringConstant.msgType,MsgTypeConstant.Server_Receive_Success);
            channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(messageContent.toJSONString()));
        }catch (Exception e){
            log.error("消息处理异常,消息内容：{}", content);
            log.error(e.getMessage(),e);
            userService.loginOut(channelHandlerContext);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(),cause);
    }
}
