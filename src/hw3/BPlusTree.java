package hw3;


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
				return (LeafNode) n;
		return null;  	
    }
    
    public LeafNode findLeafNode(Field f, Node n) {
    	if(n == null)
    		return null;
    	if(n.isLeafNode()) {
    		return (LeafNode) n;
    	}
    	InnerNode in = (InnerNode)n;
    	for(int i = 0; i < in.getKeys().size(); i++) {
    		if(f.compare(RelationalOperator.LTE, in.getKeys().get(i))) 
    			return findLeafNode(f, in.getChildren().get(i));
    	}
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
    	for(int i = 0; i < n.getEntries().size(); i++) {
    		if(i <= barrier)
    			n1.getEntries().add(n.getEntries().get(i));
    		else
    			n2.getEntries().add(n.getEntries().get(i));
    	}
    	//only root node exist in the tree, then new parent should have no children
    	if(parentNode.getChildren().isEmpty()) {
    		parentNode.getChildren().add(n1);
    		parentNode.getChildren().add(n2);
    		parentNode.getKeys().add((n1.getEntries().get(n1.getEntries().size() - 1 )).getField());
    		if(n == root)
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
    	
    }
    public void delete(Entry e) {
    	//your code here
    }
    
    public Node getRoot() {
    	//your code here
    	return null;
    }
    


	
}
