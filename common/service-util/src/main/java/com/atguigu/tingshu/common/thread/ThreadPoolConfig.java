package com.atguigu.tingshu.common.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @version 1.0
 */

@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        //获取逻辑处理器个数
        int processorsCount = Runtime.getRuntime().availableProcessors();
        processorsCount = processorsCount * 2;
        return new ThreadPoolExecutor(
                processorsCount,
                processorsCount,
                0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                Executors.defaultThreadFactory(),
                (r, executor) -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    executor.execute(r);
                });
    }
}
