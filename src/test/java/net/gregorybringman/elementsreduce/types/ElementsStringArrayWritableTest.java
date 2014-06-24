package net.gregorybringman.elementsreduce.types;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link ElementsStringArrayWritable} class.
 * 
 * @author Gregory Bringman
 * 
 */
public class ElementsStringArrayWritableTest {

	private ElementsStringArrayWritable arrayWritable;
	private ElementsStringArrayWritable identical;
	private ElementsStringArrayWritable identical2;
	private ElementsStringArrayWritable different;

	/*
	 * Test that hash codes of identical {@link ElementsStringArrayWritable}
	 * objects are equal and that hash codes of different {@link
	 * ElementsStringArrayWritable} objects are NOT equal.
	 */
	@Before
	public void setUp() {

		// Transitive, reflexive, symmetric, blah, blah, blah....

		arrayWritable = new ElementsStringArrayWritable(new String[] { "1-2", "4-6" });
		identical = new ElementsStringArrayWritable(new String[] { "1-2", "4-6" });
		identical2 = new ElementsStringArrayWritable(new String[] { "1-2", "4-6" });
		different = new ElementsStringArrayWritable(new String[] { "1-6", "7-15" });
	}

	@Test
	public void hashcode() {
		Assert.assertTrue(arrayWritable.hashCode() == identical.hashCode());
		Assert.assertTrue(identical.hashCode() == arrayWritable.hashCode());
		Assert.assertTrue(arrayWritable.hashCode() == arrayWritable.hashCode());
		Assert.assertTrue(arrayWritable.hashCode() == arrayWritable.hashCode()
				&& identical.hashCode() == identical2.hashCode());

		Assert.assertFalse(arrayWritable.hashCode() == different.hashCode());
		Assert.assertFalse(arrayWritable.hashCode() == 0);
		Assert.assertFalse(arrayWritable.hashCode() == new String().hashCode());
	}

    /*
     * Test that the equals method evaluates to {@link Boolean.TRUE} when two
     * {@link ElementsStringArrayWritable} instances are equal, to 
     * {@link Boolean.FALSE}
     */
	@Test
	public void equals() {
		Assert.assertTrue(arrayWritable.equals(identical));
		Assert.assertTrue(identical.equals(arrayWritable));
		Assert.assertTrue(arrayWritable.equals(arrayWritable));
		Assert.assertTrue(arrayWritable.equals(arrayWritable)
				&& identical.equals(identical2));
		Assert.assertFalse(arrayWritable.equals(different));
		Assert.assertFalse(arrayWritable.equals(null));
		Assert.assertFalse(arrayWritable.equals(new String()));
	}
}