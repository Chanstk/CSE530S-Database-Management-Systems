package hw3;

import java.util.ArrayList;

import hw1.Field;

public class InnerNode implements Node {
	private ArrayList<Field> keys;
	private ArrayList<Node> children;
	private int degree;
	private Node parent;
	public InnerNode(int degree) {
		this.degree = degree;
	}
	public InnerNode(int degree, Node parent) {
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

}