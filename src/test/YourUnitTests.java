package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import hw1.Catalog;
import hw1.Database;
import hw1.HeapFile;
import hw1.HeapPage;
import hw1.TupleDesc;
import hw1.Type;
/*
 * @author Shitao Chen
 */
public class YourUnitTests {
	
	private HeapFile hf;
	private TupleDesc td;
	private Catalog c;
	private HeapPage hp;

	@Before
	public void setup() {
		
		try {
			Files.copy(new File("testfiles/test.dat.bak").toPath(), new File("testfiles/test.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("unable to copy files");
			e.printStackTrace();
		}
		
		c = Database.getCatalog();
		c.loadSchema("testfiles/test.txt");
		
		int tableId = c.getTableId("test");
		td = c.getTupleDesc(tableId);
		hf = c.getDbFile(tableId);
		hp = hf.readPage(0);
	}
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Test
	public void testNumFields_TupleDesc() {
		Type[] t = {Type.INT, Type.INT, Type.INT};
		String[] c = {"", "", ""};
		TupleDesc td = new TupleDesc(t, c);
		assertTrue(td.numFields() == 3);
	}
	
	@Test
	public void testGetFieldName_TupleDesc_HappyCase() {
		Type[] t = {Type.INT, Type.INT, Type.INT};
		String[] c = {"field1", "field2", "field3"};
		TupleDesc td = new TupleDesc(t, c);
		
		assertTrue(td.getFieldName(0) == c[0]);
	}
	
	@Test
	public void testGetFieldName_TupleDesc_Exception() {
		Type[] t = {Type.INT, Type.INT, Type.INT};
		String[] c = {"field1", "field2", "field3"};
		TupleDesc td = new TupleDesc(t, c);
		Boolean flag = false; 
		
		try {
			td.getFieldName(5);
		} catch (NoSuchElementException e) {
			flag = true;
		}
		
		assertTrue(flag);
	} 

}
