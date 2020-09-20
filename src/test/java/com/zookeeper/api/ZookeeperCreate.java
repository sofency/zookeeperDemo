package com.zookeeper.api;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author sofency
 * @date 2020/7/5 19:36
 * @package IntelliJ IDEA
 * @description
 */
public class ZookeeperCreate {
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

    //创建节点
    @Test
    public void createNode() throws KeeperException, InterruptedException {
        //同步创建节点
        zooKeeper.create(
                "/create/node1",//节点路径
                "node".getBytes(),//节点数据
                ZooDefs.Ids.OPEN_ACL_UNSAFE,//权限列表 world:anyone
                CreateMode.PERSISTENT_SEQUENTIAL  //持久化顺序节点  节点的类型
        );
    }

    //异步创建节点
    @Test
    public void createNode6() throws KeeperException, InterruptedException {
        //同步创建节点
        zooKeeper.create(
                "/create/node6",//节点路径
                "node6".getBytes(),//节点数据
                ZooDefs.Ids.OPEN_ACL_UNSAFE,//权限列表 world:anyone
                CreateMode.PERSISTENT_SEQUENTIAL,  //持久化顺序节点  节点的类型
                new AsyncCallback.StringCallback() {
                    @Override
                    public void processResult(int i, String path, Object ctx, String name) {
                        System.out.println(i);//0代表创建成功
                        System.out.println(path);//节点的路径
                        System.out.println(name);//节点的路径
                        System.out.println(ctx);//上下文参数
                    }
                },
                "i am context"
        );
        System.out.println("创建结束");

    }

    //只读
    @Test
    public void createNode1() throws KeeperException, InterruptedException {
        zooKeeper.create(
                "/create/node2",
                "node2".getBytes(),
                ZooDefs.Ids.READ_ACL_UNSAFE,//只读节点
                CreateMode.PERSISTENT);
    }

    @Test
    public void createNode2() throws KeeperException, InterruptedException {
        //world授权模式
        List<ACL> acls = new ArrayList<>();
        //授权模式和授权对象
        Id id  = new Id("world","anyone");
        acls.add(new ACL(ZooDefs.Perms.READ,id));
        acls.add(new ACL(ZooDefs.Perms.WRITE,id));
        zooKeeper.create(
                "/create/node3",
                "node3".getBytes(),
                acls,
                CreateMode.PERSISTENT
        );
    }

    //ip授权模式
    @Test
    public void createNode3() throws KeeperException, InterruptedException {
        //ip授权模式
        List<ACL> acls = new ArrayList<>();
        //授权模式和授权对象
        Id id  = new Id("ip","localhost");
        acls.add(new ACL(ZooDefs.Perms.ALL,id));
        zooKeeper.create(
                "/create/node4",
                "node4".getBytes(),
                acls,
                CreateMode.PERSISTENT
        );
    }
    //auth授权模式
    @Test
    public void createNode4() throws KeeperException, InterruptedException {
        zooKeeper.addAuthInfo("digest","sofency:12345".getBytes());//添加授权的用户
        //ip授权模式
        List<ACL> acls = new ArrayList<>();
        //授权模式和授权对象
        Id id  = new Id("auth","sofency");
        acls.add(new ACL(ZooDefs.Perms.ALL,id));
        zooKeeper.create(
                "/create/node5",
                "node5".getBytes(),
                acls,
                CreateMode.PERSISTENT
        );
    }

    @After
    public void close() throws InterruptedException {
        zooKeeper.close();
    }
}
