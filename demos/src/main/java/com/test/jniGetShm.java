package com.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class jniGetShm{
    public static native byte[] getShm(String dataName, int startNum, int dataSize);

    static{
        String variableName = "STORM_HOME";
        String stormHome = System.getenv(variableName);
    	System.load(stormHome + "\\demos\\src\\main\\resources\\getShm.dll");
    }

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
    public static byte[] getCompressedImage(byte[] rawData, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] imgData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(rawData, 0, imgData, 0, rawData.length);
        // printPixelValues(image);
        // try{
        //     BufferedImage img = ImageIO.read(bais);
        //     printPixelValues(img);
        // } catch(IOException e){
        //     e.printStackTrace();
        // }

        // byte[] imgData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        // System.arraycopy(rawData, 0, imgData, 0, rawData.length);

        // displayImage(image);
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

//     public static void main(String[] args) {
//         String dataName = "__IPC_SHM_LIVE_RGB__";
//         // int startNum = Integer.parseInt(args[1]);
//         // int dataSize = Integer.parseInt(args[2]);
//         jniGetShm test = new jniGetShm();
//         byte[] data = getShm(dataName, 0, 256);
//         Map<String, String> map = test.byteArrayToMap(data);
//         int heardSize =  Integer.parseInt(map.get("header size"));
//         int imagebyte =  Integer.parseInt(map.get("image bytes"));
//         int width =  Integer.parseInt(map.get("width"));
//         int height =  Integer.parseInt(map.get("height"));
//         jniGetShm img = new jniGetShm();
//         byte[] imageData = getShm(dataName, 0, 256+heardSize+imagebyte);
//         byte[] slicedArray = new byte[imagebyte];
//         System.arraycopy(imageData, 256+heardSize, slicedArray, 0, imagebyte);
//         System.out.println(map);
//         System.out.println(slicedArray);
//         byte[]a = printImage(slicedArray, width, height);
//         // byte[] byteimgfile = printImage(slicedArray, width, height);
//         try{
//             memoryToJPG.saveByteArrayAsJPEG(a, "C:\\Users\\ran22\\Desktop\\project\\storm\\demo\\test.jpg");
//         }catch (IOException e){
//         e.printStackTrace();
//         }
//     }
// }