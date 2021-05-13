package com.vfd.vrpc.protocol.serializer.impl;

import com.vfd.vrpc.protocol.serializer.Serializer;

import java.io.*;

/**
 * @PackageName: com.vfd.protocol.serializer
 * @ClassName: JDKSerializer
 * @Description: jdk方式的序列化与反序列化
 * @author: vfdxvffd
 * @date: 2021/5/10 上午10:52
 */
public class JDKSerializer implements Serializer {

    @Override
    public int getID() {
        return 4;
    }

    @SuppressWarnings("all")
    @Override
    public <T> T deserializer(Class<T> clazz, byte[] bytes) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("反序列化失败", e);
        }
    }

    @Override
    public <T> byte[] serializer(T o) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }
}
