package com.github.mrkamel.replicaza;

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
 
    public void testTakeLeadership() {
    	// Can't be tested
    }
}
