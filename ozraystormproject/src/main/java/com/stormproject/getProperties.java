package com.stormproject;

import java.io.FileInputStream;
import java.util.Properties;

// Properties에서 설정 정보들을 가져오는 클래스
public class getProperties {

    // 분산처리 될 각 PC의 properties 파일에서 설정정보를 가져오기 위해
    // 미리 환경변수로 등록한 "STORM_HOME" 경로를 불러온 후, 이 경로에 properties 파일이 위치하는 경로 붙이기
    static String variableName = "STORM_HOME";
    static String stormHome = System.getenv(variableName);
    static String resource = stormHome + "\\ozraystormproject\\src\\main\\resources\\settings.properties";

    // 딥러닝 서버의 Port번호를 가져오는 메소드
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

    // 딥러닝 서버의 IP 주소를 가져오는 메소드
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

    // MongoDB의 URL을 가져오는 메소드
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

    // MinIO Storage의 URL을 가져오는 메소드
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

    // MinIO Storage의 ID를 가져오는 메소드
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

    // MinIO Storage의 Password를 가져오는 메소드
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

    // MongoDB Collection Name을 가져오는 메소드
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
