package com.vfd.vrpc;

import io.netty.channel.Channel;

/**
 * @PackageName: com.vfd.v-rpc
 * @ClassName: VRpcHandler
 * @Description:
 * @author: vfdxvffd
 * @date: 2021/5/12 下午11:44
 */
public class VRpcHandler {

    VRpc vRpc = new VRpc();

    public VRpcHandler(VRpc vRpc) {
        this.vRpc = vRpc;
    }

    public void keepAlive () {
        vRpc.keepAlive = true;
    }

    public void keepNonAlive () {
        vRpc.keepAlive = false;
    }

    public void closeProvideServer (int port) {
        final Channel channel = vRpc.channelMap.getOrDefault(port, null);
        if (channel != null)    channel.close();
    }


}
