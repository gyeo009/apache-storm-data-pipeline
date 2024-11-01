package com.stormproject;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.ByteArrayInputStream;
import java.io.File;

public class memoryToJPG {
    public static byte[] jpegCompressedImageToByteArray(BufferedImage image, float quality) throws IOException {
        if (quality < 0 || quality > 1) {
            throw new IllegalArgumentException("Quality should be between 0 and 1");
        }

        // Get a JPEG writer
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();

        // Set the compression quality
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.setOutput(ImageIO.createImageOutputStream(baos));
        writer.write(null, new javax.imageio.IIOImage(image, null, null), param);

        return baos.toByteArray();
    }


    public static void saveByteArrayAsJPEG(byte[] imageData, String outputPath) throws IOException {
            // 바이트 어레이를 BufferedImage로 변환
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        BufferedImage image = ImageIO.read(bais);
    
        if (image == null) {
            throw new IllegalArgumentException("Provided byte array isn't a valid image data.");
        }
    
            // 이미지를 JPEG 형식의 파일로 저장
        File outputFile = new File(outputPath);
        ImageIO.write(image, "jpg", outputFile);
    }
}


