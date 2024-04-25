package com.stormproject;

import java.util.HashMap;
import java.util.Map;

// 토폴로지 배포시 입력받은 키워드를 Ozray Shm 통신 모듈에 적용하기 위한 변환 자료
public class ImgTypeList {
    Map<String, String> img_type_list = new HashMap<String, String>();
    public ImgTypeList(){
        img_type_list.put("live_rgb", "-");
        img_type_list.put("save_rgb","-");
        img_type_list.put("live_hsi", "-");
        img_type_list.put("save_hsi", "-");
        img_type_list.put("live_hsi_rr","-");
        img_type_list.put("save_hsi_rr","-");
        img_type_list.put("live_swir", "-");
        img_type_list.put("save_swir", "-");
    }
}


