package com.github.mrkamel.replicaza;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TableTest extends TestCase {
    public TableTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TableTest.class);
    }

    public void testTable() {
    	new Table("database", "table");
    }
    
    public void testGetDatabase() {
    	assertEquals("database", new Table("database", "table").getDatabase());
    }
    
    public void testGetTable() {
    	assertEquals("table", new Table("database", "table").getTable());
    }
}
