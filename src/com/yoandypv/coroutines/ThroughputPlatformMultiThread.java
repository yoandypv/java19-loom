package com.yoandypv.coroutines;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ThroughputPlatformMultiThread {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try (var executor =  Executors.newThreadPerTaskExecutor(Executors.defaultThreadFactory())) {
            IntStream.range(0, 30000).forEach(i -> executor.submit(()->{
                Thread.sleep(Duration.ofSeconds(1));
                System.out.println(i);
                return i;
            }));
        }

        long endTime = System.currentTimeMillis();
        long total = endTime-startTime;
        System.out.println("Total time: (thread per task)" + total );
    }
}
