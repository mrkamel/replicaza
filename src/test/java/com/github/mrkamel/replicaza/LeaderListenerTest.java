package com.github.mrkamel.replicaza;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LeaderListenerTest extends TestCase {
    public LeaderListenerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(LeaderListenerTest.class);
    }

    public void testLeaderListener() {
    	CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1", new ExponentialBackoffRetry(1000, 3));
    	
    	curatorFramework.start();
    	
    	new LeaderListener(new GtidSet("/replicaza_test_gtid", curatorFramework.getZookeeperClient()));
    }
    
    public void testTakeLeadership() {
    	// Can't be tested
    }
}
