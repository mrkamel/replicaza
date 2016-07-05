
package com.github.mrkamel.replicaza;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private static final Properties properties = new Properties();
	
	public static void read(String path) throws FileNotFoundException, IOException {
		properties.load(new FileInputStream(path));
	}
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}
}
