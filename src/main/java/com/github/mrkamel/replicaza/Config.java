
package com.github.mrkamel.replicaza;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private Properties properties = null;
	
	public Config(String path) throws FileNotFoundException, IOException {
		this.properties = new Properties();
		
		properties.load(new FileInputStream(path));
	}
	
	public Config() {
		this.properties = new Properties();
	}
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}
}
