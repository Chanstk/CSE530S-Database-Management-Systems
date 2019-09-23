package hw1;

import java.sql.Types;
import java.util.HashMap;

/**
 * This class represents a tuple that will contain a single row's worth of information
 * from a table. It also includes information about where it is stored
 * @author Sam Madden modified by Doug Shook, implemented by Shitao Chen
 *
 */
public class Tuple {
	private TupleDesc tupleDesc;
	private Field[] fieldList;
	private int pId;
	private int id;
	/**
	 * Creates a new tuple with the given description
	 * @param t the schema for this tuple
	 */
	public Tuple(TupleDesc t) {
		//your code here
		this.tupleDesc = t;
		this.fieldList = new Field[t.numFields()];
	}
	
	public TupleDesc getDesc() {
		//your code here
		return this.tupleDesc;
	}
	
	/**
	 * retrieves the page id where this tuple is stored
	 * @return the page id of this tuple
	 */
	public int getPid() {
		//your code here
		return this.pId;
	}

	public void setPid(int pid) {
		//your code here
		this.pId = pid;
	}

	/**
	 * retrieves the tuple (slot) id of this tuple
	 * @return the slot where this tuple is stored
	 */
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		//your code here
		this.id = id;
	}
	
	public void setDesc(TupleDesc td) {
		//your code here;
		this.tupleDesc = td;
	}
	
	/**
	 * Stores the given data at the i-th field
	 * @param i the field number to store the data
	 * @param v the data
	 */
	public void setField(int i, Field v) {
		//your code here
		this.fieldList[i] = v;
	}
	
	public Field getField(int i) {
		//your code here
		return this.fieldList[i];
	}
	
	/**
	 * Creates a string representation of this tuple that displays its contents.
	 * You should convert the binary data into a readable format (i.e. display the ints in base-10 and convert
	 * the String columns to readable text).
	 */
	public String toString() {
		
		int i = 0;
		String ret = "";
		for(; i < this.tupleDesc.numFields() - 1; i++) {
			ret += this.fieldList[i].toString() + " | ";
		}
		ret += this.fieldList[i].toString();
		return ret;
	}
}
	