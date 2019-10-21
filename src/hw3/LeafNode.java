package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class LeafNode implements Node {
	private int degree;
	public InnerNode parent;
	
	ArrayList<Entry> entries;
	public LeafNode(int degree) {
		this.degree = degree;
		this.entries = new ArrayList<Entry>();
	}
	
	public LeafNode(int degree, InnerNode parent) {
		this.degree = degree;
		this.parent = parent;
		this.entries = new ArrayList<Entry>();
	}
	
	public ArrayList<Entry> getEntries() {
		return this.entries;
	}

	public InnerNode getParent() {
		return this.parent;
	}
	public void setParent(Node p) {
		this.parent = (InnerNode) p;
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