package net.gregorybringman.elementsreduce;

import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * 
 * @author Gregory Bringman
 *
 */
public class ElementsMapJob {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: elementsmap <in> <out>");
			System.exit(2);
		}

		setSerializations(conf);
		
		Job job = new Job(conf, "Elements Map");
		job.setNumReduceTasks(0);
		job.setJarByClass(ElementsMapJob.class);

		job.setMapperClass(ElementsVersionsMapper.class);
		job.setCombinerClass(ElementsVersionsReducer.class);
		job.setReducerClass(ElementsVersionsReducer.class);

		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(List.class);

		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Map.class);
		
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
	private static void setSerializations(Configuration conf) {
		conf.set("io.serializations", 
		"org.apache.hadoop.io.serializer.WritableSerialization, org.apache.hadoop.io.serializer.JavaSerialization");		
	}
}