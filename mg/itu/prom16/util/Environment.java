package mg.itu.prom16.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Environment {
    private Properties properties;
    private static Environment instance;

    private static Environment getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Environment not initialized. Call loadEnvironmentClass first.");
        }
        return instance;
    }

    public static Environment loadEnvironmentClass(String propertiesFileName) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = Environment.class.getClassLoader().getResourceAsStream(propertiesFileName)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        }
        instance = new Environment(properties);
        return instance;
    }
    
    public Environment(Properties properties) {
        if (properties == null) {
            this.properties = new Properties();
        } else {
            this.properties = properties;
        }
    }

    public static String getProperty(String key) {
        return getInstance().properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return getInstance().properties.getProperty(key, defaultValue);
    }
}
