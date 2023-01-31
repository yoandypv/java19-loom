package com.yoandypv.coroutines;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ThroughputPlatformSingleThread {

    public static void main(String[] args) {
        try (var executor =  Executors.newSingleThreadExecutor()) {
            IntStream.range(0, 3000).forEach(i -> executor.submit(()->{
                Thread.sleep(Duration.ofSeconds(1));
                System.out.println(i);
                return i;
            }));
        }
    }
}
