package net.gregorybringman.elementsreduce;

import java.io.IOException;

import net.gregorybringman.elementsreduce.types.ElementsStringArrayWritable;
import net.gregorybringman.elementsreduce.util.ElementsUtils;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Inputs data on page correspondences between different versions of Diderot's
 * <em>Éléments de Physiologie</em> from Jean Mayer's &quot;La composition
 * fragmentaire des Éléments de Physiologie&quot;,
 * <em>Studies on Voltaire and the 18th Century</em>, (1988). This class is 
 * responsible for the first part of the cycle, mapping the page entry of the 
 * Herbert Dieckmann version to a collection containing the Vandeul (V) and 
 * Leningrad (L) editions.
 * 
 * @author Gregory Bringman
 */
public class ElementsVersionsMapper extends
		Mapper<LongWritable, Text, IntWritable, ArrayWritable> {

	/**
	 * <p>
	 * Split a line (one record) into text strings representing three versions
	 * of Diderot's <em>Éléments de Physiologie</em>.
	 * </p>
	 * 
	 * <p>
	 * Extract the page number from the first version and map it as a key to a
	 * list containing the text for version 2 and 3.
	 * </p>
	 * <ul>
	 * <li>Version 1 is Dieckmann-Proust-Varloot (DPV)</li>
	 * <li>Version 2 is Vandeul (V)</li>
	 * <li>Version 3 is Leningrad (L)</li>
	 * </ul>
	 * <p>
	 * DPV is a critical edition dating from 1987, so previous versions are
	 * rolled up in reference to this contemporary version.
	 * </p>
	 */
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		String v = value.toString();

		if (v.indexOf("#") > -1) {
			return;
		}

		String[] split = v.split(":");

		if (split.length != 3) {
			System.out.println("At: " + value);
			return;
		}

		ElementsStringArrayWritable versions = new ElementsStringArrayWritable(new String[]{split[1], split[2]});

		context.write(ElementsUtils.fetchPage(split[0]), versions);
	}
}