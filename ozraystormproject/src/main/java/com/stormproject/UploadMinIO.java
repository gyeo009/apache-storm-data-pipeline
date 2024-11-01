package com.stormproject;


import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;

import java.io.ByteArrayInputStream;

import java.io.IOException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;




public class UploadMinIO {
  public static void Upload(byte[] image, int classificationVal, String img_type, String filename)
      throws IOException, NoSuchAlgorithmException, InvalidKeyException {    
    
    try {
      // Minio Storage Access
      String MinioURL = getProperties.getMinIOURLFromProperties();
      String MinioID = getProperties.getMinIOIDFromProperties();
      String MinioPW = getProperties.getMinIOPWFromProperties();
      MinioClient minioClient =
          MinioClient.builder()
              .endpoint(MinioURL)
              .credentials(MinioID, MinioPW)
              .build();

      String folderPath = "cfval_" + classificationVal + "/"; // 분류 폴더
      // bucket exist check & create
      String bucketName = "a";


      switch (img_type) {
        case "__IPC_SHM_LIVE_RGB__":
            bucketName = "live-rgb";
            break;
        case "__IPC_SHM_SAVE_RGB__":
            bucketName = "save-rgb";
            break;
        case "__IPC_SHM_LIVE_HSI__":
            bucketName = "live-hsi";
            break;
        case "__IPC_SHM_SAVE_HSI__":
            bucketName = "save-hsi";
            break;
        case "__IPC_SHM_LIVE_HSI_RR_DATA__":
            bucketName = "live-hsi-rr";
            break;
        case "__IPC_SHM_SAVE_HSI_RR_DATA__":
            bucketName = "save-hsi-rr";
            break;
        case "__IPC_SHM_LIVE_SWIR__":
            bucketName = "live-swir";
            break;
        case "__IPC_SHM_SAVE_SWIR__":
            bucketName = "save-swir";
            break;
      }
      
      // bucketName = bucketName + classificationVal;

      boolean found =
          minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
      if (!found) { 
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      }

      // ByteArray data를 Input하기 위한 Stream 생성
      ByteArrayInputStream bis = new ByteArrayInputStream(image);

      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(bucketName)
              .object(folderPath.concat(filename+".jpg"))
              .stream(bis, -1, 10485760)
              .build());
      bis.close(); // stream close
    } catch (MinioException | IOException e) {
      System.out.println("Error occurred: " + e);
      e.printStackTrace();
    }
  }
}



