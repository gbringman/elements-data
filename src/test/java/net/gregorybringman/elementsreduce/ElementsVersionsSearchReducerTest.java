package net.gregorybringman.elementsreduce;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import net.gregorybringman.elementsreduce.types.ElementsMapWritable;
import net.gregorybringman.elementsreduce.util.AbstractTextHelper;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ElementsVersionsSearchReducer} class.
 * 
 * @author Gregory Bringman
 * 
 */
public class ElementsVersionsSearchReducerTest extends AbstractTextHelper {

    private ElementsVersionsSearchReducer.Context context;
    private ElementsVersionsSearchReducer reducer;
    private ElementsMapWritable version;
    private IntWritable pageNo;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        reducer = new ElementsVersionsSearchReducer();
        pageNo = new IntWritable(293);
        version = new ElementsMapWritable();
        version.put(pageNo, new Text(tei(null)));
        context = mock(ElementsVersionsSearchReducer.Context.class);
    }

    /*
     * Test that the final reduced context contains the retrieved text, based
     * upon page number.
     */
    @Test
    public void reduce() throws InterruptedException, IOException {

        Text value = new Text(retrievedTeiSection());

        reducer.reduce(pageNo, version, context);
        Text key = new Text(String.valueOf(pageNo.get()));

        verify(context).write(key, value);
    }

    /*
     * Ensure that regexes for page break tags handle whitespace correctly.
     */
    @Test
    public void reduceWithAbnormalWhitespace() throws InterruptedException,
            IOException {

        version = new ElementsMapWritable();
        version.put(pageNo, new Text(tei(teiBodyDPVOnlyAbnormal())));

        // Retrieved section should be the same (no page break tags
        // with extra whitespace)

        Text value = new Text(retrievedTeiSection());

        reducer.reduce(pageNo, version, context);
        Text key = new Text(String.valueOf(pageNo.get()));

        verify(context).write(key, value);
    }
}