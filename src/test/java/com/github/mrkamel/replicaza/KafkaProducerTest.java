package com.github.mrkamel.replicaza;

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
    	return new KafkaProducer("127.0.0.1:9092", 1);
    }
    
    public void testKafkaProducer() {
    	createKafkaProducer();
    }
    
    public void testSend() {
    	createKafkaProducer().send("test-topic", "parition-key", "message");
    }
}
