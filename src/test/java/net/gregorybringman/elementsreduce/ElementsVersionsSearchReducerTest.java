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

public class ElementsVersionsSearchReducerTest extends AbstractTextHelper {
    
    private ElementsVersionsSearchReducer.Context context;
    private ElementsVersionsSearchReducer reducer;
    private ElementsMapWritable version;
    private IntWritable pageNo;
    
    @Before
    public void setUp() {
        reducer = new ElementsVersionsSearchReducer();
        pageNo = new IntWritable(293);
        version = new ElementsMapWritable();
        version.put(pageNo, new Text(tei()));
        context = mock(ElementsVersionsSearchReducer.Context.class);
    }

    @Test
    public void reduce() throws InterruptedException, IOException {
        
        Text value = new Text(retrievedTeiSection());
        
        reducer.reduce(pageNo, version, context);
        Text key = new Text(String.valueOf(pageNo.get()));
        
        verify(context).write(key, value);
    }
}