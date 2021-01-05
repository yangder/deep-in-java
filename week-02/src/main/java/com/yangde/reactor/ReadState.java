/**
 * fshows.com
 * Copyright (C) 2013-2021 All Rights Reserved.
 */
package com.yangde.reactor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author yangdw
 */
@Component
@Slf4j
public class ReadState implements HandlerState {

    private SelectionKey sk;

    public ReadState() {
    }

    @Override
    public void changeState(TCPHandler h) {
        h.setState(new WorkState());
    }

    @Override
    public void handle(TCPHandler h, SelectionKey sk, SocketChannel sc,
                       ThreadPoolTaskExecutor pool) throws IOException {
        this.sk = sk;
        // non-blocking下不可用Readers
        byte[] arr = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(arr);

        int numBytes = sc.read(buf);
        if (numBytes == -1) {
            log.warn("A client 被关闭");
            h.closeChannel();
            return;
        }
        String str = new String(arr, "UTF-8");
        if ((str != null) && !str.equals(" ")) {
            h.setState(new WorkState());
            pool.execute(new WorkerThread(h, str));
            log.info(sc.socket().getRemoteSocketAddress().toString() + " > " + str);
        }

    }


    synchronized void process(TCPHandler h, String str) {
        h.setState(new WriteState());
        this.sk.interestOps(SelectionKey.OP_WRITE);
        this.sk.selector().wakeup();
    }

    /**
     * 工作线程
     */
    class WorkerThread implements Runnable {

        TCPHandler h;
        String str;

        public WorkerThread(TCPHandler h, String str) {
            this.h = h;
            this.str = str;
        }

        @Override
        public void run() {
            process(h, str);
        }

    }
}