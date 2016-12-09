package org.pretent.mrpc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourcesFactory {

	private static Properties properties = new Properties();

	static {
		InputStream input = ResourcesFactory.class.getResourceAsStream("/mrpc.properties");
		try {
			properties.load(input);
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getString(String key) {
		return properties.getProperty(key);
	}

	public static Integer getInt(String key) {
		return properties.getProperty(key) == null ? null : Integer.parseInt(properties.getProperty(key));
	}

	public static void main(String[] args) {
		System.out.println(ResourcesFactory.getString("mrpc.register"));
	}
}
