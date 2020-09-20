package com.zookeeper.watcher;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author sofency
 * @date 2020/9/20 0:23
 * @package IntelliJ IDEA
 * @description
 */
public class ZookeeperWatcherExists {
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
                System.out.println("path"+watchedEvent.getPath());
                System.out.println("eventType"+watchedEvent.getType());
            }
        });
        countDownLatch.await();
    }

    //同步设置
    @Test
    public void exist() throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(
                "/watcher",//节点路径
                true
        );

        Thread.sleep(50000);
        System.out.println("当前的版本号"+stat.getVersion());
    }

    //异步设置
    @Test
    public void existNode() throws KeeperException, InterruptedException {
        zooKeeper.exists(
                "/watcher",//节点路径
                new Watcher() {
                    @Override
                    public void process(WatchedEvent watchedEvent) {
                        System.out.println("自定义watcher");
                        System.out.println(watchedEvent.getType());
                        System.out.println(watchedEvent.getPath());
                    }
                }
        );
    }

    @After
    public void close() throws InterruptedException {
        zooKeeper.close();
    }
}
