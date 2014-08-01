package net.gregorybringman.elementsreduce;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import net.gregorybringman.elementsreduce.types.ElementsMapWritable;
import net.gregorybringman.elementsreduce.types.ElementsStringArrayWritable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Runs the ElementsVersions map-reduce with an input data set of the format used in
 * &quot;resources/elements-data-table-1-Mayer.txt&quot;
 * 
 * @author Gregory Bringman
 * 
 */
public class ElementsMapJob {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        if (args.length != 2) {
            System.err.println("Usage: elementsmap <in> <out>");
            System.exit(2);
        }

        deleteOutput(conf);
        
        Job job = new Job(conf, "Elements Map");
        job.setNumReduceTasks(1);
        job.setJarByClass(ElementsMapJob.class);

        job.setMapperClass(ElementsVersionsMapper.class);
        job.setCombinerClass(ElementsVersionsReducer.class);
        job.setReducerClass(ElementsVersionsReducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(ElementsStringArrayWritable.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(ElementsMapWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    private static void deleteOutput(Configuration conf) {
        Path outputPath = new Path("target/output");
        try {
            FileSystem  fs = FileSystem.get(new URI(outputPath.toString()), conf);
            if (fs.exists(outputPath)) {
                fs.delete(outputPath, true);
            }
            fs.close();
        }
        
        //TODO: Throw Elements-data exceptions...
        
        catch (IOException ioe) {
            throw new RuntimeException("File could not be deleted.", ioe);
        }
        catch (URISyntaxException use) {
            throw new RuntimeException("Invalid path / URI.", use);
        }
    }
}