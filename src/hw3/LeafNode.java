package hw3;

import java.util.ArrayList;

import hw1.Field;
//import hw1.IntField;
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
	public void deleteEntry(Entry e) {	
		for(int i = 0; i < this.entries.size(); i++)
			if(e.getField().compare(RelationalOperator.EQ, this.entries.get(i).getField())) {
				//this.entries.add(i, e);
				if(i==this.entries.size()-1 && i>0) {
					Field np = this.entries.get(i-1).getField();
					this.parent.UpdateKeys(e.getField(), np);
				}
				this.entries.remove(i);

				return;
			}
		
	}
	
	public LeafNode getLeftNeighbor() {
	    int index = this.parent.getChildren().indexOf(this);
	    if (index-1>=0) {
	    	return (LeafNode)this.parent.getChildren().get(index-1);
	    }else {
	    	return null;
	    }
	    
	}
	public LeafNode getRightNeighbor() {
	    int index = this.parent.getChildren().indexOf(this);
	    if (index+1<this.parent.getChildren().size()) {
	    	return (LeafNode)this.parent.getChildren().get(index+1);
	    }else {
	    	return null;
	    }
	    
	}
	public int getDegree() {
		return this.degree;
	}
	public boolean overDegree() {
		return this.entries.size() > this.degree;
	}
	public boolean ToMerge() {
		int low = this.degree/2;
		if (this.degree % 2 != 0) {
			low++;
		}
		return this.entries.size() < low;
	}
	public boolean couldBorrow() {
		int low = this.degree/2;
		if (this.degree % 2 != 0) {
			low++;
		}
		if(this.getLeftNeighbor()!=null&&this.getLeftNeighbor().getEntries().size()>low  ) {
			return true;
		}else if (this.getRightNeighbor()!=null && this.getRightNeighbor().getEntries().size()>low) {
			return true;
		}
		return false;
	}
	
	public boolean isLeafNode() {
		return true;
	}

}