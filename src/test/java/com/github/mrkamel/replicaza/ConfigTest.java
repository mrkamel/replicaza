package com.github.mrkamel.replicaza;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConfigTest extends TestCase {
    public ConfigTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ConfigTest.class);
    }

    public void testRead() throws IOException {
    	Files.write(Paths.get("/tmp/config.properties"), Arrays.asList("property1=value1", "property2=value2"), Charset.forName("UTF-8"));

    	Config.read("/tmp/config.properties");
    	
    	assertEquals("value1", Config.getProperty("property1"));
    	assertEquals("value2", Config.getProperty("property2"));
    }
    
    public void testGetProperty() {
    	// Already tested
    }
    
    public void testSetProperty() {
    	Config.setProperty("key", "value");
    	
    	assertEquals("value", Config.getProperty("key"));
    }
}
