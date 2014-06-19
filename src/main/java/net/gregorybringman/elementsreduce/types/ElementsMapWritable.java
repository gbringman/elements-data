package net.gregorybringman.elementsreduce.types;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Writable;

public class ElementsMapWritable extends MapWritable {
	public ElementsMapWritable() {
		super();
	}

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