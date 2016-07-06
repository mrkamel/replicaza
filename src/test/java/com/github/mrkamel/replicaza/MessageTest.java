package com.github.mrkamel.replicaza;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MessageTest extends TestCase {	
    public MessageTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(MessageTest.class);
    }
    
    public void testMessage() {
    	new Message("topic", "gtidSet", "operation", "id", "time");
    }
    
    public void testGetTopic() {
    	assertEquals("topic", new Message("topic", "gtidSet", "operation", "id", "time").getTopic());
    }
    
    public void testGetGtidSet() {
    	assertEquals("gtidSet", new Message("topic", "gtidSet", "operation", "id", "time").getGtidSet());
    }
    
    public void testGetOperation() {
    	assertEquals("operation", new Message("topic", "gtidSet", "operation", "id", "time").getOperation());
    }
    
    public void testGetId() {
    	assertEquals("id", new Message("topic", "gtidSet", "operation", "id", "time").getId());
    }
    
    public void testGetTime() {
    	assertEquals("time", new Message("topic", "gtidSet", "operation", "id", "time").getTime());
    }
}
    