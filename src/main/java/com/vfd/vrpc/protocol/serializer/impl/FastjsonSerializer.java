package com.vfd.vrpc.protocol.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.vfd.vrpc.protocol.serializer.Serializer;

import java.nio.charset.StandardCharsets;

/**
 * @PackageName: com.vfd.protocol.serializer.impl
 * @ClassName: FastjsonSerializer
 * @Description: fastjson方式的序列化与反序列化
 * @author: vfdxvffd
 * @date: 2021/5/10 上午10:56
 */
public class FastjsonSerializer implements Serializer {
    @Override
    public int getID() {
        return 1;
    }

    @Override
    public <T> T deserializer(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), clazz);
    }

    @Override
    public <T> byte[] serializer(T o) {
        return JSON.toJSONString(o).getBytes(StandardCharsets.UTF_8);
    }
}
