package hw3;

import java.util.ArrayList;

import hw1.Field;
import hw1.RelationalOperator;

public class InnerNode implements Node {
	private ArrayList<Field> keys;
	private ArrayList<Node> children;
	private int degree;
	private InnerNode parent;
	public InnerNode(int degree) {
		this.degree = degree;
	}
	public InnerNode(int degree, InnerNode parent) {
		this.parent = parent;
	}
	
	public ArrayList<Field> getKeys() {
		return this.keys;
	}
	
	public Node getParent() {
		return this.parent;
	}
	public ArrayList<Node> getChildren() {
		return this.children;
	}

	public int getDegree() {
		return this.degree;
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