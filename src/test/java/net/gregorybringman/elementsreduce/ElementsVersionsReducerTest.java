package net.gregorybringman.elementsreduce;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import net.gregorybringman.elementsreduce.util.ElementsUtils;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Gregory Bringman
 */
@RunWith(MockitoJUnitRunner.class)
public class ElementsVersionsReducerTest {

	@SuppressWarnings("unused")
	private final String data = "319, 1-4::345, 15-19\n5-7::30, 15-18\n8-10::338, 4-8\n11-19:21r:332, 9 à 333, 1\n20-23::303, 15-22";

	ElementsVersionsMapper mapper;
	ElementsVersionsReducer reducer;
	ArrayWritable keys;
	ArrayWritable  one;
	ArrayWritable  two;
	Writable[] versions, expected;
	MapWritable entry;
	String pageKey = "30";

	@Before
	public void setUp() {

		reducer = new ElementsVersionsReducer();
		keys = new ArrayWritable(new String[]{"319, 1-4", "319, 5-7"});
		one = new ArrayWritable(new String[]{"","345, 15-19"});
		two = new ArrayWritable(new String[]{"", pageKey + ",15-18"});
		versions = new Writable[]{one, two};
		expected = new Writable[]{one, two};
		entry = new MapWritable();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testTagging() throws IOException, InterruptedException {

		ElementsVersionsReducer.Context context = mock(ElementsVersionsReducer.Context.class);

		IntWritable pageNo = ElementsUtils.fetchPage(keys.toStrings()[0]);

		reducer.reduce(pageNo, one, context);

		int count = 1;
		for (Writable w : expected) {

			ArrayWritable l = (ArrayWritable) w;

			String L = l.get()[1].toString();
			entry.put(new Text(ElementsUtils.posMatch(L.toString())  + "_" + count), ElementsUtils.fetchRange(L.toString()));
			count++;
		}

		verify(context).write(eq(pageNo), any(MapWritable.class));

		ArrayWritable aw = mock(ArrayWritable.class);
		when(context.getCurrentValue()).thenReturn(aw);
		when(context.getCurrentKey()).thenReturn(new IntWritable(new Integer(pageKey)));
		when(aw.toStrings()).thenReturn(two.toStrings());

		context.getCurrentValue();
	}

	@After
	public void tearDown() {
	}
}