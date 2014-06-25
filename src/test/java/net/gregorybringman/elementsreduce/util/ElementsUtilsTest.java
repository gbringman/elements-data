package net.gregorybringman.elementsreduce.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import net.gregorybringman.elementsreduce.types.ElementsMapWritable;
import net.gregorybringman.elementsreduce.types.ElementsStringArrayWritable;

import org.apache.hadoop.io.ArrayWritable;
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

    Writable[] expected;
    ElementsMapWritable entry;
    String expectedText;

    @Before
    public void setUp() {

        ElementsStringArrayWritable one = new ElementsStringArrayWritable(new String[] { "", "345, 15-19" });
        ElementsStringArrayWritable two = new ElementsStringArrayWritable(new String[] { "", "30,15-18" });
        
        entry = new ElementsMapWritable();
        expected = new Writable[] { one, two };
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
    @Test
    public void markupPages() throws IOException, InterruptedException {

        String page = "";

        for (int i = 0; i < 8; i++) {
            page = page + "line" + i + "\n";
        }

        ElementsUtils.populateEntry(entry, expected);
        
        assertEquals("Text incorrectly marked up!", expectedText,
            ElementsUtils.markupPages(entry, "A\nB\nC\nD\nE\nF\nG\nH"));
    }
    
    @Test
    public void populateEntry() {
        ElementsStringArrayWritable range = mock(ElementsStringArrayWritable.class);
        Writable[] expectedRanges = new Writable[] { range, range };

        ElementsMapWritable emw = mock(ElementsMapWritable.class);
        String [] stringRanges = new String[] { "", "1, 4-6" };
        
        when(range.toStrings()).thenReturn(stringRanges);
        
        ElementsUtils.populateEntry(emw, expectedRanges);
        
        verify(emw).put(new Text("pagewlinerng_1"), ElementsUtils.fetchRange(stringRanges[1]));
    }
}