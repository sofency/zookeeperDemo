package com.sofency.top;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import java.util.concurrent.CountDownLatch;

/**
 * @author sofency
 * @date 2020/9/20 0:51
 * @package IntelliJ IDEA
 * @description zookeeper配置中心
 */
public class ConfigurationCenter implements Watcher {
    private static final String HOST = "localhost:2181";
    private static final Integer SESSION_TIME_OUT = 5000;
    private static final String URL = "/configuration/url";
    private static final String USERNAME = "/configuration/username";
    private static final String PASSWORD = "/configuration/password";
    static ZooKeeper zooKeeper;
    static CountDownLatch countDownLatch = new CountDownLatch(1);
    private Configuration configuration;

    public ConfigurationCenter() {
        initValue();
    }


    public static void main(String[] args) {
        ConfigurationCenter configurationCenter = new ConfigurationCenter();
        try {
            for(int i=0;i<10;i++){
                Thread.sleep(3000);
                System.out.println(configurationCenter.configuration.getUrl());
                System.out.println(configurationCenter.configuration.getUsername());
                System.out.println(configurationCenter.configuration.getPassword());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    //拉取配置文件
    public void initValue() {
        try {
            zooKeeper = new ZooKeeper(HOST,SESSION_TIME_OUT,this);
            countDownLatch.await();//阻塞等待
            //读取配置文件
            String url = new String(zooKeeper.getData(URL,true,null));
            String username = new String(zooKeeper.getData(USERNAME,true,null));
            String password = new String(zooKeeper.getData(PASSWORD,true,null));
            configuration = new Configuration(url,username,password);
        }catch (Exception e){
            e.printStackTrace();
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
            //当配置中心发生变化重新拉取数据
        }else if(watchedEvent.getType() == Event.EventType.NodeDataChanged){
            initValue();
        }
    }
}
