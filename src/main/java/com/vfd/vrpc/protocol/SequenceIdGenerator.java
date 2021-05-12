package com.vfd.vrpc.protocol;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @PackageName: com.vfd.protocol
 * @ClassName: SequenceIdGenerator
 * @Description:
 * @author: vfdxvffd
 * @date: 2021/5/10 上午10:10
 */
public class SequenceIdGenerator {
    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId() {
        return id.incrementAndGet();
    }
}

