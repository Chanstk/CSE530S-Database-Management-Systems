package test;

import static org.junit.Assert.*;

import org.junit.Test;

import hw1.IntField;
import hw3.BPlusTree;
import hw3.Entry;
import hw3.InnerNode;

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
		bt.insert(new Entry(new IntField(1), 0));
		
	}

}
