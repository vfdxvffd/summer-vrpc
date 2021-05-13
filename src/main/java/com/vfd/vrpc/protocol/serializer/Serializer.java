package com.vfd.vrpc.protocol.serializer;

import com.vfd.vrpc.protocol.serializer.impl.FastjsonSerializer;

/**
 * @PackageName: com.vfd.protocol.serializer
 * @ClassName: Serializer
 * @Description: 序列化方式的接口
 * @author: vfdxvffd
 * @date: 2021/5/10 上午10:31
 */
public interface Serializer {

    int getID();

    <T> T deserializer (Class<T> clazz, byte[] bytes);

    <T> byte[] serializer (T o);
}
