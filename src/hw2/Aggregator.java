package hw2;

import java.util.ArrayList;
import java.util.HashMap;

import hw1.Field;
import hw1.IntField;
import hw1.RelationalOperator;
import hw1.StringField;
import hw1.Tuple;
import hw1.TupleDesc;
import hw1.Type;

/**
 * A class to perform various aggregations, by accepting one tuple at a time
 * @author Shitao Chen
 *
 */
public class Aggregator {
	private ArrayList<Tuple> tuples;
	private TupleDesc td;
	private boolean groupBy;
	private AggregateOperator o;
	private HashMap<IntField, Integer> countMap_Int = new HashMap<>(); 
	private HashMap<Field, Field> listMap = new HashMap<>(); 
	private IntField constantTmp;
	public Aggregator(AggregateOperator o, boolean groupBy, TupleDesc td) {
		this.o = o;
		this.groupBy = groupBy;
		this.td = td;
		this.tuples = new ArrayList<Tuple>();
		
		tuples.add(new Tuple(td));
		switch(o){
			case COUNT:
				this.tuples.get(0).setField(0, new IntField(0));
				break;
			case MAX:
				this.tuples.get(0).setField(0, new StringField("a"));
				if(td.getType(0) == Type.INT)
					this.tuples.get(0).setField(0, new IntField(Integer.MIN_VALUE));
				break;
			case MIN:
				this.tuples.get(0).setField(0, new StringField("z"));
				if(td.getType(0) == Type.INT)
					this.tuples.get(0).setField(0, new IntField(Integer.MAX_VALUE));
				break;
			case AVG:
			case SUM:
				this.tuples.get(0).setField(0, new IntField(0));
				break;
		}
		constantTmp = new IntField(0);
		countMap_Int.put(constantTmp, 0);
	}

	/**
	 * Merges the given tuple into the current aggregation
	 * @param t the tuple to be aggregated
	 */
	public void merge(Tuple t) {
		if(this.groupBy) {
			merge_Group(t);
			return;
		}
		switch(o){
			case COUNT:
				IntField times = (IntField) this.tuples.get(0).getField(0);
				this.tuples.get(0).setField(0, new IntField(times.getValue() + 1));
				break;
			case MAX:
				if(!this.tuples.get(0).getField(0).compare(RelationalOperator.GT, t.getField(0))) {
					this.tuples.get(0).setField(0, t.getField(0));
				}
				break;
			case MIN:
				if(this.tuples.get(0).getField(0).compare(RelationalOperator.GT, t.getField(0))) {
					this.tuples.get(0).setField(0, t.getField(0));
				}
				break;
			case AVG:
			case SUM:
				IntField sums = (IntField) this.tuples.get(0).getField(0);
				this.tuples.get(0).setField(0, new IntField(sums.getValue() + ((IntField) t.getField(0)).getValue()));
				countMap_Int.put(constantTmp, countMap_Int.get(constantTmp) + 1);
				break;
		}
	}
	
	private void merge_Group(Tuple t) {

		Field key =t.getField(0);
		switch(o){
			case COUNT:
				int times = countMap_Int.getOrDefault(t.getField(0), 0);
				countMap_Int.put((IntField) t.getField(0), times + 1);
				break;
			case MAX:
				if(listMap.getOrDefault(key, null) == null || 
				!listMap.get(key).compare(RelationalOperator.GT, t.getField(1)))
					listMap.put(key, t.getField(1));
				break;
			case MIN:
				if(listMap.getOrDefault(key, null) == null || 
				listMap.get(key).compare(RelationalOperator.GT, t.getField(1)))
					listMap.put(key, t.getField(1));
				break;
			case AVG:
			case SUM:
				IntField sums = (IntField) listMap.getOrDefault(key, new IntField(0));
				listMap.put(key, new IntField(sums.getValue() + ((IntField)t.getField(1)).getValue()));
				countMap_Int.put((IntField) key, countMap_Int.getOrDefault(t.getField(0), 0) + 1);
				break;
		}
	}
	
	
	/**
	 * Returns the result of the aggregation
	 * @return a list containing the tuples after aggregation
	 */
	public ArrayList<Tuple> getResults() {
		if(this.groupBy)
			return this.getResults_Group();
		
		if(o == AggregateOperator.AVG) {
			IntField sums = (IntField) this.tuples.get(0).getField(0);
			int count = countMap_Int.get(constantTmp);
			this.tuples.get(0).setField(0, new IntField(sums.getValue() / count));
		}
		return this.tuples;
	}
	private ArrayList<Tuple> getResults_Group(){
		this.tuples.clear();
		for(Field f : listMap.keySet()) {
			Tuple t = new Tuple(this.td);
			if(o == AggregateOperator.AVG) {
				int v =  ((IntField)listMap.get(f)).getValue() / countMap_Int.get(f);
				t.setField(0, f);
				t.setField(1, new IntField(v));
			}else {
				t.setField(0, f);
				t.setField(1, listMap.get(f));
			}
			this.tuples.add(t);
		}
		return this.tuples;
	}
	
}
