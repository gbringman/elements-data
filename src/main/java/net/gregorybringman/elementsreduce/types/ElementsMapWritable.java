package net.gregorybringman.elementsreduce.types;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Writable;

/**
 * A child wrapper around {@link MapWritable} that permits a client to compare
 * its {@link MapWritable} instances with a simple invocation of 
 * {@link #equals}.
 * 
 * @author Gregory Bringman
 * 
 */
public class ElementsMapWritable extends MapWritable {
    public ElementsMapWritable() {
        super();
    }

    /**
     * Overriding hashcode and equals, only the keys and values of the map are 
     * significant. Since equality is the result of a map structure having all 
     * the same keys mapped to all the same values, this {@link #hashCode()} 
     * implementation hashes both the keys and values of all keys, in the sequence 
     * of the keys in the collection.
     * 
     * So, if each key pair were a hashable object, the hash of each would be
     * used to create a hash code for the entire map object.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (Writable key : this.keySet()) {
            result = prime * result + key.toString().hashCode();
            result = prime * result + this.get(key).toString().hashCode();
        }

        return result;
    }

    /**
     * <p>
     * Equality overriding the {#equals} method is a result of:
     * </p>
     * 
     * <ul>
     * <ol>
     * The size of the compared maps being equal.
     * </ol>
     * <ol>
     * Any key of one map being contained in the other map.
     * </ol>
     * <ol>
     * The value of both Map objects given the key contained in the
     * {@link ElementsMapWritable} object.
     * </ol>
     * </ul>
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        ElementsMapWritable other = (ElementsMapWritable) obj;
        if (this.size() != other.size()) {
            return false;
        }

        for (Writable key : this.keySet()) {
            if (!other.containsKey(key))
                return false;
            if (!this.get(key).toString().equals(other.get(key).toString()))
                return false;
        }

        return true;
    }
}