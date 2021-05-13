package com.vfd.vrpc.protocol.serializer.impl;

import com.google.gson.*;
import com.vfd.vrpc.protocol.serializer.Serializer;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @PackageName: com.vfd.protocol.serializer
 * @ClassName: JsonSerializer
 * @Description: gson方式的序列化与反序列化
 * @author: vfdxvffd
 * @date: 2021/5/10 上午10:43
 */
public class GsonSerializer implements Serializer {

    @Override
    public int getID() {
        return 2;
    }

    @Override
    public <T> T deserializer(Class<T> clazz, byte[] bytes) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
        String json = new String(bytes, StandardCharsets.UTF_8);
        return gson.fromJson(json, clazz);
    }

    @Override
    public <T> byte[] serializer(T o) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
        String json = gson.toJson(o);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    private static class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String str = json.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override             //   String.class
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            // class -> json
            return new JsonPrimitive(src.getName());
        }
    }
}
