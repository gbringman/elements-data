package net.gregorybringman.elementsreduce;

import java.io.IOException;

import net.gregorybringman.elementsreduce.util.ElementsUtils;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Takes part-of-speech models of Jean Mayer's data on page correspondences
 * between versions of Diderot's Éléments de Physiologie (from a previous map /
 * reduce phase) and adds markers representing them into the text of the
 * Éléments de Physiologie.
 * 
 * @author Gregory Bringman
 */
public class ElementsVersionsMarkupReducer extends
        Reducer<IntWritable, MapWritable, IntWritable, Text> {

    /**
     * For each page of DPV mark up each line range from each version, V and L,
     * with the custom POS of the model in order to re-present Mayer's data on
     * page correspondences into the text of the Elements itself.
     */
    public void reduce(IntWritable pageNo, MapWritable values, Context context)
            throws IOException, InterruptedException {

        context.write(
                pageNo,
                new Text(ElementsUtils.markupPages(values,
                        ElementsUtils.fetchTextOfPage())));
    }
}