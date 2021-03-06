
package com.github.mrkamel.replicaza;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class App {
	public static Config config = null;

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
    	config = new Config(args[0]);

		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(config.getProperty("zookeeper.hosts"), new ExponentialBackoffRetry(1000, 3));
    	curatorFramework.start();
    	
    	LeaderSelector leaderSelector = new LeaderSelector(curatorFramework, "/replicaza_leader", new LeaderListener());
    	leaderSelector.start();
		
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(leaderSelector, curatorFramework));
            
        Thread.currentThread().join();
    }
}
