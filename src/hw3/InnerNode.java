package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class InnerNode implements Node {
	private ArrayList<Field> keys;
	private ArrayList<Node> children;
	private int degree;
	public InnerNode parent;
	public InnerNode(int degree) {
		this.keys = new ArrayList<Field>();
		this.children = new ArrayList<Node>();
		this.degree = degree;
	}
	public InnerNode(int degree, InnerNode parent) {
		this.parent = parent;
		this.keys = new ArrayList<Field>();
		this.children = new ArrayList<Node>();
		this.degree = degree;
	}
	
	public ArrayList<Field> getKeys() {
		return this.keys;
	}
	
	public InnerNode getParent() {
		return this.parent;
	}
	public ArrayList<Node> getChildren() {
		return this.children;
	}

	public int getDegree() {
		return this.degree;
	}
	
	public void setParent(Node p) {
		this.parent = (InnerNode) p;
	}
	public boolean isLeafNode() {
		return false;
	}
	public boolean overDegree() {
		return this.children.size() > this.degree;
	}
	public void insertKeys(Field e) {
		for(int i = 0; i < this.keys.size(); i++)
			if(e.compare(RelationalOperator.LT, this.keys.get(i))) {
				this.keys.add(i, e);
				return;
			}
		this.keys.add(e);
	}
	public void UpdateKeys(Field e1, Field e2) {
		for(int i = 0; i < this.keys.size(); i++)
			if(e1.compare(RelationalOperator.EQ, this.keys.get(i))) {
				this.keys.set(i, e2);
				return;
			}
//		if(this.parent != null) {
//			this.parent.UpdateKeys(e1, e2);
//		}
	}
	public InnerNode getLeftNeighbor() {
	    int index = this.parent.getChildren().indexOf(this);
	    if (index-1>=0) {
	    	return (InnerNode)this.parent.getChildren().get(index-1);
	    }else {
	    	return null;
	    }
	    
	}
	public InnerNode getRightNeighbor() {
	    int index = this.parent.getChildren().indexOf(this);
	    if (index+1<this.parent.getChildren().size()) {
	    	return (InnerNode)this.parent.getChildren().get(index+1);
	    }else {
	    	return null;
	    }
	    
	}
	public boolean couldBorrow() {
		if(this.parent == null) {
			return false;
		}
		
		if(this.getLeftNeighbor()!=null&&this.getLeftNeighbor().getKeys().size()>1 ) {
			return true;
		}else if (this.getRightNeighbor()!=null && this.getRightNeighbor().getKeys().size()>1) {
			return true;
		}
		return false;
	}

}