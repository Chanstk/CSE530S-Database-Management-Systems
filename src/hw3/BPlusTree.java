package hw3;


import hw1.Field;

public class BPlusTree {
    private Node root;
    private int pInner;
    private int pLeaf;
    public BPlusTree(int pInner, int pLeaf) {
    	this.pInner = pInner;
    	this.pLeaf = pLeaf;
    }
    
    public LeafNode search(Field f) {
    	return searchInNode(f, this.root);
    }
    
    public LeafNode searchInNode(Field f, Node n) {
    	
    	return null;
    }
    public void insert(Entry e) {
    	//your code here
    }
    
    public void delete(Entry e) {
    	//your code here
    }
    
    public Node getRoot() {
    	//your code here
    	return null;
    }
    


	
}
