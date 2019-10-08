package hw2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import hw1.Field;
import hw1.RelationalOperator;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

/**
 * This class provides methods to perform relational algebra operations. It will be used
 * to implement SQL queries.
 * @author Shitao Chen
 *
 */
public class Relation {

	private ArrayList<Tuple> tuples;
	private TupleDesc td;
	
	public Relation(ArrayList<Tuple> l, TupleDesc td) {
		this.tuples = l;
		this.td = td;
	}
	
	/**
	 * This method performs a select operation on a relation
	 * @param field number (refer to TupleDesc) of the field to be compared, left side of comparison
	 * @param op the comparison operator
	 * @param operand a constant to be compared against the given column
	 * @return
	 */
	public Relation select(int field, RelationalOperator op, Field operand) {
		//your code here
		ArrayList<Tuple> newTuples = (ArrayList<Tuple>) this.tuples
				.stream()
				.filter(e -> e.getField(field).compare(op, operand))
				.collect(Collectors.toList());;
		return new Relation(newTuples, this.td);
	}
	/**
	 * This method performs a rename operation on a relation
	 * @param fields the field numbers (refer to TupleDesc) of the fields to be renamed
	 * @param names a list of new names. The order of these names is the same as the order of field numbers in the field list
	 * @return
	 */
	public Relation rename(ArrayList<Integer> fields, ArrayList<String> names) throws Exception {
		Type[] types = new Type[this.td.numFields()];
		String[] field = new String[this.td.numFields()];
		
		for(int i = 0; i < this.td.numFields(); i++) {
			types[i] = this.td.getType(i);
			field[i] = this.td.getFieldName(i);
		}
		for(int i = 0; i < fields.size(); i++) {
			if(names.get(i) != "")
				field[fields.get(i)] = names.get(i);
		}
		Set<String> set = Arrays.stream(field).collect(Collectors.toSet());
		if(set.size() != this.td.numFields())
			throw new Exception();
		
		return new Relation(this.tuples, new TupleDesc(types, field));
	}
	
	/**
	 * This method performs a project operation on a relation
	 * @param fields a list of field numbers (refer to TupleDesc) that should be in the result
	 * @return
	 */
	public Relation project(ArrayList<Integer> fields) {
		Type[] types = new Type[fields.size()];
		String[] field = new String[fields.size()];
		for(int i = 0; i < fields.size(); i++) {
			
			try {
				if(fields.get(i)>td.numFields()-1) {
					throw new IllegalArgumentException();
				}
				types[i] = this.td.getType(fields.get(i));
				field[i] = this.td.getFieldName(fields.get(i));
			}catch (IllegalArgumentException e) {
				throw new IllegalArgumentException();
			}
			
		}
		
		TupleDesc newTd = new TupleDesc(types, field);
		
		ArrayList<Tuple> newTupleList = new ArrayList<>();
		if(newTd.getSize() == 0)
			return new Relation(newTupleList, newTd);
		for(Tuple tuple: this.tuples) {
			Tuple e = new Tuple(newTd);
			for(int j = 0; j < fields.size(); j++)
				e.setField(j, tuple.getField(fields.get(j)));
			newTupleList.add(e);
		}
		return new Relation(newTupleList, newTd);
	}
	
	/**
	 * This method performs a join between this relation and a second relation.
	 * The resulting relation will contain all of the columns from both of the given relations,
	 * joined using the equality operator (=)
	 * @param other the relation to be joined
	 * @param field1 the field number (refer to TupleDesc) from this relation to be used in the join condition
	 * @param field2 the field number (refer to TupleDesc) from other to be used in the join condition
	 * @return
	 */
	public Relation join(Relation other, int field1, int field2) {
		int numField1 = this.td.numFields();
		int numField2 = other.getDesc().numFields();
		Type[] types = new Type[numField1 + numField2];
		String[] field = new String[numField1 + numField2];
		for(int i = 0; i < numField1; i++) {
			types[i] = this.td.getType(i);
			field[i] = this.td.getFieldName(i);
		}
		for(int i = 0; i < numField2; i++) {
			types[numField1 + i] = other.getDesc().getType(i);
			field[numField1 + i] = other.getDesc().getFieldName(i);
		}
		TupleDesc newTd = new TupleDesc(types, field);
		ArrayList<Tuple> cartesianProduct  = new ArrayList<>();
		for(Tuple tuple1: this.tuples)
			for(Tuple tuple2: other.getTuples()) {
				Tuple tmp = new Tuple(newTd);
				for(int i = 0; i < numField1; i++)
					tmp.setField(i, tuple1.getField(i));
				for(int i = 0; i < numField2; i++)
					tmp.setField(i + numField1, tuple2.getField(i));
				cartesianProduct.add(tmp);
			}
		
		ArrayList<Tuple> newTuples = (ArrayList<Tuple>) cartesianProduct
				.stream()
				.filter(e -> e
						.getField(field1)
						.compare(RelationalOperator.EQ,
									e.getField(field2 + numField1)
								)
					    )
				.collect(Collectors.toList());
		return new Relation(newTuples, newTd);
	}
	
	/**
	 * Performs an aggregation operation on a relation. See the lab write up for details.
	 * @param op the aggregation operation to be performed
	 * @param groupBy whether or not a grouping should be performed
	 * @return
	 */
	public Relation aggregate(AggregateOperator op, boolean groupBy) {
		Aggregator agg = new Aggregator(op, groupBy, this.td);
		for(Tuple tp: this.tuples)
			agg.merge(tp);
		return new Relation(agg.getResults(), this.td);
	}
	
	public TupleDesc getDesc() {
		return this.td;
	}
	
	public ArrayList<Tuple> getTuples() {
		return this.tuples;
	}
	
	/**
	 * Returns a string representation of this relation. The string representation should
	 * first contain the TupleDesc, followed by each of the tuples in this relation
	 */
	public String toString() {
		String ret = this.td.toString() + " ";
		for(int i = 0; i < this.tuples.size(); i++)
			ret += this.tuples.get(i).toString();
		return ret;
	}
}
