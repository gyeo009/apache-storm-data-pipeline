package com.stormproject;

import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Date;

public class ImageSpout extends BaseRichSpout {
    private SpoutOutputCollector collector;

    private String img_type_args;
    private int img_idx = 0;
    private String img_type;
    private int max_fps;
    
    
    // spout 컴포넌트 초기상태 설정
    @Override
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        
        // storm configuration에서 img_type 받아오기
        ImgTypeList imgtypelist = new ImgTypeList();
        img_type_args = String.valueOf(conf.get("img_type"));

        // 공유메모리 접근을 위한 이미지 타입 키워드 받아오기
        img_type = imgtypelist.img_type_list.get(img_type_args);

        // storm configuration에서 max_fps 받아오기
        String str_max_fps = String.valueOf(conf.get("max_fps"));
        max_fps = Integer.parseInt(str_max_fps);
    }

    // spout 컴포넌트 동작
    @Override
    public void nextTuple() {
        // imageSpout 실행시간 계산을 위한 TimeStamp - spout 시작 시간 기록
        long imageSpoutStartTime = System.currentTimeMillis();

        // 이미지 1장당 처리에 걸린시간을 확인하기 위해 milli초 단위 timestamp 생성
        long processingStartTime = imageSpoutStartTime;

        // img_idx 인덱스 1개씩 증가
        img_idx++; 

        String filename = img_type_args + String.valueOf(img_idx); // 파일 이름
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss"); // 현재 시간
        String timestamp = sdf.format(new Date()); // 현재 시간
        
        

        // 공유메모리에 접근, 공유 메모리에서 받아온 헤더 데이터를 map에 저장
        jniGetShm shmemory = new jniGetShm();
        byte[] data = shmemory.getShm(img_type, 0, 256);
        Map<String, String> map = shmemory.byteArrayToMap(data);

        // map에서 각종 메타데이터 extract
        String enctime = map.get("enctime"); // enctime
        String sequence = map.get("sequence"); // sequence
        int width =  Integer.parseInt(map.get("width")); // 이미지 너비
        int height =  Integer.parseInt(map.get("height")); // 이미지 높이
        int header_size =  Integer.parseInt(map.get("header size")); // 헤더 사이즈
        int img_bytes_size =  Integer.parseInt(map.get("image bytes")); // 이미지 총 크기
        
        // 받아올 이미지 크기에 맞게 이미지 수신
        byte[] fullData = shmemory.getShm(img_type, 0, 256+header_size+img_bytes_size);
        byte[] slicedArray = new byte[img_bytes_size]; // ImageBytes만 담을 바이트어레이
        System.arraycopy(fullData, 256+header_size, slicedArray, 0, img_bytes_size); // Image Byte data를 slicedArray에 담음
        byte[] image = shmemory.getCompressedImage(slicedArray, width, height); // Compressing Image Byte data

        // imageSpout 실행시간 계산을 위한 TimeStamp - spout 종료 시간 기록
        long imageSpoutEndTime = System.currentTimeMillis();

        // imageSpout 실행시간 계산을 위한 TimeStamp - spout 실행 시간 계산
        long imageSpoutExecutionTime = imageSpoutEndTime - imageSpoutStartTime;

        // 연결된 컴포넌트에 튜플 전송
        this.collector.emit(new Values(img_idx, filename, img_type, image, timestamp, enctime, sequence, header_size, img_bytes_size, processingStartTime, imageSpoutExecutionTime));
        
        // 다음 spout 동작전 latency
        Utils.sleep((int)(1000/max_fps));
    }

    // 전송할 tuple의 schema 설정. 이를 Field라고 함
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("img_idx", "filename", "img_type", "image", "timestamp", "enctime", "sequence", "header_size", "img_bytes_size", "processingStartTime", "imageSpoutExecutionTime"));
    }
}