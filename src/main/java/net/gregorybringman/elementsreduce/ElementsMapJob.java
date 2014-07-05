package net.gregorybringman.elementsreduce;

import net.gregorybringman.elementsreduce.types.ElementsMapWritable;
import net.gregorybringman.elementsreduce.types.ElementsStringArrayWritable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Runs the ElementsVersions map-reduce with an input data set of the format used in
 * {@link elements-data-2012-06-23_pipe_delim.txt}.
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

        Job job = new Job(conf, "Elements Map");
        job.setNumReduceTasks(1);
        job.setJarByClass(ElementsMapJob.class);

        job.setMapperClass(ElementsVersionsMapper.class);
        job.setCombinerClass(ElementsVersionsMarkupReducer.class);
        job.setReducerClass(ElementsVersionsMarkupReducer.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(ElementsStringArrayWritable.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(ElementsMapWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}