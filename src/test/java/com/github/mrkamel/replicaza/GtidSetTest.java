package com.github.mrkamel.replicaza;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GtidSetTest extends TestCase {	
    public GtidSetTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(GtidSetTest.class);
    }
    
    public CuratorZookeeperClient createCuratorZookeeperClient() {
    	CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1", new ExponentialBackoffRetry(1000, 3));
    	curatorFramework.start();
    	
    	return curatorFramework.getZookeeperClient();
    }

    public void testGtidSet() {
    	new GtidSet("/replicaza_test_gtid", createCuratorZookeeperClient());
    }

    public void testSetGtidSet() {
    	GtidSet gtidSet = new GtidSet("/replicaza_test_gtid", createCuratorZookeeperClient());
    	gtidSet.setGtidSet("id:range");
    	
    	assertEquals("id:range", gtidSet.getGtidSet());
    }

    public void testGetGtidSet() {
    	// Already tested
    }
    
    public void testRun() {
    	// Can't be tested
    }
    
    public void testSaveGtidSet() throws Exception {
    	GtidSet gtidSet = new GtidSet("/replicaza_test_gtid", createCuratorZookeeperClient());
    	gtidSet.setGtidSet("id:range");
    	
    	assertEquals("id:range", gtidSet.saveGtidSet());
    	
    	GtidSet newGtidSet = new GtidSet("/replicaza_test_gtid", createCuratorZookeeperClient());
    	
    	assertEquals("id:range", newGtidSet.getGtidSet());
    }
}
