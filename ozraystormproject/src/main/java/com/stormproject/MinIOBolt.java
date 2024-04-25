package com.stormproject;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;

// MinIO Storage에 저장하기 위한 볼트
public class MinIOBolt implements IBasicBolt{

    // 볼트 초기 동작 설정
    @Override
    public void prepare(Map<String,Object> topoConf, TopologyContext context){

    }

    // 볼트 동작
    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        // 저장 데이터 준비
        byte[] image = tuple.getBinaryByField("image");
        int classificationVal = tuple.getIntegerByField("classification"); // DL 결과값
        String img_type = tuple.getStringByField(("img_type"));
        String filename = tuple.getStringByField(("filename"));


        try{
            UploadMinIO.Upload(image, classificationVal, img_type, filename);
        } catch(IOException e){
            System.out.println("Error occurred: " + e);
        } catch(NoSuchAlgorithmException e){
            System.out.println("Error occurred: " + e);
        } catch(InvalidKeyException e){
            System.out.println("Error occurred: " + e);
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
