package com.stormproject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONObject;

public class SocketConnection {

    // 소켓 통신 시작 -> 서버로 데이터 송신(request) -> 서버로 부터 데이터 수신 -> 수신받은 데이터를 HashMap에 저장 -> 이후 ImageBolt에서 HashMap에서 데이터 꺼내서 사용
    public void run(String serverHost, int serverPort, int img_idx, String base64EncodedData, String img_type) throws InterruptedException{
        try {
            // 서버와 연결
            Socket socket = new Socket(serverHost, serverPort);

            // 서버로 송신할 데이터 준비
            JSONObject requestData = new JSONObject();
            requestData.put("data", base64EncodedData); // Replace with actual image data
            requestData.put("requestId", img_idx);
            requestData.put("img_type", img_type);
            String requestJson = requestData.toString();
            String willSend = requestJson + "\n";

            // 서버에 데이터 송신
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(willSend.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            // 서버로부터 JSON 데이터 수신받을 때까지 대기
            byte[] jsonData = new byte[2048];
            int dataBytesRead = socket.getInputStream().read(jsonData);
            String jsonDataStr = new String(jsonData, 0, dataBytesRead);
            

            // Wait for response data
            while(jsonDataStr == null){
                Thread.sleep(10);
                dataBytesRead = socket.getInputStream().read(jsonData);
                jsonDataStr = new String(jsonData, 0, dataBytesRead);
            }
            
            // jsonData 바이트 배열 초기화
            Arrays.fill(jsonData, (byte)0);

            // JSON 데이터 디코딩
            JSONObject jsonResponse = new JSONObject(jsonDataStr);
            int responseId = jsonResponse.getInt("responseId");
            int responseData = Integer.valueOf(jsonResponse.getString("responseData"));
            int predictTime = jsonResponse.getInt("predictTime");

            // HashMap에 {Key:img_idx, Value:[]} 쌍으로 저장
            ArrayList<Integer> list = new ArrayList<Integer>();
            list.add(responseData); // index in arraylist : 0
            list.add(predictTime); // index in arraylist : 1
            HashMapObj.getSharedMap().put(responseId, list);

            // 소켓 종료
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
