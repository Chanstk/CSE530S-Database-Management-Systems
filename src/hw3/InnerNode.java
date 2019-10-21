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

}