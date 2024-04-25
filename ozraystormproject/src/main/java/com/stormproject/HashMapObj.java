package com.stormproject;

import java.util.HashMap;
import java.util.ArrayList;

// HashMap 자료구조
// {Key: img_idx, Value:[responseData, predictTime] } 으로 저장
public class HashMapObj {
    private static HashMap<Integer, ArrayList<Integer>> sharedMap = new HashMap<>();

    public static HashMap<Integer, ArrayList<Integer>> getSharedMap() {
        return sharedMap;
    }
}
