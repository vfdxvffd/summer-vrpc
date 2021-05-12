package com.vfd.vrpc.annotation;

import com.vfd.vrpc.protocol.serializer.Serializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Reference {
    String host() default "";       // 主机
    int port() default -1;          // 端口
    String beanName() default "";   // 指定bean的名字
    Class<? extends Serializer> serializer() default Serializer.class;  // 序列化的方式
}
