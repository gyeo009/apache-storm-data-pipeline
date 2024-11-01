package com.stormproject;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.mongodb.bolt.MongoInsertBolt;
import org.apache.storm.mongodb.common.mapper.MongoMapper;
import org.apache.storm.mongodb.common.mapper.SimpleMongoMapper;

public class ImageTopology {
    private static final String TEST_MONGO_URL = getProperties.getMongoDBURLFromProperties();
    private static final String TEST_MONGO_COLLECTION_NAME = getProperties.getMongoDBCollectionName();

    public static void main(String[] args) throws Exception {
        String url = TEST_MONGO_URL;
        String collectionName = TEST_MONGO_COLLECTION_NAME;

        // arguments로 topology Name 및 number of worker 받기
        if (args.length != 6) {
            System.out.println("Usage: python storm.py jar <jar_file_path> <main_class_path> <topology_name> <image_type> <max_fps> <img_bolt_workers> <mongo_bolt_workers> <minio_bolt_workers>");
            return;
        }

        String topoName = args[0];
        int img_bolt_workers = Integer.valueOf(args[3]);
        int mongo_bolt_workers = Integer.valueOf(args[4]);
        int minio_bolt_workers = Integer.valueOf(args[5]);

        // Mapping with MongoDB
        MongoMapper mapper = new SimpleMongoMapper()
        .withFields("img_idx", "img_type", "filename", "classification",
        "timestamp", "SVInfo", "SPOInfo", "BoltInfo",
        "enctime", "sequence", "header_size", "img_bytes_size", "save_path", "DLprocessingTime", "imageSpoutExecutionTime", "imageBoltExecutionTime");

        MongoInsertBolt insertBolt = new MongoInsertBolt(url, collectionName, mapper);


        // 토폴로지 설정 - 토폴로지와 spout, bolt, mongo, minio 연결
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("image-spout", new ImageSpout(), 1);
        builder.setBolt("image-bolt", new ImageBolt(), img_bolt_workers)
               .fieldsGrouping("image-spout", new Fields("img_idx"));
        builder.setBolt("mongo-bolt", insertBolt, mongo_bolt_workers)
                .fieldsGrouping("image-bolt", new Fields("img_idx"));
        builder.setBolt("minio-bolt", new MinIOBolt(), minio_bolt_workers)
                .fieldsGrouping("image-bolt", new Fields("img_idx"));

                

        // configuration 파일 - main함수 argument로 입력받은 image_type 및 max_fps를 storm configuration에 입력
        Config conf = new Config();
        conf.setDebug(true);
        conf.setNumWorkers(img_bolt_workers);
        conf.put("img_type", args[1]); // args[3]
        conf.put("max_fps", args[2]);  // args[4]

        // Topology 제출
        StormSubmitter.submitTopology(topoName, conf, builder.createTopology());
    }
}