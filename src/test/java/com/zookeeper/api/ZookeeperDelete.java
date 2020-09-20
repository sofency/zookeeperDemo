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
 * @date 2020/9/19 22:39
 * @package IntelliJ IDEA
 * @description
 */
public class ZookeeperDelete {
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
    public void delete() throws KeeperException, InterruptedException {
        zooKeeper.delete(
                "/set/node1",//节点路径
                -1         // 版本号  -1代表版本号不参与更新
        );
    }

    //异步设置
    @Test
    public void deleteAsync() throws KeeperException, InterruptedException {
        zooKeeper.delete(
                "/delete/node1",//节点路径
                -1,         // 版本号  -1代表版本号不参与更新
                new AsyncCallback.VoidCallback() {
                    @Override
                    public void processResult(int i, String s, Object o) {
                        System.out.println(i);//0代表删除成功
                        System.out.println(s);//当前删除节点的路径
                        System.out.println(o);//上下文环境
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
