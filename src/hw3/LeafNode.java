package hw3;

import java.util.ArrayList;

import hw1.RelationalOperator;

public class LeafNode implements Node {
	private int degree;
	private InnerNode parent;
	
	ArrayList<Entry> entries;
	public LeafNode(int degree) {
		this.degree = degree;
	}
	
	public LeafNode(int degree, InnerNode parent) {
		this.parent = parent;
	}
	
	public ArrayList<Entry> getEntries() {
		return this.entries;
	}

	public InnerNode getParent() {
		return this.parent;
	}
	public void insertEntry(Entry e) {	
		for(int i = 0; i < this.entries.size(); i++)
			if(e.getField().compare(RelationalOperator.LT, this.entries.get(i).getField())) {
				this.entries.add(i, e);
				return;
			}
		this.entries.add(e);
		
	}
	public int getDegree() {
		return this.degree;
	}
	public boolean overDegree() {
		return this.entries.size() > this.degree;
	}
	
	public boolean isLeafNode() {
		return true;
	}

}