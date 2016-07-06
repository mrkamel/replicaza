package com.github.mrkamel.replicaza;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class KafkaProducerTest extends TestCase {
    public KafkaProducerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(KafkaProducerTest.class);
    }
    
    private KafkaProducer createKafkaProducer() {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1", new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();
        	
        GtidSync gtidSync = new GtidSync("/replicaza_test_gtid", curatorFramework.getZookeeperClient());
        	
    	return new KafkaProducer("127.0.0.1:9092", 1, gtidSync);
    }
    
    public void testKafkaProducer() {
    	createKafkaProducer();
    }
    
    public void testSend() {
    	createKafkaProducer().send("topic", "gtidSet", "operation", "id", "time");
    }
    
    public void testFlush() {
    	KafkaProducer kafkaProducer = createKafkaProducer();
    	
    	kafkaProducer.send("topic", "gtidSet", "operation", "id", "time");
    	kafkaProducer.send("topic", "gtidSet", "operation", "id", "time");
    	
    	kafkaProducer.flush();
    }
}
