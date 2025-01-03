package cn.gyyx.im.wshandler;

import cn.gyyx.im.beans.StringConstant;
import cn.gyyx.im.beans.UserSession;
import cn.gyyx.im.config.ImConfig;
import cn.gyyx.im.service.UserService;
import cn.gyyx.im.utils.MD5;
import cn.gyyx.im.utils.SessionUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 邢少亚
 * @date 2024/2/23  18:23
 * @description 用户建立ws连接
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class WebRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final ImConfig imConfig;
    private final UserService userService;
    private final TextWebSocketFrameHandler textWebSocketFrameHandler;
    @Setter
    @Value("${server.heart.time:60}")
    private Integer readTimeout;

    public WebRequestHandler(ImConfig imConfig, UserService userService, TextWebSocketFrameHandler textWebSocketFrameHandler) {
        this.imConfig = imConfig;
        this.userService = userService;
        this.textWebSocketFrameHandler = textWebSocketFrameHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        log.info(uri);
        switch (uri.split("\\?")[0]){
            case "/health":
                //跳过健康检查
                String responseBody = "成功";
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(responseBody, StandardCharsets.UTF_8));
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                channelHandlerContext.writeAndFlush(response);
                return;
            case "/login": {
                try {
                    // 首次验签，决定是否驱逐，验签成功后注册用户
                    Map<String, String> paramsMap = convertParam(uri);
                    boolean checkSign = checkSign(paramsMap);
                    if (!checkSign) {
                        //验签失败，通知前端
                        channelHandlerContext.close();
                    } else {
                        //验签成功
                        String appId = paramsMap.get("appId");
                        String userId = paramsMap.get("userId");
                        UserSession userSession = new UserSession();
                        userSession.setAppId(appId);
                        userSession.setUserId(userId);

                        //获取用户登录端信息
                        userSession.setDevice(paramsMap.get("loginId"));

                        //将登录成功信息放入通道缓存中
                        channelHandlerContext.channel().attr(SessionUtil.SESSION).set(userSession);
                        //将登录请求url信息放入通道缓存中
                        channelHandlerContext.channel().attr(SessionUtil.urlParam).set(paramsMap);
                        //注册合法用户
                        userService.login(channelHandlerContext, userSession);
                        log.info("用户登录成功，信息：{}", userSession);
                    }
                } catch (Exception e) {
                    log.error("消息转换异常e:{}，消息内容：{}", e, uri);
                    log.error(e.getMessage(), e);
                    userService.loginOut(channelHandlerContext);
                }

                //websocket响应
                WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("", null, false);
                WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(request);
                if (handshaker == null) {
                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channelHandlerContext.channel());
                } else {
                    handshaker.handshake(channelHandlerContext.channel(), request);
                }

                //在本channel上移除这个handler，只执行一次
                channelHandlerContext.pipeline().remove(WebRequestHandler.class);
                channelHandlerContext.pipeline().addLast("IdleState", new IMIdleStateHandler(readTimeout, userService));
                channelHandlerContext.pipeline().addLast(textWebSocketFrameHandler);
                return;
            }
            case "/link/outLogin": {
                Map<String, String> paramsMap = convertParam(uri);
                Map<String, Channel> channelMap = SessionUtil.getChannel(paramsMap.get("appId"), paramsMap.get("userId"));
                if(StringUtils.isNotEmpty(paramsMap.get("deviceId"))){
                    //单独踢一个设备
                    Channel channel = channelMap.get(paramsMap.get("deviceId"));
                    SessionUtil.loginOut(channel);
                }else {
                    //用户所有端踢登录
                    channelMap.values().forEach(channel -> {
                        SessionUtil.loginOut(channel);
                    });
                }
                sendResponse(channelHandlerContext);
                return;
            }
            case "/link/sendMessage": {
                String body = request.content().toString(CharsetUtil.UTF_8);
                Map<String, String> paramsMap = convertParam(uri);
                if(StringUtils.isNotEmpty(paramsMap.get("deviceId"))){
                    SessionUtil.sendMessage(paramsMap.get("appId"),paramsMap.get("userId"),paramsMap.get("deviceId"),body);
                }else {
                    SessionUtil.sendMessage(paramsMap.get("appId"),paramsMap.get("userId"),body);
                }
                sendResponse(channelHandlerContext);
                return;
            }
        }
    }

    private void sendResponse(ChannelHandlerContext ctx){
        DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        res.content().writeBytes("成功".getBytes());
        ChannelFuture future = ctx.channel().writeAndFlush(res);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    private Map<String, String> convertParam(String url) {
        int index = url.indexOf("?");
        String paramString = url.substring(index + 1);
        String[] paramKeyValue = paramString.split("&");
        Map<String, String> params = new HashMap<>(paramKeyValue.length);
        for (String s : paramKeyValue) {
            String[] split = s.split("=");
            if(split.length==2) {
                params.put(split[0], split[1]);
            }
        }
        return params;
    }

    public boolean checkSign(Map<String, String> paramsMap) {
        String msgType = paramsMap.get(StringConstant.msgType);
        String appId = paramsMap.get("appId");
        String userId = paramsMap.get("userId");
        String loginId = paramsMap.get("loginId");
        String timestamp = paramsMap.get("txNo");
        String userSig = paramsMap.get("sign");

        if (StringUtils.isAnyBlank(msgType, appId, userId, timestamp, userSig)) {
            log.info("用户连接socket参数错误");
            return false;
        }
        //获取当前时间戳
        long currentTime = System.currentTimeMillis();
        long timeStamp = Long.parseLong(timestamp);
        // 相差五分钟以上 不合法
        if (Math.abs(currentTime - timeStamp) / 1000 > imConfig.getOutTime()) {
            log.info("用户：{}连接socket签名已过期", userId);
            return false;
        }

        if(StringUtils.isNotEmpty(loginId) && !"null".equals(loginId) && !"undefined".equals(loginId)){
            boolean equals = userSig.equals(MD5.encode(msgType + appId + userId + loginId + timestamp + imConfig.getKey()));
            if (!equals) {
                log.info("用户：{}连接socket签名错误, loginId:{}", userId, loginId);
                return false;
            }
            return true;
        }

        String sign = MD5.encode(msgType + appId + userId + timestamp + imConfig.getKey());
        boolean equals = sign.equals(userSig);
        if (!equals) {
            log.info("用户：{}连接socket签名错误", userId);
            return false;
        }
        log.info("用户：{}连接socket签名验证成功", userId);
        return true;
    }
}
