package hw1;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 * @author Shitao Chen
 */
public class TupleDesc {

	private Type[] types;
	private String[] fields;
	
    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr array specifying the number of and types of fields in
     *        this TupleDesc. It must contain at least one entry.
     * @param fieldAr array specifying the names of the fields. Note that names may be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
    	//your code here
    	this.types = typeAr;
    	this.fields = fieldAr;
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        //your code here
    	return this.fields.length;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
    	try {
	        return this.fields[i];
    	} catch(NoSuchElementException e) {
    		throw new NoSuchElementException("not a valid field reference.");
    	}
    }

    /**
     * Find the index of the field with a given name.
     *
     * @param name name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException if no field with a matching name is found.
     */
    public int nameToId(String name) throws NoSuchElementException {
    	
    	try {
	        for(int i = 0; i <= this.numFields(); i++) {
	        	if(name.equals(this.fields[i]))
	        		return i;
	        }
    	} catch(Exception e) {
    		throw new NoSuchElementException("not a valid field reference.");
    	}
    	return -1;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i The index of the field to get the type of. It must be a valid index.
     * @return the type of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public Type getType(int i) throws NoSuchElementException {
        //your code here
    	try {
	        return this.types[i];
    	} catch(NoSuchElementException e) {
    		throw new NoSuchElementException("not a valid field reference.");
    	}
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     * Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
    	//your code here
    	int size = 0;
    	for(int i = 0; i < this.numFields(); i++) {
    		if(this.types[i] == Type.INT)
    			size += 4;
    		else if(this.types[i] == Type.STRING)
    			size += 129;
    	}
    	return size;
    }

    /**
     * Compares the specified object with this TupleDesc for equality.
     * Two TupleDescs are considered equal if they are the same size and if the
     * n-th type in this TupleDesc is equal to the n-th type in td.
     *
     * @param o the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
    	//your code here
    	return o.hashCode() == this.hashCode();
    }
    

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
    	int hashCodes = 0;
    	for(int i = 0; i < this.numFields(); i++) {
    		hashCodes += Math.pow(-1, i) * this.types[i].hashCode();
    	}
    	return hashCodes;
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * @return String describing this descriptor.
     */
    public String toString() {
        //your code here
    	String ret = "";
    	int i = 0;
    	for(; i < this.numFields() - 1; i++) {
    		ret += this.types[i].toString() + "(" + this.fields[i] + "), ";
    	}
    	ret += this.types[i].toString() + "(" + this.fields[i] + ")";
    	return ret;
    }
}
