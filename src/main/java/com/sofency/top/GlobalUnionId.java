package com.sofency.top;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * @author sofency
 * @date 2020/9/20 1:17
 * @package IntelliJ IDEA
 * @description 分布式唯一id
 */
public class GlobalUnionId implements Watcher {
    private static final String HOST = "localhost:2181";
    private static final Integer SESSION_TIME_OUT = 5000;
    static ZooKeeper zooKeeper;
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static final String defaultPath = "/uniqueId";

 
    public GlobalUnionId(){
        try{
            zooKeeper = new ZooKeeper(HOST,SESSION_TIME_OUT,this);
            countDownLatch.await();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //生成唯一id
    public String getUnionId(){
        String path = "";
        try{
            path = zooKeeper.create(defaultPath,new byte[0],
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT_SEQUENTIAL);
        }catch (Exception e){
            e.printStackTrace();
        }
        return path.substring(9);
    }

    public static void main(String[] args) {
        GlobalUnionId globalUnionId = new GlobalUnionId();
        for(int i=0;i<8;i++){
            String unionId = globalUnionId.getUnionId();
            System.out.println(unionId);
        }
    }
    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType()== Event.EventType.None){
            if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                System.out.println("建立连接");
                countDownLatch.countDown();
            }else if(watchedEvent.getState() == Event.KeeperState.AuthFailed){
                System.out.println("身份认证失败");
            }else if(watchedEvent.getState() ==  Event.KeeperState.Expired){
                System.out.println("会话失效");
            }else if(watchedEvent.getState() ==  Event.KeeperState.Disconnected){
                System.out.println("断开连接");
            }
        }
    }
}
