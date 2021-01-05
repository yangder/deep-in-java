/**
 * fshows.com
 * Copyright (C) 2013-2021 All Rights Reserved.
 */
package com.yangde.reactor;

import java.io.IOException;

/**
 * @author yangdw
 */
public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            TCPReactor reactor = new TCPReactor(9999);
            reactor.run();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}   