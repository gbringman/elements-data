package net.gregorybringman.elementsreduce.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import junit.framework.Assert;
import net.gregorybringman.elementsreduce.types.ElementsMapWritable;
import net.gregorybringman.elementsreduce.types.ElementsStringArrayWritable;

import org.apache.hadoop.io.IntWritable;
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

    Writable[] expectedRanges;
    ElementsMapWritable entry;
    String expectedText;

    @Before
    public void setUp() {

        ElementsStringArrayWritable one = new ElementsStringArrayWritable(new String[] { "", "345, 15-19" });
        ElementsStringArrayWritable two = new ElementsStringArrayWritable(new String[] { "", "30,15-18" });
        
        entry = new ElementsMapWritable();
        expectedRanges = new Writable[] { one, two };
        expectedText = "<div class=\"range\">A\nB\nC\nD</div><div class=\"range\">E\nF\nG\nH</div>";
    }

    /*
     * Test that the line range for a page correspondence entry (with both pages
     * and line numbers in the source) is extracted.
     */
    @Test
    public void fetchRange() {

        ElementsStringArrayWritable actual = ElementsUtils.fetchRange("30, 15-18");
        ElementsStringArrayWritable expected = new ElementsStringArrayWritable(new String[] { "15", "18" });

        assertEquals("Created Range is incorrect", expected, actual);
    }

    /*
     * Test that the invocation of a line range match test inside {@link this#fetchRange} 
     * will fail and that the range values will match their initialization value, 0.
     */
    @Test
    public void fetchRangeNoPOSMatch() {

        ElementsStringArrayWritable actual = ElementsUtils.fetchRange("30, 15");
        
        // If there is no match, start and end have only values of initialization...
        ElementsStringArrayWritable expected = new ElementsStringArrayWritable(new String[] { "0", "0" });

        assertEquals("Created Range is incorrect", expected, actual);
    }

    /*
     * Validate that the {@link this#fetchPage} method recognizes page 
     * numbers in the data and creates an equivalent {@link IntWritable} 
     * for each.
     */
    @Test
    public void fetchPage() {
        
        IntWritable actual = ElementsUtils.fetchPage("30, 15-18");
        IntWritable expected = new IntWritable(30);
        
        Assert.assertEquals(expected, actual);
    }

    /*
     * Test that the invocation of a page match test inside {@link this#fetchPage} 
     * will fail and that it then matches its initialization value, 0.
     */
    @Test
    public void fetchPageNoPOSMatch() {
        
        IntWritable actual = ElementsUtils.fetchPage("(suite) à");

        // If there is no match, the page has only values of initialization...
        IntWritable expected = new IntWritable(0);
        
        Assert.assertEquals(expected, actual);
    }

    /*
     * Validate that the selected page is the integer value that is the 
     * right operand of the French lexeme for &quot;to&quot;, &quot;à&quot;.
     * 
     * TODO://evaluate whether this is the desired behavior.
     */
    @Test
    public void fetchPageWithToText() {
        
        // &quot;To text&quot;, meaning a page range is specified with the French
        // lexeme for &quot;to&quot;, an &quot;a&quot; with a grave accent.
        
        IntWritable actual = ElementsUtils.fetchPage("30 à 397");
        IntWritable expected = new IntWritable(397);
        IntWritable notExpected = new IntWritable(30);
        
        Assert.assertEquals(expected, actual);
        Assert.assertNotSame(notExpected, actual);
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

        ElementsUtils.populateEntry(entry, expectedRanges);
        
        assertEquals("Text incorrectly marked up!", expectedText,
            ElementsUtils.markupPages(entry, "A\nB\nC\nD\nE\nF\nG\nH"));
    }
    
    /*
     * Test that an instance of {@link ElementsMapWritable} is created, with, 
     * for only the current map or reduce phase, a properly serialized POS 
     * key.
     */
    @Test
    public void populateEntry() {
        ElementsStringArrayWritable range = mock(ElementsStringArrayWritable.class);
        Writable[] expected = new Writable[] { range, range };

        ElementsMapWritable emw = mock(ElementsMapWritable.class);
        String [] stringRanges = new String[] { "", "1, 4-6" };
        
        when(range.toStrings()).thenReturn(stringRanges);
        
        ElementsUtils.populateEntry(emw, expected);
        
        verify(emw).put(new Text("pagewlinerng_1"), ElementsUtils.fetchRange(stringRanges[1]));
    }
    
    /*
     * Test the condition when incoming data has two instances of the same POS, that 
     * its serial suffix will then disambiguate inside the map structure, i.e.: 
     * &quot;pagewlinerng_1&quot; and &quot;pagewlinerng_4&quot; will be distinct 
     * keys.
     */
    @Test
    public void populateEntryWSerializedKeys() {
        ElementsStringArrayWritable range = mock(ElementsStringArrayWritable.class);
        Writable[] expected = new Writable[] { range, range };

        ElementsMapWritable emw = mock(ElementsMapWritable.class);
        String [] stringRanges = new String[] { "", "1, 4-6", "2, 5-7" };
        
        when(range.toStrings()).thenReturn(stringRanges);
        
        ElementsUtils.populateEntry(emw, expected);
        
        verify(emw).put(new Text("pagewlinerng_1"), ElementsUtils.fetchRange(stringRanges[1]));
        verify(emw).put(new Text("pagewlinerng_2"), ElementsUtils.fetchRange(stringRanges[2]));
    }
}