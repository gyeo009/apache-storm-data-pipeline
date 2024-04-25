**[데이터 흐름]**
Data(RGB, HSI) -> 공유메모리 -> ImageSpout -> ImageBolt(<->PythonSocketServer)         --> MongoDB
                                                                                      ㄴ> MinIO

*ImageBolt에서 Python AI Model 서버와 소켓통신을 하여 예측값(혹은 AI모델을 돌린 뒤 나온 값)을 가져오게 됩니다.


**[ImageTopology에 사용된 java파일은 다음과 같습니다.]**

ImageSpout.java  # Image Spout
ImageBolt.java   # Image Bolt
SocketConnection.java # Python Server와 소켓통신하는 파일
MinIOBolt.java # MinIO Bolt
TestMinIOByteFile.java # MinIO insertion file







**[TestMinIOByteFile.java의 역할]**
해당 자바 파일에서는 이미지파일을 문자열 형태로 변환한 String 값과 더불어 파일 url을 인자로 전달받게 됩니다.
1. 문자열 형태의 이미지를 ByteArray로 복원
2. 정규표현식을 통해 파일 URL에서 파일명을 추출
3. 1에서의 ByteArray를 전송할 Stream을 생성한 후 MinIO에 연결
4. 2에서 추출한 파일명대로 MinIO에 삽입






*AmazonS3 Storage BucketNaming Rules*
https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucketnamingrules.html