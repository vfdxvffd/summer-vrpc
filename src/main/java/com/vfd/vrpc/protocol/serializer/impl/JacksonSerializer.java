package com.vfd.vrpc.protocol.serializer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vfd.vrpc.protocol.serializer.Serializer;

import java.io.IOException;

/**
 * @PackageName: com.vfd.protocol.serializer.impl
 * @ClassName: JacksonSerializer
 * @Description: jackson方式的序列化与反序列化
 * @author: vfdxvffd
 * @date: 2021/5/10 上午11:07
 */
public class JacksonSerializer implements Serializer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T deserializer(Class<T> clazz, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new RuntimeException("反序列化失败", e);
        }
    }

    @Override
    public <T> byte[] serializer(T o) {
        try {
            return objectMapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }
}
