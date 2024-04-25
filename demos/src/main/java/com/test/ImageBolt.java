package com.test;

import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Base64;
import java.util.Map;

public class ImageBolt extends BaseRichBolt {
    private OutputCollector collector;
    String SPOInfo;
    String BoltInfo;

    // bolt 초기 실행 동작
    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.SPOInfo = "ImageSpout";
        this.BoltInfo = this.getClass().getSimpleName();
    }

    // bolt 동작
    @Override
    public void execute(Tuple tuple) {
        // Spout으로부터 전해받은 tuple에서 각종 키워드 추출
        int img_idx = tuple.getIntegerByField("img_idx");
        String filename = tuple.getStringByField("filename");
        String img_type = tuple.getStringByField("img_type");
        byte[] image = tuple.getBinaryByField("image");
        String classification;
        String timestamp = tuple.getStringByField("timestamp");
        String enctime = tuple.getStringByField("enctime");
        String sequence = tuple.getStringByField("sequence");
        int header_size = tuple.getIntegerByField("header_size");
        int img_bytes_size = tuple.getIntegerByField("img_bytes_size");
        String bucketName = "default";                              
        
        // Supervisor info(SV info)
        String SVinfo = "OS: " + System.getProperty("os.name")+", "+System.getProperty("os.arch")+", Java Version: "+System.getProperty("java.version") +", User Name: "+ System.getProperty("user.name");

        // 딥러닝 서버와 소켓통신
        String encodedData = Base64.getEncoder().encodeToString(image); // 이미지 인코딩
        SocketConnection s = new SocketConnection(); // 소켓 연결을 위한 인스턴스 생성
        
        try {
            // Properties 파일에서 딥러닝 서버 URL 가져오기
            String port = getProperties.getDLserverPortFromProperties();
            String IP = getProperties.getDLserverIPFromProperties();

            // 딥러닝 서버와 연결
            s.run(IP, Integer.valueOf(port), img_idx, encodedData, img_type);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // HashMap에 저장된 classification 값 추출
        classification = HashMapObj.getSharedMap().get(img_idx);
        HashMapObj.getSharedMap().remove(img_idx);

        // img_type에 맞게 MinIO bucketName 설정
        switch (img_type) {
            case "-":
                bucketName = "live-rgb";
                break;
            case "-":
                bucketName = "save-rgb";
                break;
            case "-":
                bucketName = "live-hsi";
                break;
            case "-":
                bucketName = "save-hsi";
                break;
            case "-":
                bucketName = "live-hsi-rr";
                break;
            case "-":
                bucketName = "save-hsi-rr";
                break;
            case "-":
                bucketName = "live-swir";
                break;
            case "-":
                bucketName = "save-swir";
                break;
          }

        // MinIO에 저장되는 path, MongoDB에 각 파일의 실제저장 위치로 들어감
        String save_path = bucketName + "/" + "cfval_" + classification + "/" + filename + ".jpg"; 
        
        // Emit to next component
        this.collector.emit(new Values(img_idx, filename, img_type, image, classification, timestamp, SVinfo, SPOInfo, BoltInfo, enctime, sequence, header_size, img_bytes_size, save_path)); 
    }

    // 전송할 tuple의 schema 설정
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("img_idx", "filename", "img_type", "image", "classification", "timestamp", "SVInfo", "SPOInfo", "BoltInfo", "enctime", "sequence", "header_size", "img_bytes_size", "save_path"));
    }
}