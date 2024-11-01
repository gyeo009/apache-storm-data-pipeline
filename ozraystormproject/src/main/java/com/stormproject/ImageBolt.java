package com.stormproject;

import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
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
        // imageBolt 실행시간 계산을 위한 TimeStamp - Bolt 시작 시간 기록
        long imageBoltStartTime = System.currentTimeMillis();

        // Spout으로부터 전해받은 tuple에서 각종 키워드 추출
        int img_idx = tuple.getIntegerByField("img_idx");
        String filename = tuple.getStringByField("filename");
        String img_type = tuple.getStringByField("img_type");
        byte[] image = tuple.getBinaryByField("image");
        int classification;
        String timestamp = tuple.getStringByField("timestamp");
        String enctime = tuple.getStringByField("enctime");
        String sequence = tuple.getStringByField("sequence");
        int header_size = tuple.getIntegerByField("header_size");
        int img_bytes_size = tuple.getIntegerByField("img_bytes_size");
        String bucketName = "default";                              
        long imageSpoutExecutionTime = tuple.getLongByField("imageSpoutExecutionTime");
        long processingStartTime = tuple.getLongByField("processingStartTime");
        int predictTime;

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
        ArrayList<Integer> list = HashMapObj.getSharedMap().get(img_idx);
        classification = list.get(0);
        predictTime = list.get(1);
        HashMapObj.getSharedMap().remove(img_idx);

        // img_type에 맞게 MinIO bucketName 설정
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

        // MinIO에 저장되는 path, MongoDB에 각 파일의 실제저장 위치로 들어감
        String save_path = bucketName + "/" + "cfval_" + classification + "/" + filename + ".jpg";

        // 이미지 1장 당 ImageSpout -> ImageBolt + 딥러닝 처리에 걸린 시간
        int DLprocessingTime = predictTime;
        
        // imageBolt 실행시간 계산을 위한 TimeStamp - Bolt 종료 시간 기록
        long imageBoltEndTime = System.currentTimeMillis();

        // imageBolt 실행시간 계산을 위한 TimeStamp - Bolt 실행 시간 계산
        long imageBoltExecutionTime = imageBoltEndTime - imageBoltStartTime;

        // Emit to next component
        this.collector.emit(new Values(img_idx, filename, img_type, image, classification, timestamp, SVinfo, SPOInfo, BoltInfo, enctime, sequence, header_size, img_bytes_size, save_path, DLprocessingTime, imageSpoutExecutionTime, imageBoltExecutionTime, processingStartTime));
    }

    // 전송할 tuple의 schema 설정
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("img_idx", "filename", "img_type", "image", "classification", "timestamp", "SVInfo", "SPOInfo", "BoltInfo", "enctime", "sequence", "header_size", "img_bytes_size", "save_path", "DLprocessingTime", "imageSpoutExecutionTime", "imageBoltExecutionTime", "processingStartTime"));
    }
}