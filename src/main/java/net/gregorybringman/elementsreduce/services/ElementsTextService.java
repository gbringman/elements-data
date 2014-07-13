package net.gregorybringman.elementsreduce.services;

import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;

import com.google.common.collect.Maps;

public class ElementsTextService implements TextService {

    public String textFromPageAndModel(IntWritable pageNo, MapWritable pageModel) {
        return "";
    }
    public String textFromPageModel(MapWritable pageModel) {
        return "";
    }
    public String textFromMayerPage(IntWritable pageNo) {
        return "";
    }
    
    /*
     * Implement {@TextService#versionsFromPageAndModel}
     */
    public Map<String, String> versionsFromPageAndModel(String page, MapWritable pageModel) {
        Map<String, String> versions = Maps.newConcurrentMap();
        
        return versions;
    }
}