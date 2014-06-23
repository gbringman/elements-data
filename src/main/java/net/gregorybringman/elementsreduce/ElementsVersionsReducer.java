package net.gregorybringman.elementsreduce;

import java.io.IOException;

import net.gregorybringman.elementsreduce.util.ElementsUtils;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Construct the actual data structure that holds the page correspondences of
 * versions of Éléments de Physiologie.
 * 
 * @author Gregory Bringman
 */
public class ElementsVersionsReducer extends
		Reducer<IntWritable, ArrayWritable, IntWritable, MapWritable> {

	/**
	 * <p>
	 * Take the mapped versions of the <em>Éléments de Physiologie</em> and for
	 * the Vandeul edition and the Leningrad edition, identify the POS of the
	 * text representing each version. Map this part-of-speech as a key to an
	 * IntWritable list with a starting and ending element for the page range.
	 * </p>
	 * <p>
	 * Take the mapped POS and line range, "the model" and add it to the
	 * collector as argument 2, where the page number of DPV is argument 1.
	 * </p>
	 */
	public void reduce(IntWritable pageNo, ArrayWritable values, Context context)
			throws IOException, InterruptedException {

		MapWritable entry = new MapWritable();

		int count = 1;

		for (Writable t : values.get()) {
			String in = t.toString();
			String match = ElementsUtils.posMatch(in);
			if (match.indexOf(ElementsUtils.MATCH_FAILURE_CODE) < 0) {
				entry.put(new Text(match + "_" + count),
						ElementsUtils.fetchRange(in));
				count++;
			}
		}
		context.write(pageNo, entry);
	}
}