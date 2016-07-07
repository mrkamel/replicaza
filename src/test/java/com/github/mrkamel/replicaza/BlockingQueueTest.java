package com.github.mrkamel.replicaza;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BlockingQueueTest extends TestCase {
    public BlockingQueueTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(BlockingQueueTest.class);
    }
    
    public void testBlockingQueue() {
    	new BlockingQueue<Object>(5);
    }
    
    public void testPut() {
    	BlockingQueue<String> queue = new BlockingQueue<String>(5);
    	
    	queue.put("test");
    	
    	assertEquals("test", queue.poll());
    }
    
    public void testPoll() {
    	BlockingQueue<String> queue = new BlockingQueue<String>(5);
    	
    	assertNull(queue.poll());
    	
    	// Already tested
    }
    
    public void testWaitIfNeccessary() {
    	BlockingQueue<String> queue = new BlockingQueue<String>(5);
    	
    	queue.put("test");
    	
		queue.waitIfNeccessary();
    	
    	assertEquals("test", queue.poll());
    }
}
