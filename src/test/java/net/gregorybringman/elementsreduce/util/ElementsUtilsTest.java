package net.gregorybringman.elementsreduce.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;

import java.io.IOException;

import net.gregorybringman.elementsreduce.ElementsVersionsMapper;
import net.gregorybringman.elementsreduce.ElementsVersionsReducer;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests the {@link ElementsUtils} class.
 * 
 * @author Gregory Bringman
 */
@RunWith(MockitoJUnitRunner.class)
public class ElementsUtilsTest {

    ElementsVersionsMapper mapper;
    ElementsVersionsReducer reducer;
    ArrayWritable keys;
    ArrayWritable one;
    ArrayWritable two;
    Writable[] versions, expected;
    MapWritable entry;
    String expectedText;

    @Before
    public void setUp() {

        mapper = new ElementsVersionsMapper();
        reducer = new ElementsVersionsReducer();
        keys = new ArrayWritable(new String[] { "319, 1-4", "319, 5-7" });
        one = new ArrayWritable(new String[] { "", "345, 15-19" });
        two = new ArrayWritable(new String[] { "", "30,15-18" });
        versions = new Writable[] { one, two };
        expected = new Writable[] { one, two };

        entry = new MapWritable();
        expectedText = "<div class=\"range\">A\nB\nC\nD</div><div class=\"range\">E\nF\nG\nH</div>";
    }

    /*
     * Test that the line range for a page correspondence entry (with both pages
     * and line numbers in the source) is extracted.
     */
    @Test
    public void fetchRange() {

        ArrayWritable range = ElementsUtils.fetchRange("30, 15-18");
        ArrayWritable expected = new ArrayWritable(new String[] { "15", "18" });

        assertEquals("Created Range is incorrect", expected.getValueClass(),
            range.getValueClass());
    }

    /*
     * Test that the {@link Pattern} objects created from the RegEx equivalents
     * of the {@link ElementsGrammar} detect their respective parts of (page
     * equivalence) text.
     */
    @Test
    public void posMatch() {

        String pos = "page";
        assertEquals("Part of speech does not match.", pos,
            ElementsUtils.posMatch("319,"));
        
        pos = "pagewtext";
        assertEquals("Part of speech does not match.", pos,
            ElementsUtils.posMatch("319, Avertissement"));
        assertEquals("Part of speech does not match.", pos,
            ElementsUtils.posMatch("319, 320 Ch I"));
        
        pos = "pagewlinerng";
        assertEquals("Part of speech does not match.", pos,
            ElementsUtils.posMatch("319, 1-4"));
        
        pos = "pagelinetopageline";
        assertEquals("Part of speech does not match.", pos,
            ElementsUtils.posMatch("320 à 321, 13"));
        
        pos = "linerng";
        assertEquals("Part of speech does not match.", pos,
            ElementsUtils.posMatch("1-4"));
        
        pos = "structuraltext";
        assertEquals("Part of speech does not match.", pos,
            ElementsUtils.posMatch("Ch.II Tissue cellulaire"));
    }

    /*
     * Test that the range tags around the text to be marked up correspond to
     * the POS model page and line ranges.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void markupPages() throws IOException, InterruptedException {

        String page = "";

        for (int i = 0; i < 8; i++) {
            page = page + "line" + i + "\n";
        }

        ElementsVersionsReducer.Context context = mock(ElementsVersionsReducer.Context.class);

        IntWritable pageNo = ElementsUtils.fetchPage(keys.get()[0].toString());
        reducer.reduce(pageNo, one, context);

        int count = 1;
        for (Writable w : expected) {

            ArrayWritable l = (ArrayWritable) w;

            String L = l.get()[1].toString();
            entry.put(new Text(ElementsUtils.posMatch(L) + "_" + count),
                    ElementsUtils.fetchRange(L));
            count++;
        }
        
        verify(context).write(eq(pageNo), any(MapWritable.class));

        assertEquals("Text incorrectly marked up!", expectedText,
            ElementsUtils.markupPages(entry, "A\nB\nC\nD\nE\nF\nG\nH"));
    }
}