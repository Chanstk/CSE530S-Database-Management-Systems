package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import hw1.Field;
import hw1.IntField;
import hw1.RelationalOperator;
import hw3.BPlusTree;
import hw3.Entry;
import hw3.InnerNode;
import hw3.LeafNode;
import hw3.Node;

public class YourHW3Tests {

	@Test
	public void testOneRootNode() {
		BPlusTree bt = new BPlusTree(3, 2);
		bt.insert(new Entry(new IntField(9), 0));
		assertTrue(bt.getRoot().isLeafNode() == true);
		assertTrue(((InnerNode)bt.getRoot()).getChildren().size() == 0);
	}
	
	@Test
	public void testMoreDegree() {
		BPlusTree bt = new BPlusTree(4, 3);
		bt.insert(new Entry(new IntField(1), 9));
		bt.insert(new Entry(new IntField(1), 4));
		bt.insert(new Entry(new IntField(1), 2));
		//verify root properties
		Node root = bt.getRoot();
		assertTrue(root.isLeafNode() == true);
		
		bt.insert(new Entry(new IntField(1), 7));
		InnerNode in = (InnerNode)bt.getRoot();
		
		assertTrue(in.isLeafNode() == false);
		assertTrue(in.getChildren().size() == 2);
		
		//grab left and right children from root
		LeafNode l = (LeafNode)in.getChildren().get(0);
		LeafNode r = (LeafNode)in.getChildren().get(0);
		assertTrue(l.isLeafNode() == true);
		assertTrue(r.isLeafNode() == true);
		assertTrue(l.getEntries().get(0).getField().compare(RelationalOperator.EQ, new IntField(4)));
		assertTrue(r.getEntries().get(0).getField().compare(RelationalOperator.EQ, new IntField(9)));
	}

}
