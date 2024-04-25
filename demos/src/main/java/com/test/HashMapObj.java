package com.test;

import java.util.HashMap;
import java.util.Map;

public class HashMapObj {
    private static Map<Integer, String> sharedMap = new HashMap<>();

    public static Map<Integer, String> getSharedMap() {
        return sharedMap;
    }
}
