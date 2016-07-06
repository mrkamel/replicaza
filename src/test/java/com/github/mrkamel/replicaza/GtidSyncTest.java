package com.github.mrkamel.replicaza;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GtidSyncTest extends TestCase {	
    public GtidSyncTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(GtidSyncTest.class);
    }
    
    public CuratorZookeeperClient createCuratorZookeeperClient() {
    	CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1", new ExponentialBackoffRetry(1000, 3));
    	curatorFramework.start();
    	
    	return curatorFramework.getZookeeperClient();
    }

    public void testGtidSet() {
    	new GtidSync("/replicaza_test_gtid", createCuratorZookeeperClient());
    }

    public void testSetGtidSet() {
    	GtidSync gtidSync = new GtidSync("/replicaza_test_gtid", createCuratorZookeeperClient());
    	gtidSync.setGtidSet("id:range");
    	
    	assertEquals("id:range", gtidSync.getGtidSet());
    }

    public void testGetGtidSet() {
    	// Already tested
    }
    
    public void testRun() {
    	// Can't be tested
    }
    
    public void testSaveGtidSet() throws Exception {
    	GtidSync gtidSync = new GtidSync("/replicaza_test_gtid", createCuratorZookeeperClient());
    	gtidSync.setGtidSet("id:range");
    	
    	assertEquals("id:range", gtidSync.saveGtidSet());
    	
    	GtidSync newGtidSync = new GtidSync("/replicaza_test_gtid", createCuratorZookeeperClient());
    	
    	assertEquals("id:range", newGtidSync.getGtidSet());
    }
}
