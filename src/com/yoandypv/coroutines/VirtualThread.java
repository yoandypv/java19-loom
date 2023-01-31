package com.yoandypv.coroutines;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class VirtualThread {
    public static void main(String[] args) {
        var counter = new AtomicInteger();
        while (true) {
            Thread.startVirtualThread(()-> {
                int count = counter.incrementAndGet();
                System.out.println("Thread number = " + count);
                LockSupport.park();
            });
        }
    }
}
