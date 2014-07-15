package net.gregorybringman.elementsreduce.services;

import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;

public interface TextService {

    public String textFromPageAndModel(IntWritable pageNo, MapWritable pageModel);

    public String textFromPageModel(MapWritable pageModel);

    public String textFromMayerPage(IntWritable pageNo);

    /**
     * Fetch a page from a document source exposed to the service implementation
     * and apply a page model. Search source can be a partitioned document or
     * corpus on HDFS or not.
     * 
     * Page models usually come from the {@link ElementsVersionsReducer}.
     * 
     * @param page
     *            The page of the XML content to which to add range tags.
     * @param pageModel
     *            The POS model that has line range information.
     * @return A collection different retrieved texts with marked line ranges.
     */
    public Map<String, String> versionsFromPageAndModel(String page,
            MapWritable pageModel);
}