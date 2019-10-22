package hw3;
/*
 * author:
 * search & insert @Shitao Chen
 */

import hw1.Field;
import hw1.RelationalOperator;

public class BPlusTree {
    private Node root;
    private int pInner;
    private int pLeaf;
    public BPlusTree(int pInner, int pLeaf) {
    	this.pInner = pInner;
    	this.pLeaf = pLeaf;
    }
    
    public LeafNode search(Field f) {
    	LeafNode ln = findLeafNode(f, root);
    	if(root == null) return null;
    	
    	for(Entry e: ln.getEntries()) 
			if(f.compare(RelationalOperator.EQ, e.getField()))
				return (LeafNode) ln;
		return null;  	
    }
    
    public LeafNode findLeafNode(Field f, Node n) {
    	if(n == null)
    		return null;
    	if(n.isLeafNode()) {
    		return (LeafNode) n;
    	}
    	InnerNode in = (InnerNode)n;
    	for(int i = 0; i < in.getKeys().size(); i++) 
    		if(f.compare(RelationalOperator.LTE, in.getKeys().get(i))) 
    			return findLeafNode(f, in.getChildren().get(i));
    	
    	return findLeafNode(f, in.getChildren().get(in.getChildren().size() - 1));
    }
    public void insert(Entry e) {
    	//find the leaf node to be inserted
    	LeafNode ln = findLeafNode(e.getField(), root);
    	if(ln == null) {
    		root = new LeafNode(pLeaf);
    		((LeafNode)root).getEntries().add(e);
    	}else {
    		ln.insertEntry(e);
    		if(ln.overDegree()) {
    			splitLeafNode(ln);
    		}
    	}
    }
    
    private void splitLeafNode(LeafNode n) {
    	InnerNode parentNode = n.getParent();
    	if(parentNode == null) {
    		parentNode = new InnerNode(pInner);
    	}
    	//create two new node
    	LeafNode n1 = new LeafNode(pLeaf, parentNode);
    	LeafNode n2 = new LeafNode(pLeaf, parentNode);
    	int barrier = n.getEntries().size() / 2;
		if(n.getEntries().size() % 2 == 0)
			barrier--;
    	for(int i = 0; i <= barrier ; i++) 
    		n1.getEntries().add(n.getEntries().get(i));
    	for(int i= barrier + 1; i < n.getEntries().size(); i++)
    		n2.getEntries().add(n.getEntries().get(i));
    	
    	//only root node exist in the tree, then new parent should have no children
    	if(parentNode.getChildren().isEmpty()) {
    		parentNode.getChildren().add(n1);
    		parentNode.getChildren().add(n2);
    		parentNode.getKeys().add((n1.getEntries().get(n1.getEntries().size() - 1 )).getField());
    		this.root = parentNode;
    		return;
    	}
    	int index = parentNode.getChildren().indexOf(n);
    	parentNode.getChildren().remove(index);
    	parentNode.getChildren().add(index, n1);
    	parentNode.getChildren().add(index + 1, n2);
    	parentNode.getKeys().add(index, (n1.getEntries().get(n1.getEntries().size() - 1 )).getField());
    	//parentNode.insertKeys((n1.getEntries().get(n1.getEntries().size() - 1 )).getField());
    	if(parentNode.overDegree())
    		splitParentNode(parentNode);
    	
    }
    private void splitParentNode(InnerNode n) {
    	InnerNode parentNode = n.getParent();
    	if(parentNode == null) {
    		parentNode = new InnerNode(pInner);
    	}
    	//create two new inner node
    	InnerNode n1 = new InnerNode(pInner, parentNode);
    	InnerNode n2 = new InnerNode(pInner, parentNode);
    	int middleIndex = n.getKeys().size() / 2;
    	if(n.getKeys().size() % 2 == 0)
    		middleIndex--;
    	Field middle = n.getKeys().get(middleIndex);
    	for(int i = 0; i < middleIndex; i++)
    		n1.getKeys().add(n.getKeys().get(i));
    	for(int i = 0; i <= middleIndex; i++) {
    		n1.getChildren().add(n.getChildren().get(i));
    		n1.getChildren().get(i).setParent(n1);
    	}
    	
    	for(int i = middleIndex + 1; i < n.getKeys().size(); i++)
    		n2.getKeys().add(n.getKeys().get(i));
    	for(int i = middleIndex + 1; i < n.getChildren().size(); i++) {
    		n2.getChildren().add(n.getChildren().get(i));
    		n2.getChildren().get(i - middleIndex - 1).setParent(n2);
    	}
    	if(parentNode.getChildren().isEmpty()) {
    		parentNode.getKeys().add(middle);
    		parentNode.getChildren().add(n1);
    		parentNode.getChildren().add(n2);
    		this.root = parentNode;
    		return;
    	}
    	int index = parentNode.getChildren().indexOf(n);
    	parentNode.getChildren().remove(index);
    	parentNode.getChildren().add(index, n1);
    	parentNode.getChildren().add(index + 1, n2);
    	parentNode.getKeys().add(index, middle);
    	if(parentNode.overDegree())
    		splitParentNode(parentNode);
    }
    public void delete(Entry e) {
    	//your code here
    }
    
    public Node getRoot() {
    	return this.root;
    }
    


	
}
