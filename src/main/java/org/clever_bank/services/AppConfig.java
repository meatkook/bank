package org.clever_bank.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class represents the configuration for the application.
 * It contains properties such as the URL, full URL, username, password, and database name.
 */
public class AppConfig {
    /**
     * The URL of the application.
     */
    private String url;

    /**
     * The full URL of the application.
     */
    private String fullUrl;

    /**
     * The username for accessing the application.
     */
    private String username;

    /**
     * The password for accessing the application.
     */
    private String password;

    /**
     * The name of the database.
     */
    private String dbName;

    /**
     * Constructs a new AppConfig object and initializes its properties from the application.yml file.
     */
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

    /**
     * @return the URL of the database
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the full URL of the database
     */
    public String getFullUrl() {
        return fullUrl;
    }

    /**
     * @return the name of the database
     */
    public String getDbName(){
        return  dbName;
    }

    /**
     * @return the username for accessing the database
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password for accessing the database
     */
    public String getPassword() {
        return password;
    }
}
