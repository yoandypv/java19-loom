package com.yoandypv.coroutines;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class PlatformThread {
    public static void main(String[] args) {
        var counter = new AtomicInteger();
        while (true) {
            new Thread(()-> {
                int count = counter.incrementAndGet();
                System.out.println("Thread number = " + count);
                LockSupport.park();
            }).start();
        }
    }
}
