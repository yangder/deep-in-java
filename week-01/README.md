#### 1. 实现一个自定义的classloader，加载如下的文件，内容需要解码，读取的字节码需要解码，解码方式：255减去原有值，并执行成功。
```java
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
        Method method = clazz.getDeclaredMethod("hello");
        method.invoke(obj);
    }
}   
```
#### **运行结果**
``` text
Hello, classLoader!
```
#### **2. 分析以下GC日志，尽可能详细的标注出GC发生时相关的信息。**
``` text
# 前面的时间格式是固定的，114.015表示GC和项目运行的相对时间，单位为秒，[Times: user=0.86 sys=0.00, real=0.28 secs]表示GC用户态消耗的CPU时间、内核态消耗的CPU时间、操作从开始到结束所经过的等待耗时，单位为秒

# 初始标记，当前老年代的容量为2097152KK，在使用了106000K时开始进行CMS垃圾回收；当前堆内存使用1084619K，容量为3984640K，耗时0.2824583s，会STW
2020-10-29T21:19:19.488+0800: 114.015: [GC (CMS Initial Mark) [1 CMS-initial-mark: 106000K(2097152K)] 1084619K(3984640K), 0.2824583 secs] [Times: user=0.86 sys=0.00, real=0.28 secs]

# 并发标记开始，遍历整个老年代并且标记活着的对象，0.160/0.160 secs表示该阶段持续的时间和时钟时间，耗时0.16秒，对上一阶段被标记又发生引用改变的对象打上Dirty Card标记
2020-10-29T21:19:19.771+0800: 114.298: [CMS-concurrent-mark-start]
2020-10-29T21:19:19.931+0800: 114.458: [CMS-concurrent-mark: 0.160/0.160 secs] [Times: user=0.32 sys=0.03, real=0.16 secs]

# 把上一个阶段被标记为Dirty Card的对象以及可达的对象重新遍历标记，完成后清除Dirty Card标记
2020-10-29T21:19:19.931+0800: 114.459: [CMS-concurrent-preclean-start]
2020-10-29T21:19:19.998+0800: 114.525: [CMS-concurrent-preclean: 0.065/0.066 secs] [Times: user=0.05 sys=0.01, real=0.06 secs]

# 可终止的并发预清理，为Final Remark做最后准备，耗时较长，5.038s
2020-10-29T21:19:19.998+0800: 114.525: [CMS-concurrent-abortable-preclean-start]CMS: abort preclean due to time 
2020-10-29T21:19:25.072+0800: 119.599: [CMS-concurrent-abortable-preclean: 5.038/5.073 secs] [Times: user=7.72 sys=0.50, real=5.08 secs]

# 最终标记，会STW,标记整个年老代的所有的存活对象，年轻代容量为1887488K,使用了1279357K
2020-10-29T21:19:25.076+0800: 119.603: [GC (CMS Final Remark) [YG occupancy: 1279357 K (1887488 K)]
# STW状态标记存活对象
2020-10-29T21:19:25.076+0800: 119.603: [Rescan (parallel) , 0.3120602 secs]
# STW状态处理弱引用
2020-10-29T21:19:25.388+0800: 119.915: [weak refs processing, 0.0015920 secs]
# STW状态卸载无用类
2020-10-29T21:19:25.390+0800: 119.917: [class unloading, 0.0517863 secs]
# STW状态清理类级元数据、内部化字符串的符号、字符串表
2020-10-29T21:19:25.441+0800: 119.969: [scrub symbol table, 0.0212825 secs]
# 老年代容量为2097152K，使用了106000K，堆内存容量为3984640K，使用了1385358K，耗时 0.3959182s
2020-10-29T21:19:25.463+0800: 119.990: [scrub string table, 0.0022435 secs][1 CMS-remark: 106000K(2097152K)] 1385358K(3984640K), 0.3959182 secs] [Times: user=1.33 sys=0.00, real=0.40 secs]

# 并发清除，清除没有标记的无用对象并回收内存
2020-10-29T21:19:25.473+0800: 120.000: [CMS-concurrent-sweep-start]
2020-10-29T21:19:25.540+0800: 120.067: [CMS-concurrent-sweep: 0.067/0.067 secs] [Times: user=0.18 sys=0.02, real=0.06 secs]
# 重新设置CMS算法内部的数据结构
2020-10-29T21:19:25.540+0800: 120.068: [CMS-concurrent-reset-start]
2020-10-29T21:19:25.544+0800: 120.071: [CMS-concurrent-reset: 0.003/0.003 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]
```
#### **3. 标注以下启动参数每个参数的含义**
 >java -Denv=PRO -server -Xms4g -Xmx4g -Xmn2g -XX:MaxDirectMemorySize=512m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m -XX:-UseBiasedLocking -XX:-UseCounterDecay -XX:AutoBoxCacheMax=10240 -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 -XX:+UseCMSInitiatingOccupancyOnly -XX:MaxTenuringThreshold=6 -XX:+ExplicitGCInvokesConcurrent -XX:+ParallelRefProcEnabled -XX:+PerfDisableSharedMem -XX:+AlwaysPreTouch -XX:-OmitStackTraceInFastThrow  -XX:+ExplicitGCInvokesConcurrent -XX:+ParallelRefProcEnabled -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/devjava/logs/ -Xloggc:/home/devjava/logs/lifecircle-tradecore-gc.log -XX:+PrintGCApplicationStoppedTime -XX:+PrintGCDateStamps -XX:+PrintGCDetails -javaagent:/home/devjava/ArmsAgent/arms-bootstrap-1.7.0-SNAPSHOT.jar -jar /home/devjava/lifecircle-tradecore/app/lifecircle-tradecore.jar

  | **参数** | **含义** |
  | --- | ---  |
  | -Denv=PRO | 设置项目启动环境参数,拉取相应Apollo配置  |
  | -server |	开启服务端模式  |
  | -Xms4g	| 设置堆初始值为4g |
  | -Xmx4g	| 设置堆最大值为4g |
  | -Xmn2g	| 设置堆年轻代内存为2g |
  | -XX:MaxDirectMemorySize=512m	| 设置堆外内存为512m |
  | -XX:MetaspaceSize=128m	| 设置jvm元空间最小为128m |
  | -XX:MaxMetaspaceSize=512m	| 设置jvm元空间最大为512m |
  | -XX:-UseBiasedLocking	| 禁用偏向锁定功能 |
  | -XX:-UseCounterDecay	| 禁止JIT调用计数器衰减 |
  | -XX:AutoBoxCacheMax=10240	| 加大Integer Cache最大值为10240 |
  | -XX:+UseConcMarkSweepGC	| 设置老年代使用CMS收集器 |
  | -XX:CMSInitiatingOccupancyFraction=75 | 设置触发执行CMS回收当前年代区内存使用率为75% |
  | -XX:+UseCMSInitiatingOccupancyOnly	| 禁止hostspot自行触发CMS GC,只根据占用情况作为开始执行CMS收集的标准 |
  | -XX:MaxTenuringThreshold=6	| 新生代对象经过6次YGC进入老生代 |
  | -XX:+ExplicitGCInvokesConcurrent	| 调用系统GC时会执行CMS而不是FG |
  | -XX:+ParallelRefProcEnabled	| 并行处理Reference,如WeakReference |
  | -XX:+PerfDisableSharedMem	| 关闭PerfData写入，避免高IO场景GC时因为写PerfData文件被阻塞 |
  | -XX:+AlwaysPreTouch	|  启动时预申请内存 |
  | -XX:-OmitStackTraceInFastThrow	| 关闭jvm错误堆栈信息优化（不输出） |
  | -XX:+HeapDumpOnOutOfMemoryError	| 设置OOM异常，输出Heap Dump到指定文件 |
  | -XX:HeapDumpPath=/home/devjava/logs/ |	设置Heap Dump日志文件的保存路径 |
  | -Xloggc:/home/devjava/logs/lifecircle-tradecore-gc.log	| 设置gc日志输出的文件路径 |
  | -XX:+PrintGCApplicationStoppedTime	| 设置gc日志中打印应用暂停时间 |
  | -XX:+PrintGCDateStamps	| 设置gc日志打印日期时间戳 |
  | -XX:+PrintGCDetails	| 设置gc日志打印详细信息 |
  | -javaagent:/home/devjava/ArmsAgent/arms-bootstrap-1.7.0-SNAPSHOT.jar	| 加载arms的包,应用arms监控 |
  | -jar/home/devjava/lifecircle-tradecore/app/lifecircle-tradecore.jar	| 指定要启动的项目jar包


 
