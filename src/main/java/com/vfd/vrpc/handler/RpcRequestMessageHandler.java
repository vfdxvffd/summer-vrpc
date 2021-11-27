package com.vfd.vrpc.handler;

import com.vfd.summer.applicationContext.ApplicationContext;
import com.vfd.summer.ioc.bean.BeanDefinition;
import com.vfd.vrpc.annotation.ParseByBean;
import com.vfd.vrpc.config.Config;
import com.vfd.vrpc.message.RpcRequestMessage;
import com.vfd.vrpc.message.RpcResponseMessage;
import com.vfd.vrpc.protocol.serializer.ParseJsoner4Param;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

/**
 * @PackageName: com.vfd
 * @ClassName: RpcRequestMessageHandler
 * @Description:
 * @author: vfdxvffd
 * @date: 2021/5/10 下午12:31
 */
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    private ApplicationContext ioc;

    public RpcRequestMessageHandler() {
    }

    public RpcRequestMessageHandler(ApplicationContext context) {
        this.ioc = context;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) {
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(message.getSequenceId());
        try {
            final Object o;
            BeanDefinition beanDefinition;
            if ("".equals(message.getBeanName())) {
                o = ioc.getBean(message.getBeanType());
                beanDefinition = ioc.getBeanDefinition(message.getBeanType());
            } else {
                o = ioc.getBean(message.getBeanName(), message.getBeanType());//ServicesFactory.getService(message.getInterfaceName(), interfaceToImplement);
                beanDefinition = ioc.getBeanDefinition(message.getBeanName(), message.getBeanType());
            }

            Method realMethod = beanDefinition.getBeanClass().getDeclaredMethod(message.getMethodName(), message.getParameterTypes());
            ParseByBean parseByBean = realMethod.getAnnotation(ParseByBean.class);
            final Method method = o.getClass().getDeclaredMethod(message.getMethodName(), message.getParameterTypes());
            Object[] params = message.getParameterValue();
            if (parseByBean != null && !"".equals(parseByBean.parseParamsBeanName())) {
                ParseJsoner4Param parseJsoner4Param = ioc.getBean(parseByBean.parseParamsBeanName(), ParseJsoner4Param.class);
                params = parseJsoner4Param.parseParam(params);
            }
            final Object invoke = method.invoke(o, params);
            response.setReturnValue(invoke);
            if (parseByBean != null && !"".equals(parseByBean.parseResultBeanName())) {
                response.setParseJsonerBeanName(parseByBean.parseResultBeanName());
            }
            response.setAlias(Config.getServerAlias());
        } catch (Exception e) {
            e.printStackTrace();
            String msg;
            final Throwable cause = e.getCause();
            if (cause == null)  msg = e.getMessage();
            else    msg= cause.getMessage();
            if (msg == null)    msg = e.toString();
            final Exception exception = new Exception("Remote procedure call error: " + msg);
            exception.setStackTrace(new StackTraceElement[]{});
            response.setExceptionValue(exception);
        } finally {
            ctx.writeAndFlush(response);
        }
    }
}
