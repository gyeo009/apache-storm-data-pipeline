package com.test;

import java.io.FileInputStream;
import java.util.Properties;

public class getProperties {
    static String variableName = "STORM_HOME";
    static String stormHome = System.getenv(variableName);
    static String resource = stormHome + "\\demos\\src\\main\\resources\\settings.properties";

    public static String getDLserverPortFromProperties(){
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(resource);
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String DLserverPort = properties.getProperty("DLserverPort");
        return DLserverPort;
    }
    public static String getDLserverIPFromProperties(){
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(resource);
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String DLserverIP = properties.getProperty("DLserverIP");
        return DLserverIP;
    }
    public static String getMongoDBURLFromProperties(){
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(resource);
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String MongoDBURL = properties.getProperty("MongoDBURL");
        return MongoDBURL;
    }
    public static String getMinIOURLFromProperties(){
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(resource);
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String MinIOURL = properties.getProperty("MinIOURL");
        return MinIOURL;
    }
    public static String getMinIOIDFromProperties(){
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(resource);
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String MinIOID = properties.getProperty("MinIOID");
        return MinIOID;
    }
    public static String getMinIOPWFromProperties(){
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(resource);
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String MinIOPW = properties.getProperty("MinIOPW");
        return MinIOPW;
    }
    public static String getMongoDBCollectionName(){
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream(resource);
            properties.load(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String MongoDBCollectionName = properties.getProperty("MongoDBCollectionName");
        return MongoDBCollectionName;
    }
}
