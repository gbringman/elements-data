package net.gregorybringman.elementsreduce;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests the {@link ElementsVersionsReducer} class.
 * 
 * @author Gregory Bringman
 */
@RunWith(MockitoJUnitRunner.class)
public class ElementsVersionsReducerTest {

    @SuppressWarnings("unused")
    private final String data = "319, 1-4::345, 15-19\n5-7::30, 15-18\n8-10::338, "
                              + "4-8\n11-19:21r:332, 9 à 333, 1\n20-23::303, 15-22";

    ElementsVersionsMapper mapper;
    ElementsVersionsReducer reducer;
    ArrayWritable keys;
    ArrayWritable one;
    ArrayWritable two;
    Writable[] versions, expected;
    MapWritable entry;
    String pageKey = "30";

    @Before
    public void setUp() {

        reducer = new ElementsVersionsReducer();
        keys = new ArrayWritable(new String[] { "319, 1-4", "319, 5-7" });
        one = new ArrayWritable(new String[] { "", "345, 15-19" });
        two = new ArrayWritable(new String[] { "", pageKey + ",15-18" });
        versions = new Writable[] { one, two };
        expected = new Writable[] { one, two };
        entry = new MapWritable();
    }

    /**
     * Test that a POS is created that becomes the key to the line ranges of
     * markers to insert into sample text from the Vandeul (V) and Leningrad (L)
     * editions.
     * 
     * @throws IOException
     *             If the reduced data cannot be added to the Hadoop context.
     * @throws InterruptedException
     *             If the reduced data cannot be added to the Hadoop context.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void reduce() throws IOException, InterruptedException {

        ElementsVersionsReducer.Context context = mock(ElementsVersionsReducer.Context.class);

        IntWritable pageNo = ElementsUtils.fetchPage(keys.toStrings()[0]);

        reducer.reduce(pageNo, one, context);

        int count = 1;
        for (Writable w : expected) {

            ArrayWritable l = (ArrayWritable) w;

            String L = l.get()[1].toString();
            entry.put(new Text(ElementsUtils.posMatch(L.toString()) + "_"
                    + count), ElementsUtils.fetchRange(L.toString()));
            count++;
        }

        verify(context).write(eq(pageNo), any(MapWritable.class));
    }

    @After
    public void tearDown() {
    }
}