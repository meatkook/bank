package org.clever_bank.utility_classes;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private String url;
    private String fullUrl;
    private String username;
    private String password;
    private String dbName;

    public AppConfig() {
        try {
            Properties properties = new Properties();
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/application.yml");
            properties.load(fileInputStream);

            this.url = properties.getProperty("url");
            this.fullUrl = properties.getProperty("full_url");
            this.dbName = properties.getProperty("dbName");
            this.username = properties.getProperty("username");
            this.password = properties.getProperty("password");

            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }
    public String getFullUrl() {
        return fullUrl;
    }

    public String getDbName(){
        return  dbName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
