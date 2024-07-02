package com.automation.code.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetInputDataFromYAMLFile1 {

    public static Map<String, String> getInputData(String filePath) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Map<String, String> keyValueMap=null;
        try {
            File yamlFile = new File(filePath);
            Map<String, Object> yamlMap = mapper.readValue(yamlFile, Map.class);
            keyValueMap = new HashMap<>();
            extractAllKeyValuePairs(yamlMap, "", keyValueMap);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyValueMap;
    }

    private static void extractAllKeyValuePairs(Map<String, Object> yamlMap, String currentKeyPrefix, Map<String, String> keyValueMap) {
        for (Map.Entry<String, Object> entry : yamlMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                String newPrefix = currentKeyPrefix.isEmpty() ? key : currentKeyPrefix + "." + key;
                extractAllKeyValuePairs((Map<String, Object>) value, newPrefix, keyValueMap);
            } else {
                String fullKey = currentKeyPrefix.isEmpty() ? key : currentKeyPrefix + "." + key;
                String strValue = value != null ? value.toString() : "";
                keyValueMap.put(fullKey, strValue);
            }
        }
    }
}
