package com.sofency.top;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author sofency
 * @date 2020/9/20 0:05
 * @package IntelliJ IDEA
 * @description
 */
public class ZookeeperWatcher implements Watcher {
    private static final String HOST = "localhost:2181";
    private static final Integer SESSION_TIME_OUT = 50000;
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    static ZooKeeper zooKeeper;
    public static void main(String[] args) throws IOException, InterruptedException {
        zooKeeper=new ZooKeeper(HOST,SESSION_TIME_OUT,new ZookeeperWatcher());
        countDownLatch.await();
        System.out.println("获取会话的id"+zooKeeper.getSessionId());
        Thread.sleep(50000);
        zooKeeper.close();
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType()== Event.EventType.None){
            if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
                System.out.println("建立连接成功");
                countDownLatch.countDown();
            }
        }else if(watchedEvent.getState()==Event.KeeperState.Disconnected){
            System.out.println("断开连接");
        }else if(watchedEvent.getState()==Event.KeeperState.AuthFailed){
            System.out.println("身份认证失败");
        }
    }
}
