package net.gregorybringman.elementsreduce;

import java.io.IOException;

import net.gregorybringman.elementsreduce.types.ElementsMapWritable;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class ElementsVersionsSearchReducer extends
        Reducer<IntWritable, MapWritable, Text, Text> {

    public static final String pageBreakTag = "<pb\\s*n=\"\\d+\"\\s*/>";
    public static final String pageBreakTagWithPage = "<pb\\s*n=\"%d\"\\s*/>";

    /**
     * Retrieve the text of page from a version of the <em>Éléments de 
     * Physiologie</em> corpus.
     * 
     * @param pageNo
     *            The page number to retrieve from {@code version}.
     * @param version
     *            The version with an identifying key [DPV|V|L], with corpus as
     *            value.
     * @param context
     *            The map reduce context in which to store the retrieved page.
     * @throws IOException
     *             If either key or value cannot be added to the context.
     * @throws InterruptedException
     *             If either key or value cannot be added to the context.
     */
    public void reduce(IntWritable pageNo, ElementsMapWritable version,
            Context context) throws IOException, InterruptedException {

        String corpus = version.values().iterator().next().toString();
        String searchString = String.format(pageBreakTagWithPage, pageNo.get());
        String pageAndOther[] = corpus.split(searchString);
        String fromPageToEnd[] = pageAndOther[1].split(pageBreakTag);

        Text key = new Text(version.keySet().iterator().next().toString());
        Text value = new Text(fromPageToEnd[0]);

        context.write(key, value);
    }
}