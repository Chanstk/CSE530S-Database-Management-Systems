package hw2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import hw1.Tuple;
import hw1.TupleDesc;

/**
 * A class to perform various aggregations, by accepting one tuple at a time
 * @author Doug Shook
 *
 */
public class Aggregator {
	private ArrayList<Tuple> tuples;
	private TupleDesc td;
	private boolean groupBy;
	private AggregateOperator o;
	public Aggregator(AggregateOperator o, boolean groupBy, TupleDesc td) {
		this.o = o;
		this.groupBy = groupBy;
		this.td = td;
		this.tuples = new ArrayList<Tuple>();
	}

	/**
	 * Merges the given tuple into the current aggregation
	 * @param t the tuple to be aggregated
	 */
	public void merge(Tuple t) {
		//your code here
		
	}
	
	/**
	 * Returns the result of the aggregation
	 * @return a list containing the tuples after aggregation
	 */
	public ArrayList<Tuple> getResults() {
		//your code here
		return null;
	}

}
