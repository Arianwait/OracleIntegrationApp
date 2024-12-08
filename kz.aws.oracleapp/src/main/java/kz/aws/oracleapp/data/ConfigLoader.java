package kz.aws.oracleapp.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    private static final String CONFIG_FILE = "data/config.properties";

    public static Properties loadConfig() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load configuration file: " + CONFIG_FILE);
        }
        return properties;
    }
}