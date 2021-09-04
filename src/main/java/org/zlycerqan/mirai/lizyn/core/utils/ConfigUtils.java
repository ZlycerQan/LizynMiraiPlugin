package org.zlycerqan.mirai.lizyn.core.utils;


import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigUtils {

    public static Map<?, ?> getConfigFromFile(String path) throws FileNotFoundException, YamlException {
        YamlReader yamlReader = new YamlReader(new FileReader(path));
        Map<?, ?> config = (Map<?, ?>) yamlReader.read();
        return config == null ? new ConcurrentHashMap<>() : config;
    }

    public static Map<?, ?> getConfigFromAll(Map<?, ?> all, String service) {
        Map<?, ?> config = (Map<?, ?>) all.get(service);
        return config == null ? new ConcurrentHashMap<>() : config;
    }
}
