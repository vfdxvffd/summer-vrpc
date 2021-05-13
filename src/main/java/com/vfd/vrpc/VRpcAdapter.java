package com.vfd.vrpc;

import com.vfd.summer.applicationContext.impl.SummerAnnotationConfigApplicationContext;
import com.vfd.summer.extension.Extension;
import com.vfd.summer.ioc.bean.BeanDefinition;
import com.vfd.vrpc.annotation.Reference;
import com.vfd.vrpc.config.Config;
import com.vfd.vrpc.handler.RpcRequestMessageHandler;
import com.vfd.vrpc.handler.RpcResponseMessageHandler;
import com.vfd.vrpc.message.RpcRequestMessage;
import com.vfd.vrpc.protocol.Destination;
import com.vfd.vrpc.protocol.MessageCodec;
import com.vfd.vrpc.protocol.ProtocolFrameDecoder;
import com.vfd.vrpc.protocol.SequenceIdGenerator;
import com.vfd.vrpc.protocol.serializer.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @PackageName: com.vfd.v-rpc
 * @ClassName: VRpc
 * @Description:
 * @author: vfdxvffd
 * @date: 2021/5/12 下午2:20
 */
public class VRpcAdapter implements Extension {

    public final Map<Integer, Channel> serverChannelMap = new ConcurrentHashMap<>();
    //private Channel channel = null;
    public final Map<Destination, Channel> clientChannelMap = new ConcurrentHashMap<>();
    RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();

    boolean keepAlive = Config.keepAlive();

    private SummerAnnotationConfigApplicationContext context;

    //记录关键位置的日志
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void doOperation0 (SummerAnnotationConfigApplicationContext context) {
        this.context = context;
        Config.propertyFile = context.getPropertyFile();
    }

    @Override
    public void doOperation1 (SummerAnnotationConfigApplicationContext context) {

    }


    // 把控制、操作、修改rpc配置的句柄装配到ioc容器中
    @Override
    public void doOperation2 (SummerAnnotationConfigApplicationContext context) {
        context.getIocByName().put("vRpcHandler", new VRpcHandler(this));
        final HashSet<String> set = new HashSet<>();
        set.add("vRpcHandler");
        context.getBeanTypeAndName().put(VRpcHandler.class, set);
    }

    // 对二级缓存中标注了@Reference注解的域进行注入
    @Override
    public void doOperation3 (SummerAnnotationConfigApplicationContext context) throws Exception {
        for (Map.Entry<String, Object> objectEntry : context.getEarlyRealObjects().entrySet()) {
            referenceObject(objectEntry.getValue());
        }
        logger.info("远程调用的代理对象设置完成");
    }

    private void referenceObject (Object object) throws Exception {
        final Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            final Reference reference = field.getAnnotation(Reference.class);
            if (reference != null) {
                field.setAccessible(true);
                if (field.get(object) != null) {
                    continue;
                }
                String host = "".equals(reference.host())? Config.getDestHost():reference.host();
                int port = reference.port() == -1? Config.getDestPort():reference.port();
                Serializer serializer = reference.serializer()==Serializer.class? Config.getDestSerializer():reference.serializer().newInstance();
                final Object remoteObj = getRemoteObj(host, port, serializer, reference.beanName(), field.getType());
                field.set(object, remoteObj);
            }
        }
    }

    @SuppressWarnings("all")
    private <T> T getRemoteObj (String destHost, int destPort, Serializer serializer,
                                 String beanName, Class<T> beanType) {
        final Object o = Proxy.newProxyInstance(beanType.getClassLoader(), new Class[] {beanType},
                ((proxy, method, args) -> {
                    final int sequenceId = SequenceIdGenerator.nextId();
                    RpcRequestMessage msg = new RpcRequestMessage(
                            sequenceId,
                            beanName,
                            beanType,
                            method.getName(),
                            method.getReturnType(),
                            method.getParameterTypes(),
                            args
                    );
                    final Destination destination = new Destination(destHost, destPort, serializer);
                    final Channel ch = getChannel(destination);
                    ch.writeAndFlush(msg);
                    DefaultPromise<Object> promise = new DefaultPromise<>(ch.eventLoop());
                    RPC_HANDLER.getPROMISES().put(sequenceId, promise);
                    promise.await();
                    if (promise.isSuccess()) {
                        final Object now = promise.getNow();
                        if (!keepAlive)
                            closeConnect(destination);
                        return now;
                    } else {
                        final Throwable cause = promise.cause();
                        closeConnect(destination);
                        throw new RuntimeException(cause);
                    }
                }));
        return (T) o;
    }

    private Channel getChannel (Destination destination) {
        if (clientChannelMap.getOrDefault(destination, null) == null)
            initChannel(destination);
        return clientChannelMap.get(destination);
    }

    private void initChannel (Destination destination) {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        MessageCodec MESSAGE_CODEC = new MessageCodec(destination.getSerializer());
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        final ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new ProtocolFrameDecoder())
                                //.addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(MESSAGE_CODEC)
                                .addLast(RPC_HANDLER);
                    }
                });
        try {
            final Channel channel = bootstrap.connect(destination.getHost(), destination.getPort()).sync().channel();
            clientChannelMap.put(destination, channel);
            final ChannelFuture channelFuture = channel.closeFuture();
            channelFuture.addListener(future -> group.shutdownGracefully());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 开启服务端监听端口，对外提供服务
    @Override
    public void doOperation4 (SummerAnnotationConfigApplicationContext context) throws Exception {
        if (!Config.providerServer()) {
            return;
        }
        int port = Config.getServerPort();
        final Serializer serializer = Config.getServerSerializer();
        provide0(port, serializer);
    }

    public void provide0(int port, Serializer serializer) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        MessageCodec MESSAGE_CODEC = new MessageCodec(serializer);
        RpcRequestMessageHandler RPC_HANDLER = new RpcRequestMessageHandler(context);
        try {
            ServerBootstrap serverBootStrap = new ServerBootstrap();
            serverBootStrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            final ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new ProtocolFrameDecoder())
                                    //.addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(MESSAGE_CODEC)
                                    .addLast(RPC_HANDLER);
                        }
                    });
            Channel channel = serverBootStrap.bind(port).sync().channel();
            serverChannelMap.put(port, channel);
            final ChannelFuture channelFuture = channel.closeFuture();
            channelFuture.addListener(future -> {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("远程服务提供开启，在端口: " + port + "监听");
    }

    public void closeConnect (Destination destination) {
        Channel channel;
        if ((channel = clientChannelMap.getOrDefault(destination, null)) != null) {
            channel.close();
            clientChannelMap.remove(destination);
        }
    }

    @Override
    public void doOperation5 (SummerAnnotationConfigApplicationContext context, BeanDefinition beanDefinition) {

    }

    @Override
    public void doOperation6 (SummerAnnotationConfigApplicationContext context, Object o) {

    }

    // 对此对象进行检查，如果其中的域包含了@Reference注解，则对其进行注入
    @Override
    public void doOperation7 (SummerAnnotationConfigApplicationContext context, Object o) throws Exception {
        referenceObject(o);
    }

    @Override
    public void doOperation8 (SummerAnnotationConfigApplicationContext context, Object o) {

    }

    public SummerAnnotationConfigApplicationContext getContext() {
        return this.context;
    }
}
