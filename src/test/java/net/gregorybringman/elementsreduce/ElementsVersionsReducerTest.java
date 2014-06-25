package net.gregorybringman.elementsreduce;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import net.gregorybringman.elementsreduce.types.ElementsMapWritable;
import net.gregorybringman.elementsreduce.util.ElementsUtils;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
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

    ElementsVersionsReducer reducer;
    ArrayWritable keys;
    ArrayWritable one;
    Writable[] expected;
    ElementsMapWritable entry;

    @Before
    public void setUp() {

        reducer = new ElementsVersionsReducer();
        keys = new ArrayWritable(new String[] { "319, 1-4", "319, 5-7" });
        one = new ArrayWritable(new String[] { "", "345, 15-19" });
        expected = new Writable[] { one};
        entry = new ElementsMapWritable();
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
        ElementsUtils.populateEntry(entry, expected);

        reducer.reduce(pageNo, one, context);
        
        verify(context).write(pageNo, entry);
    }

    @After
    public void tearDown() {
    }
}