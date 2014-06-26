package net.gregorybringman.elementsreduce.util;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.gregorybringman.elementsreduce.grammar.ElementsGrammar;
import net.gregorybringman.elementsreduce.types.ElementsMapWritable;
import net.gregorybringman.elementsreduce.types.ElementsStringArrayWritable;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

/**
 * Provides utility methods for creating data types needed by each stage of map
 * / reduce, and needed for textual markup.
 * 
 * @author Gregory Bringman
 */
public class ElementsUtils {

    public static final String RANGE_TAG_BEGIN = "<div class=\"range\">";
    public static final String RANGE_TAG_END = "</div>";
    public static final String MATCH_FAILURE_CODE = "NaN";

    public static String posMatch(String text) {

        Map<String, Pattern> pos = ElementsGrammar.grammarAsMap();
        Iterator<String> it = pos.keySet().iterator();

        while (it.hasNext()) {
            String key = it.next();
            if (pos.get(key).matcher(text).matches()) {
                return key;
            }
        }

        return MATCH_FAILURE_CODE;
    }

    /**
     * Parse a {@link ElementsGrammar#LINE_RANGE} into a Hadoop
     * {@link ArrayWritable}.
     * 
     * @param text
     *            The string with a range of the format &quot;1-4&quot;
     * @return The {@link ArrayWritable} range.
     */
    public static ElementsStringArrayWritable fetchRange(String text) {

        Matcher m = ElementsGrammar.LINE_RANGE.matcher(text);

        int start = 0;
        int end = 0;

        if (m.find()) {
            String[] split = m.group(0).split(ElementsGrammar.RANGE_DELIMITER);
            start = Integer.parseInt(split[0]);
            end = Integer.parseInt(split[1]);
        }

        ElementsStringArrayWritable range = new ElementsStringArrayWritable(new String[] {
                String.valueOf(start), String.valueOf(end) });
        return range;
    }

    /**
     * Parse a {@link ElementsGrammar#PAGE} into a Hadoop {@link IntWritable}.
     * 
     * @param text
     *            The string with a range of the format
     *            {@link ElementsGrammar#PAGE}.
     * @return The {@link IntWritable} range.
     */
    public static IntWritable fetchPage(String text) {

        String delim = ",";

        Matcher m = ElementsGrammar.PAGE.matcher(text);

        int pageNo = 0;

        if (m.find()) {
            String page = m.group(0);
            String sansSpace = page.replaceAll("\\s", "");
            int to = sansSpace.indexOf("à");
            if (to > -1) {
                page = sansSpace.substring(to + 1);
            }

            page = page.indexOf(delim) > -1 ? page.substring(0,
                    page.indexOf(delim)) : page.trim();
            pageNo = Integer.parseInt(page);
        }

        return new IntWritable(pageNo);
    }

    /**
     * Given a range tag definition, insert its opening and closing forms around
     * a portion of text. Translate all structural characteristics implied by
     * the Mayer data on fragments, i.e. line span, etc.
     * 
     * @param pageModel
     *            A {@link Map} of page keys and their range values.
     * @param text
     *            The text to populate with range tag identifiers.
     * @return The marked up text.
     */
    public static String markupPages(MapWritable pageModel, String text) {

        String[] lines = text.split("\n");

        String l = "";
        String markedUp = "";

        int i = 0;
        int lastRange = 0;

        for (Writable range : pageModel.values()) {

            Writable[] rng = ((ArrayWritable) range).get();

            int start = Integer.parseInt(rng[0].toString());
            int end = Integer.parseInt(rng[1].toString());

            int r = end - start;
            int numLines = r + lastRange + 1;

            markedUp = markedUp + RANGE_TAG_BEGIN;

            for (; i < numLines; i++) {

                l = l + lines[i] + (i == (numLines - 1) ? "" : "\n");
            }

            markedUp = markedUp + l + RANGE_TAG_END;

            l = "";
            i = numLines;
            lastRange = r;
        }

        return markedUp;
    }

    /**
     * Perform the work of associating the POS match to its line range.
     * 
     * @param e
     *      The {@link ElementsMapWritable} to populate.
     * @param expectedRanges
     *      The raw string data assumed to contain line ranges.
     */
    public static void populateEntry(ElementsMapWritable e, Writable[] expectedRanges) {
        
        int count = 1;
        
        for (Writable w : expectedRanges) {
            ElementsStringArrayWritable l = (ElementsStringArrayWritable) w;
            for (String range : l.toStrings()) {
                String match = ElementsUtils.posMatch(range);
                if (match.indexOf(MATCH_FAILURE_CODE) < 0) {
                    e.put(new Text( match + "_" + count), ElementsUtils.fetchRange(range.toString()));
                    count++;
                }
            }
        }
    }

    /**
     * Convenience method providing some text of Éléments de Physiologie.
     * 
     * This text will receive the range markup described by the previous map /
     * reduce stage's POS models.
     * 
     * @return A dummy segment of Éléments de Physiologie.
     */
    public static String fetchTextOfPage() {

        return "Mr *** conceived of the project of drawing up the elements of physiology while \n"
                + "reading the works of Baron de Haller.  For several months, he collected what \n"
                + "seemed to him appropriate or essential to put in these elements: notes and extracts \n"
                + "were jotted down on a few isolated pages.  Death having prevented Mr *** from realizing\n"
                + " the project, for which he only happened to prepare the material, these notes are \n"
                + "believed to be united in a single copy.  No doubt incomplete, and  despite their lack of \n"
                + "order, it is thought that the public will nevertheless receive these fragments with \n"
                + "pleasure, and that one day someone will undertake, after the plan and ideas of Mr ***, \n"
                + "the work he had only sketched.";
    }
}