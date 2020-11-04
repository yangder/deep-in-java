/**
 * fshows.com
 * Copyright (C) 2013-2020 All Rights Reserved.
 */
package com.yangde.classloader;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 *
 * @author yangdw
 * @version TestCustomClassLoader.java, v 0.1 2020-11-03 19:35
 */
public class TestCustomClassLoader {
    /**
     * 自定义类加载器只需要继承 java.lang.ClassLoader 类，该类有两个核心方法，
     * loadClass(String, boolean)，实现了双亲委派机制，
     * findClass，默认实现是空方法，所以我们自定义类加载器主要是重写findClass方法。
     */
    static class CustomClassLoader extends ClassLoader {

        private String classPath = "/Users/yangdewei/IdeaProjects/fsdeepjava/deep-in-java/week-01/src/main/java/com/yangde/classloader/";
        private String classNameSuffix = ".xlass";

        /**
         * 加载xclass文件,读取到的字节码解码,解码方式:255减去原有值
         */
        private byte[] loadByte(String className) throws IOException {
            FileInputStream fis = null;
            ByteArrayOutputStream bis = null;
            try {
                fis = new FileInputStream(classPath + className + classNameSuffix);
                bis = new ByteArrayOutputStream(1024);
                int b;
                while ((b = fis.read()) != -1) {
                    bis.write((byte) ((byte) 255 - b));
                }
                return bis.toByteArray();
            } catch (IOException e) {
                // 异常日志处理
                throw new IOException();
            } finally {
                if (null != fis) fis.close();
                if (null != bis) bis.close();
            }

        }

        /**
         * 重写findClass方法
         */
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            try {
                byte[] bytes = loadByte(name);
                // defineClass将一个字节数组转为Class对象,这个字节数组是class文件读取后最终的字节数组
                return defineClass(name, bytes, 0, bytes.length);
            } catch (Exception e) {
                // 异常日志处理
                throw new ClassNotFoundException(name);
            }
        }
    }

    /**
     * main方法测试
     * @param args
     */
    public static void main(String[] args) throws Exception {
        CustomClassLoader classLoader = new CustomClassLoader();
        Class clazz = classLoader.loadClass("Hello");
        Object obj = clazz.newInstance();
        for (Method method : clazz.getDeclaredMethods()) {
            method.invoke(obj,null);
        }
    }
}   