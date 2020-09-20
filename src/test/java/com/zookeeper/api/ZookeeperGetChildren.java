package com.zookeeper.api;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author sofency
 * @date 2020/9/19 23:01
 * @package IntelliJ IDEA
 * @description
 */
public class ZookeeperGetChildren {
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
    public void getChildren() throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        List<String> children = zooKeeper.getChildren(
                "/get",//节点路径
                false,
                stat
        );
        children.forEach(System.out::println);
        System.out.println("当前的版本是"+stat.getVersion());
    }

    //异步设置
    @Test
    public void getChildrenNode() throws KeeperException, InterruptedException {
        zooKeeper.getChildren(
                "/get",//节点路径
                false,
                new AsyncCallback.ChildrenCallback() {
                    @Override
                    public void processResult(int i, String path, Object ctx, List<String> list) {
                        System.out.println(i);//0代表创建成功
                        System.out.println(path);//节点的路径
                        System.out.println(ctx);//上下文参数
                        list.forEach(System.out::println);
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
