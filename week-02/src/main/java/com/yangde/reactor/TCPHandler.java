/**
 * fshows.com
 * Copyright (C) 2013-2021 All Rights Reserved.
 */
package com.yangde.reactor;

import cn.hutool.core.date.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author yangdw
 */
@Slf4j
@Component
public class TCPHandler implements Runnable {

    private static ThreadPoolTaskExecutor taskExecutor;

    static {
        String threadPoolName = "tcpHandlerExecutor";
        taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.initialize();
        taskExecutor.setThreadNamePrefix("TCPHandler-");
        taskExecutor.setKeepAliveSeconds(10);
        taskExecutor.setMaxPoolSize(16);
        taskExecutor.setCorePoolSize(8);
        // 等待队列容量
        taskExecutor.setQueueCapacity(2000);
        // 设置拒绝策略,当前策略：如等待队列已满则记录日志，并丢弃任务
        taskExecutor.setRejectedExecutionHandler((r, executor) -> log.error("【异步线程池】等待队列超过最大长度限制，拒绝任务！ 线程池名称={}，task={}", threadPoolName, r.toString()));
        // 通过包装原有任务来捕获可能发生的异步任务抛出的异常
        taskExecutor.setTaskDecorator(runnable -> {
            Runnable d = () -> {
                long currentTime = SystemClock.now();
                try {
                    runnable.run();
                } catch (Exception e) {
                    log.warn("【异步线程池】异步任务执行过程中发生异常！耗时={}ms, 线程池名称={}", e, (SystemClock.now() - currentTime), threadPoolName);
                } finally {
                }
            };
            return d;
        });


    }

    private final SelectionKey sk;
    private final SocketChannel sc;
    private HandlerState state;

    public TCPHandler(SelectionKey sk, SocketChannel sc) {
        this.sk = sk;
        this.sc = sc;
        // 初始化状态为READING
        state = new ReadState();
    }

    @Override
    public void run() {
        try {
            state.handle(this, sk, sc, taskExecutor);

        } catch (IOException e) {
            System.out.println("A client 被关闭");
            closeChannel();
        }
    }

    public void closeChannel() {
        try {
            sk.cancel();
            sc.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void setState(HandlerState state) {
        this.state = state;
    }
}