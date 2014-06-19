package net.gregorybringman.elementsreduce;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

public class ElementsVersionsMarkupReducerTest {

	ElementsVersionsMapper mapper;
	ElementsVersionsMarkupReducer reducer;
	ArrayWritable keys;
	ArrayWritable one;
	ArrayWritable two;
	Writable[] versions, expected;
	MapWritable entry;
	String expectedText = 
			"<div class=\"range\">Mr *** conceived of the project of drawing up the elements of physiology while \n"
			+ "reading the works of Baron de Haller.  For several months, he collected what \n"
			+ "seemed to him appropriate or essential to put in these elements: notes and extracts \n"
			+ "were jotted down on a few isolated pages.  Death having prevented Mr *** from realizing</div>"
			+ "<div class=\"range\"> the project, for which he only happened to prepare the material, these notes are \n" 
			+ "believed to be united in a single copy.  No doubt incomplete, and  despite their lack of </div>";

	@Before
	public void setUp() {

		mapper = new ElementsVersionsMapper();
		reducer = new ElementsVersionsMarkupReducer();
		keys = new ArrayWritable(new String[]{"319, 1-4", "319, 5-7"});
		one = new ArrayWritable(new String[]{"","1, 4-6"});
		two = new ArrayWritable(new String[]{"","30,15-18"});
		versions = new Writable[]{one, two};
		expected = new Writable[]{one, two};

		entry = new MapWritable();
	}

	@Test
	public void reduce() throws IOException, InterruptedException {
		String page = "";

		for (int i = 0; i < 8; i++) {
			page = page + "line" + i + "\n";
		}

		@SuppressWarnings("unchecked")
		ElementsVersionsMarkupReducer.Context context = mock(ElementsVersionsMarkupReducer.Context.class);

		IntWritable pageNo = ElementsUtils.fetchPage(keys.get()[0].toString());

		int count = 1;
		for (Writable w : expected) {

			ArrayWritable l = (ArrayWritable) w;

			String L = l.get()[1].toString();
			entry.put(new Text(ElementsUtils.posMatch(L) + "_" + count), ElementsUtils.fetchRange(L));
			count++;
		}

		reducer.reduce(pageNo, entry, context);

		String actualText = ElementsUtils.markupPages(entry, ElementsUtils.fetchTextOfPage());

		verify(context).write(pageNo, new Text(actualText));

		assertEquals("Text incorrectly marked up!", expectedText, actualText);
	}

	@After
	public void tearDown() {
	}
}