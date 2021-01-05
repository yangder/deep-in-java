/**
 * fshows.com
 * Copyright (C) 2013-2021 All Rights Reserved.
 */
package com.yangde.reactor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author yangdw
 */
public class WriteState implements HandlerState {

    public WriteState() {
    }

    @Override
    public void changeState(TCPHandler h) {
        h.setState(new ReadState());
    }

    @Override
    public void handle(TCPHandler h, SelectionKey sk, SocketChannel sc,
                       ThreadPoolTaskExecutor pool) throws IOException {
        String str = "测试"
                + sc.socket().getLocalSocketAddress().toString() + "\r\n";
        ByteBuffer buf = ByteBuffer.wrap(str.getBytes());

        while (buf.hasRemaining()) {
            sc.write(buf);
        }

        h.setState(new ReadState());
        sk.interestOps(SelectionKey.OP_READ);
        sk.selector().wakeup();
    }

}