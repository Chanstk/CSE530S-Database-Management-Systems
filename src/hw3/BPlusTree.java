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
    	
    	LeafNode ln = findLeafNode(e.getField(), root);
    	if(ln == root) {
    		ln.deleteEntry(e);
    		if(ln.getEntries().size()==0) {
    			root =null;
    		}
    		return;
    	}
    
    	if (ln == null) {
    		return;
    	}else {
    		//int index= ln.getParent().getChildren().indexOf(ln);
    		ln.deleteEntry(e);
    		if(ln.ToMerge()) {
    			if(ln.couldBorrow()) {
	    			borrowLeafNode(ln,e);
	    			return;
    			}else {
    				//System.out.println("123");
    				mergeLeafNode(ln);
 	
    			}

    		}
    		
    		
    	}
    	
    	
    }
    private void borrowLeafNode(LeafNode n, Entry e) {
    	InnerNode parentNode = n.getParent();
    	LeafNode leftn = n.getLeftNeighbor();
    	
    	int index = leftn.getEntries().size()-1;
    	Entry e1 = leftn.getEntries().get(index);
    	leftn.getEntries().remove(e1);
    	n.getEntries().add(0,e1);
    	
    	Entry e2 = leftn.getEntries().get(index-1);
    	parentNode.UpdateKeys(e1.getField(), e2.getField());
    	parentNode.UpdateKeys(e.getField(), e1.getField());
    	
    	return;
    	
    }
    private void mergeLeafNode(LeafNode n) {
    	InnerNode parentNode = n.getParent();
    	LeafNode n1 = new LeafNode(pLeaf, parentNode); 
    	if(parentNode.getKeys().size()>parentNode.getDegree()/2 || parentNode.parent == null) {
    		
    		if(parentNode.getKeys().size()==1) {
    			LeafNode lf = new LeafNode(pLeaf, null);
    			for(int i=0;i<parentNode.getChildren().size();i++) {
    				LeafNode temp = (LeafNode)parentNode.getChildren().get(i);
    				for(int j=0;j<temp.getEntries().size();j++) {
    					lf.getEntries().add(temp.getEntries().get(j));
    				}
    			}
    			root = lf;
			}else {
	        	for(int index=0;index<parentNode.getChildren().size();index++) {
	        		LeafNode lf = (LeafNode)parentNode.getChildren().get(index);
	        		int low = lf.getDegree()/2;
	        		if (lf.getDegree() % 2 != 0) {
	        			low++;
	        		}
	        		if(lf.getEntries().size() < low) {
	        			n1 = (LeafNode) parentNode.getChildren().get(index);
	        			if(n1.getLeftNeighbor()!=null) {
	        				
	        				//merge from leftNeighbor
		        			LeafNode n2 = n1.getLeftNeighbor();
		        			for(int i =0;i<n1.getEntries().size();i++) {
		        				n2.getEntries().add(n1.getEntries().get(i));
		        			}
	        			}else {
	        				
	        				// merge from rightNegighbor
	        				LeafNode n2 = n1.getRightNeighbor();
	        				for(int i =0;i<n1.getEntries().size();i++) {
		        				n2.getEntries().add(0,n1.getEntries().get(i));
		        			}
	        			}

	        			parentNode.getChildren().remove(index);
	        			n.getParent().getKeys().remove(index);
	        		}
	        	}
			}

    	}else {

    		// merge parentNodes
    		if(parentNode.couldBorrow()) {
    			//from left
    			if(n.getParent().getLeftNeighbor()!=null) {
    				InnerNode leftPNode = n.getParent().getLeftNeighbor();
    				int indexL= leftPNode.getKeys().size()-1;
        			
        			Field f2 = leftPNode.getKeys().get(indexL); 
        			LeafNode lf = (LeafNode)leftPNode.getChildren().get(indexL+1);
        			
        			leftPNode.getChildren().remove(indexL+1);
        			leftPNode.getKeys().remove(indexL);
        			
        			Field f = null;
        			for(int i=0;i<parentNode.parent.getKeys().size();i++) {
        				if(f2.compare(RelationalOperator.LT, parentNode.parent.getKeys().get(i))) {
        					f = parentNode.parent.getKeys().get(i);
        					break;
        				}
        			}
        			parentNode.parent.UpdateKeys(f, f2);
        			parentNode.UpdateKeys(parentNode.getKeys().get(0), f);
        			n.getEntries().add(n.getLeftNeighbor().getEntries().get(0));
        			
        			n.getLeftNeighbor().getEntries().remove(0);
        			for(int i=0;i<lf.getEntries().size();i++) {
        				n.getLeftNeighbor().getEntries().add(lf.getEntries().get(i));
        			}
    			}else {
    				//from right
    				InnerNode rightPNode = n.getParent().getRightNeighbor();
    				int indexR= 0;
        			
        			Field f2 = rightPNode.getKeys().get(indexR); 
        			LeafNode lf = (LeafNode)rightPNode.getChildren().get(indexR);
        			
        			rightPNode.getChildren().remove(indexR);
        			rightPNode.getKeys().remove(indexR);
        			
        			Field f = null;
        			for(int i=0;i<parentNode.parent.getKeys().size();i++) {
        				if(f2.compare(RelationalOperator.GT, parentNode.parent.getKeys().get(i))) {
        					f = parentNode.parent.getKeys().get(i);
        					break;
        				}
        			}
        			parentNode.parent.UpdateKeys(f, f2);
        			parentNode.UpdateKeys(parentNode.getKeys().get(0), f);
        			n.getEntries().add(n.getRightNeighbor().getEntries().get(0));
        			
        			n.getLeftNeighbor().getEntries().remove(0);
        			for(int i=0;i<lf.getEntries().size();i++) {
        				n.getRightNeighbor().getEntries().add(lf.getEntries().get(i));
        			}
    			}
    			
    			
    			
    			
    		}
    		else {
    			//delete one level of innernodes
    			InnerNode nParent = new InnerNode(pInner, parentNode);
    			InnerNode inl = parentNode.getLeftNeighbor();
    			InnerNode inr = parentNode.getRightNeighbor();
    			Field f1 = parentNode.getKeys().get(0);
    			Field f2 = parentNode.parent.getKeys().get(parentNode.parent.getKeys().size()-1);
    			if(inl!=null) {
        			for(int i=0;i<inl.getKeys().size();i++) {
        				nParent.getKeys().add(inl.getKeys().get(i));
        				
        			}
        			for(int j=0; j<inl.getChildren().size();j++) {
        				nParent.getChildren().add((LeafNode)inl.getChildren().get(j));
        			}
    			}
    			
    			if(inr!=null) {
        			for(int i=0;i<inr.getKeys().size();i++) {
        				nParent.getKeys().add(inr.getKeys().get(i));
        				
        			}
        			for(int j=0; j<inr.getChildren().size();j++) {
        				nParent.getChildren().add((LeafNode)inr.getChildren().get(j));
        			}
    			}

    			
    			for(int i=0;i<parentNode.getKeys().size();i++) {
    				nParent.getKeys().add(parentNode.getKeys().get(i));
    				//nParent.getChildren().add(parentNode.getChildren().get(i));
    			}
    			for(int j=0; j<parentNode.getChildren().size();j++) {
    				LeafNode temp = (LeafNode)parentNode.getChildren().get(j);
    				if(temp.getEntries().size()>=temp.getDegree()/2) {
    					nParent.getChildren().add(temp);
    				}
    				
    			}
    			nParent.UpdateKeys(f1, f2);
    			if(parentNode.parent.parent == null) {
    				root = nParent;
    			}else {
    				nParent.setParent(parentNode.parent.parent);
    			}
    			
    			
    			
    			
    		}
    		return;
    	}

    	
    	
    	
    }
 
    
    public Node getRoot() {
    	return this.root;
    }
    


	
}
