package net.gregorybringman.elementsreduce;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import net.gregorybringman.elementsreduce.types.ElementsMapWritable;
import net.gregorybringman.elementsreduce.util.ElementsUtils;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ElementsVersionsMarkupReducer} class.
 * 
 * @author Gregory Bringman
 */
public class ElementsVersionsMarkupReducerTest {

    ElementsVersionsMarkupReducer reducer;
    ArrayWritable keys;
    ArrayWritable one;
    ArrayWritable two;
    Writable[] expected;
    ElementsMapWritable entry;
    
    String expectedText = "<div class=\"range\">Mr *** conceived of the project of drawing up the elements of physiology while \n"
            + "reading the works of Baron de Haller.  For several months, he collected what \n"
            + "seemed to him appropriate or essential to put in these elements: notes and extracts \n"
            + "were jotted down on a few isolated pages.  Death having prevented Mr *** from realizing</div>"
            + "<div class=\"range\"> the project, for which he only happened to prepare the material, these notes are \n"
            + "believed to be united in a single copy.  No doubt incomplete, and  despite their lack of </div>";

    @Before
    public void setUp() {

        reducer = new ElementsVersionsMarkupReducer();
        keys = new ArrayWritable(new String[] { "319, 1-4", "319, 5-7" });
        one = new ArrayWritable(new String[] { "", "1, 4-6" });
        two = new ArrayWritable(new String[] { "", "30,15-18" });
        expected = new Writable[] { one, two };

        entry = new ElementsMapWritable();
    }

    /*
     * Test that the POS models do match, in line range, the text of the
     * Éléments de Physiologie to which they are applied.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void reduce() throws IOException, InterruptedException {
        String page = "";

        for (int i = 0; i < 8; i++) {
            page = page + "line" + i + "\n";
        }

        ElementsVersionsMarkupReducer.Context context = mock(ElementsVersionsMarkupReducer.Context.class);
        IntWritable pageNo = ElementsUtils.fetchPage(keys.get()[0].toString());
        ElementsUtils.populateEntry(entry, expected);
        
        reducer.reduce(pageNo, entry, context);

        verify(context).write(pageNo, new Text(expectedText));
    }

    @After
    public void tearDown() {
    }
}