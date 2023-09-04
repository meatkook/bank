package org.clever_bank.utility_classes;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;

public class ConfigReader {
    private static final String CONFIG_FILE = "application.yml";

    public static String getConfigValues() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            return yaml.load(inputStream).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}