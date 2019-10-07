package hw3;

import java.util.ArrayList;

public class LeafNode implements Node {
	private int degree;
	private Node parent;
	
	ArrayList<Entry> entries;
	public LeafNode(int degree) {
		this.degree = degree;
	}
	
	public LeafNode(int degree, Node parent) {
		this.parent = parent;
	}
	
	public ArrayList<Entry> getEntries() {
		return this.entries;
	}

	public Node getParent() {
		return this.parent;
	}
	
	public int getDegree() {
		return this.degree;
	}
	
	public boolean isLeafNode() {
		return true;
	}

}