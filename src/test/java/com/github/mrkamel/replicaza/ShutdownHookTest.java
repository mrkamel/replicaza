package com.github.mrkamel.replicaza;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ShutdownHookTest extends TestCase {
	public ShutdownHookTest(String testName) {
    	super(testName);
    }

    public static Test suite() {
    	return new TestSuite(ShutdownHookTest.class);
    }
    
    private ShutdownHook createShutdownHook() {
    	CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1", new ExponentialBackoffRetry(1000, 3));
    	curatorFramework.start();
    	
    	LeaderSelector leaderSelector = new LeaderSelector(curatorFramework, "/replicaza_test_leader", new LeaderSelectorListener() {
    		public void takeLeadership(CuratorFramework framework) {
    			// nothing
    		}

    		public void stateChanged(CuratorFramework framework, ConnectionState state) {
				// nothing
			}
    	});
    	
    	leaderSelector.start();
    	
    	return new ShutdownHook(leaderSelector, curatorFramework);
    }

    public void testShutdownHook() {
    	// Already tested
    }

    public void testRun() throws Exception {
    	createShutdownHook().run();
    }
}
