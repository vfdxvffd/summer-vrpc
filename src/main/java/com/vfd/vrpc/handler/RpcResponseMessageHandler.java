package com.vfd.vrpc.handler;

import com.vfd.vrpc.message.Message;
import com.vfd.vrpc.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @PackageName: com.vfd.handler
 * @ClassName: RpcResponseMessageHandler
 * @Description:
 * @author: vfdxvffd
 * @date: 2021/5/10 下午1:31
 */
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    private final Map<Integer, Promise<Map<String, Object>>> PROMISES = new ConcurrentHashMap<>();

    public Map<Integer, Promise<Map<String, Object>>> getPROMISES() {
        return PROMISES;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage message) {
        Promise<Map<String, Object>> promise = PROMISES.remove(message.getSequenceId());
        if (promise != null) {
            final Object returnValue = message.getReturnValue();
            String parseJsonerBeanName = message.getParseJsonerBeanName();
            final Exception exceptionValue = message.getExceptionValue();
            if (exceptionValue != null) {
                promise.setFailure(exceptionValue);
            } else {
                Map<String, Object> result = new HashMap<>();
                result.put(Message.RETURN_VALUE, returnValue);
                result.put(Message.PARSE_JSONER_BEANNAME, parseJsonerBeanName);
                promise.setSuccess(result);
            }
        }
    }
}
