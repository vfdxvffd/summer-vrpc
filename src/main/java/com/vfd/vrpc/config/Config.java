package com.vfd.vrpc.config;

import com.vfd.vrpc.protocol.serializer.Serializer;
import com.vfd.vrpc.protocol.serializer.impl.FastjsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @PackageName: com.vfd.v-rpc.config
 * @ClassName: Config
 * @Description:
 * @author: vfdxvffd
 * @date: 2021/5/12 下午4:07
 */
public abstract class Config {
    static Properties properties;
    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static int getServerPort () {
        String value = properties.getProperty("summer.rpc.server.port");
        if(value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static String getDestHost () throws Exception {
        String value = properties.getProperty("summer.rpc.dest.host");
        if(value == null) {
            throw new Exception("unknown host");
        } else {
            return value;
        }
    }

    public static int getDestPort () throws Exception {
        String value = properties.getProperty("summer.rpc.dest.port");
        if(value == null) {
            throw new Exception("unknown port");
        } else {
            return Integer.parseInt(value);
        }
    }

    public static Serializer getDestSerializer () throws Exception {
        String value = properties.getProperty("summer.rpc.dest.serializer");
        return getSerializer(value);
    }

    private static Serializer getSerializer(String value) throws Exception {
        if(value == null) {
            return new FastjsonSerializer();
        } else {
            try {
                final Object o = Class.forName(value).newInstance();
                if (o instanceof Serializer)
                    return (Serializer) o;
                else
                    throw new Exception("invalid serializer type: " + value);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new Exception(value + " not found");
            }
        }
    }

    public static Serializer getServerSerializer () throws Exception {
        String value = properties.getProperty("summer.rpc.server.serializer");
        return getSerializer(value);
    }

    public static int getMaxMessageLength () {
        String value = properties.getProperty("summer.rpc.message.maxLength");
        if(value == null) {
            return 1024;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static boolean providerServer () {
        String value = properties.getProperty("summer.rpc.server.provide");
        if(value == null) {
            return false;
        } else {
            return Boolean.parseBoolean(value);
        }
    }

    public static boolean keepAlive () {
        String value = properties.getProperty("summer.rpc.client.keepAlive");
        if(value == null) {
            return true;
        } else {
            return Boolean.parseBoolean(value);
        }
    }
}
