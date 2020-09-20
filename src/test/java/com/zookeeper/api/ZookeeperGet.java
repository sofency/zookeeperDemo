package com.zookeeper.api;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author sofency
 * @date 2020/9/19 22:45
 * @package IntelliJ IDEA
 * @description
 */
public class ZookeeperGet {
    private static final String HOST = "localhost:2181";
    private static final int SESSION_TIME_OUT = 20000;
    private ZooKeeper zooKeeper;

    @Before
    public void init() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(HOST, SESSION_TIME_OUT, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                    System.out.println("成功创建连接");
                }
            }
        });
        countDownLatch.await();
    }

    //同步设置
    @Test
    public void get() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        byte[] data = zooKeeper.getData(
                "/get/node",//节点路径
                false,
                stat        // 版本号  -1代表版本号不参与更新
        );
        System.out.println("获取到的数据是"+new String(data));
        System.out.println("当前的版本号"+stat.getVersion());
    }

    //异步设置
    @Test
    public void getAsync() throws KeeperException, InterruptedException {
        zooKeeper.getData(
                "/get/node",//节点路径
                false,
                new AsyncCallback.DataCallback() {
                    @Override
                    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                        System.out.println(i);//0标识获取成功
                        System.out.println(s);//获取的路径
                        System.out.println("获取的数据是"+ new String(bytes));
                        System.out.println("版本号是"+stat.getVersion());
                    }
                }, "i am context"
        );
    }

    @After
    public void close() throws InterruptedException {
        zooKeeper.close();
    }
}
