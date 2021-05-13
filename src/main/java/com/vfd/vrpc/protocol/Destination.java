package com.vfd.vrpc.protocol;

import com.vfd.vrpc.protocol.serializer.Serializer;

import java.util.Objects;

/**
 * @PackageName: com.vfd.v-rpc.protocol
 * @ClassName: Destination
 * @Description:
 * @author: vfdxvffd
 * @date: 2021/5/13 上午10:10
 */
public class Destination {

    String host;
    int port;
    Serializer serializer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Destination that = (Destination) o;
        return port == that.port && serializer.getID() == that.serializer.getID() && host.equals(that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, serializer.getID());
    }

    public Destination(String host, int port, Serializer serializer) {
        this.host = host;
        this.port = port;
        this.serializer = serializer;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", serializer=" + serializer +
                '}';
    }
}
