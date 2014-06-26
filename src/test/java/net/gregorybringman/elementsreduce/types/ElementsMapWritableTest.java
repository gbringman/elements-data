package net.gregorybringman.elementsreduce.types;

import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ElementsMapWritable} class.
 * 
 * @author Gregory Bringman
 * 
 */
public class ElementsMapWritableTest {

    private final Text key = new Text("key");
    private final Text key2 = new Text("key2");

    private final ElementsStringArrayWritable value = 
        new ElementsStringArrayWritable(new String[] { "1-2", "4-6" });
    private final ElementsStringArrayWritable value2 = 
        new ElementsStringArrayWritable(new String[] { "1-6", "7-15" });

    private ElementsMapWritable mapWritable;
    private ElementsMapWritable identical;
    private ElementsMapWritable identical2;
    private ElementsMapWritable different;
    private ElementsMapWritable different2;

    @Before
    public void setUp() {

        mapWritable = new ElementsMapWritable();
        identical = new ElementsMapWritable();
        identical2 = new ElementsMapWritable();
        different = new ElementsMapWritable();
        different2 = new ElementsMapWritable();

        mapWritable.put(key, value);
        identical.put(key, value);
        identical2.put(key, value);
        different.put(key, value2);
        different2.put(key2, value2);
    }

    /*
     * Test that hash codes of identical {@link ElementsMapWritable} objects are
     * equal and that hash codes of different {@link ElementsMapWritable}
     * objects are not equal.
     */
    @Test
    public void hashcode() {
        
        // Transitive, reflexive, symmetric, blah, blah, blah....

        Assert.assertTrue(mapWritable.hashCode() == identical.hashCode());
        Assert.assertTrue(identical.hashCode() == mapWritable.hashCode());
        Assert.assertTrue(mapWritable.hashCode() == mapWritable.hashCode());
        Assert.assertTrue(mapWritable.hashCode() == identical.hashCode()
            && identical.hashCode() == identical2.hashCode());
        Assert.assertFalse(mapWritable.hashCode() == different.hashCode());
        Assert.assertFalse(mapWritable.hashCode() == 0);
        Assert.assertFalse(mapWritable.hashCode() == new String().hashCode());
    }

    /*
     * Test that the equals method evaluates to {@link Boolean.TRUE} when two
     * {@link ElementsMapWritable} instances are equal, to {@link Boolean.FALSE}
     */
    @Test
    public void equals() {

        // Transitive, reflexive, symmetric, blah, blah, blah....

        Assert.assertTrue(mapWritable.equals(identical));
        Assert.assertTrue(identical.equals(mapWritable));
        Assert.assertTrue(mapWritable.equals(mapWritable));
        Assert.assertTrue(mapWritable.equals(identical) && identical.equals(identical2));
        Assert.assertFalse(mapWritable.equals(different));
        Assert.assertFalse(mapWritable.equals(null));
        Assert.assertFalse(mapWritable.equals(new String()));
        Assert.assertFalse(mapWritable.equals(different2));

        ElementsStringArrayWritable extraValue = new ElementsStringArrayWritable(
            new String[] { "3-6", "7-14" });
        different2.put(key, extraValue);

        Assert.assertFalse(mapWritable.equals(different2));
    }
}