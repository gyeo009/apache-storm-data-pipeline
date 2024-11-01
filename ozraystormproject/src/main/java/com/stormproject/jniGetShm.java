package com.stormproject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class jniGetShm{
    public static native byte[] getShm(String dataName, int startNum, int dataSize);

    // ImageSpout에서 OzHSILab프로그램에 접근해 이미지를 받아올 때 사용하는 메소드,
    // OzHSILab프로그램은 공유메모리를 통해 이미지를 공유하게 되는데, 이 공유메모리에 접근하는 부분이 C 및 C++ 파일로 구성되어 있어
    // Java에서 사용하기 위해선 Jni(java native interface)를 사용해야 한다.


    // 미리 환경변수로 등록한 "STORM_HOME" 경로를 불러온 후, 이 경로에 getShm.dll 파일이 위치하는 경로 붙이기
    // System.load(~\\getShm.dll)를 하면 getShm.dll 내부에 존재하는 함수들을 사용할 수 있게 된다.
    // getShm.dll 내부에 존재하는 함수 중 getShm() 함수를 사용하기 위해 13번 줄 코드를 삽입하였다.
    static{
        String variableName = "STORM_HOME";
        String stormHome = System.getenv(variableName);
    	System.load(stormHome + "\\ozraystormproject\\src\\main\\resources\\getShm.dll");
    }

    // 공유 메모리에서 받아온 헤더 데이터는 ByteArray 형식인데, 이를 map 형식으로 바꿔주는 메소드
    public Map<String, String> byteArrayToMap(byte[] data) {
        String decoded = new String(data);
        String[] pairs = decoded.split("\n");
        Map<String, String> result = new HashMap<>();
        
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                result.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return result;
    }

    // 이미지 크기를 압축하는 메소드
    public static byte[] getCompressedImage(byte[] rawData, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] imgData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(rawData, 0, imgData, 0, rawData.length);
        try{
            byte[] compressedData = memoryToJPG.jpegCompressedImageToByteArray(image, 0.8f);
            return compressedData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void showImage(byte[] rawData, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

        byte[] imgData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(rawData, 0, imgData, 0, rawData.length);
        printPixelValues(image);

        displayImage(image);
    }

    public static void printPixelValues(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = img.getRGB(x, y);
                
                // Getting RGB values
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                System.out.printf("Pixel at (%d, %d): R=%d, G=%d, B=%d\n", x, y, red, green, blue);
            }
        }
    }

    private static void displayImage(BufferedImage image) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel lbl = new JLabel();
        lbl.setIcon(new ImageIcon(image));
        frame.getContentPane().add(lbl, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}