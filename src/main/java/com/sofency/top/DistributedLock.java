package com.sofency.top;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author sofency
 * @date 2020/9/20 19:02
 * @package IntelliJ IDEA
 * @description 分布式锁
 */
public class DistributedLock {
    private static final String HOST = "localhost:2181";
    private static final Integer SESSION_TIME_OUT = 5000;
    //计数器对象
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private ZooKeeper zooKeeper;
    private static final String LOCK_ROOT_PATH = "/Locks";
    private static final String LOCK_NODE_NAME = "Lock_";

    //初始化
    public DistributedLock(){
        try{
            zooKeeper = new ZooKeeper(HOST, SESSION_TIME_OUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType()==Event.EventType.None){
                        if(watchedEvent.getState()== Event.KeeperState.SyncConnected){
                            System.out.println("创建连接成功");
                            countDownLatch.countDown();
                        }
                    }
                }
            });
            countDownLatch.await();//等待
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //获取锁
    public void acquireLock() throws KeeperException, InterruptedException {
        createLock();//创建锁
        //尝试获取锁
        attemptLock();
    }

    //创建锁节点
    private void createLock() throws KeeperException, InterruptedException {
        //创建是持久化根节点
        Stat stat = zooKeeper.exists(LOCK_ROOT_PATH,false);
        if(stat==null){
            zooKeeper.create(LOCK_ROOT_PATH,new byte[0],
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        //创建临时有序的节点
        String localPath = zooKeeper.create(LOCK_ROOT_PATH + "/" + LOCK_NODE_NAME, new byte[0],
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL//临时有序节点
        );
        System.out.println("临时有序节点创建完毕"+localPath);
    }

    Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType()== Event.EventType.NodeDeleted){//删除掉
                synchronized (this){
                    notifyAll();//唤醒占有当前对象的锁
                }
            }
        }
    };

    //尝试获取锁
    private void attemptLock() throws KeeperException, InterruptedException {
        //获取Locks下的所有子节点
        List<String> list  = zooKeeper.getChildren(LOCK_ROOT_PATH,false);
        //对子节点进行排序
        Collections.sort(list);
        int index = list.indexOf(LOCK_NODE_NAME);
        if(index == 0){
            System.out.println("获取锁成功");
            return;
        }else{
            String path = list.get(index-1);//上一个节点的索引位置
            Stat exists = zooKeeper.exists(LOCK_ROOT_PATH + "/" + path, watcher);
            //尝试获取锁
            if (exists != null) {
                synchronized (watcher) {
                    watcher.wait();//等待
                }
            }
            attemptLock();//尝试获取锁
        }

    }

    public static void main(String[] args) throws KeeperException, InterruptedException {
        DistributedLock distributedLock = new DistributedLock();
        distributedLock.createLock();
        distributedLock.close();
    }

    //释放锁
    public void close() throws InterruptedException {
        zooKeeper.close();
    }
}
