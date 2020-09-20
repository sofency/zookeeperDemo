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
 * @date 2020/9/19 22:14
 * @package IntelliJ IDEA
 * @description
 */
public class ZookeeperSet {
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
    public void set() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.setData(
                "/set/node1",//节点路径
                "node13".getBytes(),//节点的值
                -1         // 版本号  -1代表版本号不参与更新
        );
        System.out.println("当前的版本号"+stat.getVersion());
    }

    //异步设置
    @Test
    public void set1() throws KeeperException, InterruptedException {
        zooKeeper.setData(
                "/set/node1",//节点路径
                "node13".getBytes(),//节点的值
                -1,         // 版本号  -1代表版本号不参与更新
                new AsyncCallback.StatCallback() {
                    @Override
                    public void processResult(int i, String path, Object ctx, Stat stat) {
                        System.out.println(i);//0代表创建成功
                        System.out.println(path);//节点的路径
                        System.out.println(ctx);//上下文参数
                        System.out.println("打印出版本号"+stat.getVersion());
                    }
                },
                 "i am context"
        );
    }

    @After
    public void close() throws InterruptedException {
        zooKeeper.close();
    }
}
