package com.vfd.vrpc;

import com.vfd.vrpc.config.Config;
import com.vfd.vrpc.protocol.Destination;
import com.vfd.vrpc.protocol.serializer.Serializer;
import io.netty.channel.Channel;

/**
 * @PackageName: com.vfd.v-rpc
 * @ClassName: VRpcHandler
 * @Description:
 * @author: vfdxvffd
 * @date: 2021/5/12 下午11:44
 */
public class VRpcHandler {

    VRpcAdapter vRpcAdapter;

    public VRpcHandler(VRpcAdapter vRpcAdapter) {
        this.vRpcAdapter = vRpcAdapter;
    }

    public void keepAlive () {
        vRpcAdapter.keepAlive = true;
    }

    public void keepNonAlive () {
        vRpcAdapter.keepAlive = false;
    }

    public void closeProvider (int port) {
        final Channel channel = vRpcAdapter.serverChannelMap.getOrDefault(port, null);
        if (channel != null)    channel.close();
        vRpcAdapter.serverChannelMap.remove(port);
    }

    public void closeAllProvider () {
        for (Integer port : vRpcAdapter.serverChannelMap.keySet()) {
            closeProvider(port);
        }
    }

    public void startProvideServer (int port, Serializer serializer) throws Exception {
        vRpcAdapter.provide0(port, serializer);
    }

    public void startProvideServer (int port) throws Exception {
        startProvideServer(port, Config.getServerSerializer());
    }

    public void disableReference (String host, int port, Serializer serializer) {
        vRpcAdapter.closeConnect(new Destination(host, port, serializer));
    }

    public void disableAllReference () {
        for (Destination destination : vRpcAdapter.clientChannelMap.keySet()) {
            vRpcAdapter.closeConnect(destination);
        }
    }
}
