package com.sofency.top;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZookeeperConnection{
    public static final String HOST = "localhost:2181";
    public static final Integer SESSION_TIME_OUT = 2000;
    public static void main(String[] args) throws InterruptedException {
        ZooKeeper zooKeeper = null;
        try{
            //计数器对象
            CountDownLatch countDownLatch = new CountDownLatch(1);
            //参数1 服务器的ip和端口号
            //参数2 客户端与服务器之间的会话超时时间
            //监视器对象
            zooKeeper = new ZooKeeper(HOST, SESSION_TIME_OUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
                        System.out.println("连接创建成功");
                        countDownLatch.countDown();
                    }
                }
            });//上述是异步的
            //主线程阻塞等待连接对象的创建完成
            countDownLatch.await();//一直等到减一为止
            //会话id
            System.out.println(zooKeeper.getSessionId());
            //zookeeper.close();//关闭资源
        }catch (Exception e){
            System.out.println("捕捉到异常");
            zooKeeper.close();
        }
    }
}