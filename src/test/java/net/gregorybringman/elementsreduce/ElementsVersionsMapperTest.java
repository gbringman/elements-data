package net.gregorybringman.elementsreduce;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import net.gregorybringman.elementsreduce.types.ElementsStringArrayWritable;
import net.gregorybringman.elementsreduce.util.ElementsUtils;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests the {@link ElementsVersionsMapper} class.
 * 
 * @author Gregory Bringman
 */
@RunWith(MockitoJUnitRunner.class)
public class ElementsVersionsMapperTest {

    private final String data = "15 à 306, 7:11v:32,1-18\n319, 1-4::345, 15-19\n"
        + "5-7::30, 15-18\n8-10::338, 4-8\n" + "11-19:21r:332, 9 à 333, 1\n20-23"
        + "::303, 15-22";

    ElementsVersionsMapper mapper;
    ElementsVersionsReducer reducer;
    ElementsStringArrayWritable keys;
    ElementsStringArrayWritable one;
    ElementsStringArrayWritable two;
    Writable[] versions, expected;

    @Before
    public void setUp() {

        mapper = new ElementsVersionsMapper();
        reducer = new ElementsVersionsReducer();
        keys = new ElementsStringArrayWritable(new String[] { "306, 7", "319, 1-4" });
        one = new ElementsStringArrayWritable(new String[] { "11v", "32,1-18" });
        two = new ElementsStringArrayWritable(new String[] { "", "345,15-19" });
        versions = new Writable[] { one, two };
        expected = new Writable[] { one, two };
    }

    /*
     * Test that the mapper correctly places pages numbers into Hadoop {@link
     * ArrayWritable} instances with the correct values.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void map() throws IOException, InterruptedException {

        String[] lines = data.split("\n");

        Text value = new Text(lines[0]);

        ElementsVersionsMapper.Context context = mock(ElementsVersionsMapper.Context.class);

        mapper.map(null, value, context);
        verify(context).write(ElementsUtils.fetchPage(value.toString()), one);
    }

    /*
     * Test that the mapper does not write the record to the context because its
     * data line is commented out in the input source.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void mapWithCommentedRecord() throws IOException,
            InterruptedException {

        String line = keys.toStrings()[0];
        Text value = new Text("#" + line);

        ElementsVersionsMapper.Context context = mock(ElementsVersionsMapper.Context.class);

        mapper.map(null, value, context);
        verify(context, never()).write(
                ElementsUtils.fetchPage(line.toString()), one);
    }

    /*
     * Test that the mapper does not write the record to the context because
     * this record has more than three elements.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void mapWithTooManyVersions() throws IOException,
            InterruptedException {

        String[] lines = data.split("\n");

        Text value = new Text(lines[0] + ":99, 1-9");

        ElementsVersionsMapper.Context context = mock(ElementsVersionsMapper.Context.class);

        mapper.map(null, value, context);
        verify(context, never()).write(
                ElementsUtils.fetchPage(value.toString()), one);
    }

    @After
    public void tearDown() {
    }
}