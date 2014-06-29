package net.gregorybringman.elementsreduce.types;

import java.util.Arrays;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;

/**
 * A child wrapper around {@link ArrayWritable} that permits a client to compare
 * its {@link ArrayWritable} instances with a simple invocation of 
 * {@link ElementsStringArrayWritable#equals}.
 * 
 * @author Gregory Bringman
 * 
 */
public class ElementsStringArrayWritable extends ArrayWritable {
    public ElementsStringArrayWritable(String[] items) {
        super(items);
    }

    public ElementsStringArrayWritable() {
        super(Text.class);
    }

    /**
     * <p>
     * The hash code returned by the {this#hashCode} method is a determined by  
     * an {@link Arrays#deepEquals} call on the ArrayWritable values rendered
     * as {@link String} objects. Array values as strings can preserve the 
     * exact data of the ArrayWritable, just as in the instantiation of 
     * precision numbers using Strings given requirements of exact scale.
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.toStrings());
    }

    /**
     * <p>
     * Equality in this overridden {#equals} method is a result of an
     * {@link Arrays#deepEquals} comparison
     * </p>
     * 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ElementsStringArrayWritable other = (ElementsStringArrayWritable) obj;

        return Arrays.deepEquals(this.toStrings(), other.toStrings());
    }
}