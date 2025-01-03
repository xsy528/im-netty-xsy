package cn.gyyx.im.config;

import cn.gyyx.im.wshandler.InitChannelHandler;
import cn.gyyx.im.wshandler.WebRequestHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 邢少亚
 * @date 2024/2/23  17:28
 * @description 所有业务handler pod启动时注册
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class BusinessHandlerRegister extends ChannelInitializer<SocketChannel> {

    private final InitChannelHandler initChannelHandler;
    private final WebRequestHandler webRequestHandler;


    public BusinessHandlerRegister(InitChannelHandler initChannelHandler, WebRequestHandler webRequestHandler) {
        this.initChannelHandler = initChannelHandler;
        this.webRequestHandler = webRequestHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //限制请求消息体长度
        ch.pipeline()
                .addLast("http", new HttpServerCodec())
                //聚合器，使用websocket会用到
                .addLast("aggregator",new HttpObjectAggregator(65536))
                //用于大数据的分区传输
                .addLast("http-chunked",new ChunkedWriteHandler())
                .addLast("initChannel",initChannelHandler)
                .addLast(webRequestHandler);
                ;
    }
}
