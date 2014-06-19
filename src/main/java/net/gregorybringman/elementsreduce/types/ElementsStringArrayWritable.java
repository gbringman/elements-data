package net.gregorybringman.elementsreduce.types;

import java.util.Arrays;

import org.apache.hadoop.io.ArrayWritable;

public class ElementsStringArrayWritable extends ArrayWritable {
	public ElementsStringArrayWritable(String[] items) {
		super(items);
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(this.toStrings());	
	}

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