package edu.cornell.ld4l.entrypoint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SetupPropertyValues {

	InputStream inputStream = null;
	Map<String, String> map = new HashMap<String, String>();

	public Map<String, String> getPropValues(String propFilePath) throws IOException {
		Properties prop = new Properties();
		inputStream = new FileInputStream(propFilePath);
		if (inputStream != null) {
			prop.load(inputStream);
		} else {
			throw new FileNotFoundException("property file '" + propFilePath + "' not found in the classpath");
		}
		String inputFolder = prop.getProperty("INPUT_FILE_FOLDER");
		map.put("INPUT_FILE_FOLDER", inputFolder);
		String outputFolder = prop.getProperty("OUTPUT_FILE_FOLDER");
		map.put("OUTPUT_FILE_FOLDER", outputFolder);
		inputStream.close();

		return map;
	}
}

