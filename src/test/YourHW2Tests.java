package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import hw1.Catalog;
import hw1.Database;
import hw1.HeapFile;
import hw1.IntField;
import hw1.StringField;
import hw1.Tuple;
import hw1.TupleDesc;
import hw2.AggregateOperator;
import hw2.Query;
import hw2.Relation;

public class YourHW2Tests {

	private HeapFile testhf;
	private TupleDesc testtd;
	private HeapFile ahf;
	private TupleDesc atd;
	private HeapFile bhf;
	private TupleDesc btd;
	private Catalog c;

	@Before
	public void setup() {
		
		try {
			Files.copy(new File("testfiles/test.dat.bak").toPath(), new File("testfiles/test.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
			Files.copy(new File("testfiles/A.dat.bak").toPath(), new File("testfiles/A.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
			Files.copy(new File("testfiles/B.dat.bak").toPath(), new File("testfiles/B.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("unable to copy files");
			e.printStackTrace();
		}
		
		c = Database.getCatalog();
		c.loadSchema("testfiles/test.txt");
		
		int tableId = c.getTableId("test");
		testtd = c.getTupleDesc(tableId);
		testhf = c.getDbFile(tableId);
		
		c = Database.getCatalog();
		c.loadSchema("testfiles/A.txt");
		
		tableId = c.getTableId("A");
		atd = c.getTupleDesc(tableId);
		ahf = c.getDbFile(tableId);
		
		c = Database.getCatalog();
		c.loadSchema("testfiles/B.txt");
		
		tableId = c.getTableId("B");
		btd = c.getTupleDesc(tableId);
		bhf = c.getDbFile(tableId);
	}
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testAggregateMax() {
		Relation ar = new Relation(ahf.getAllTuples(), atd);
		ArrayList<Integer> c = new ArrayList<Integer>();
		c.add(1);
		ar = ar.project(c);
		ar = ar.aggregate(AggregateOperator.MAX, false);
		
		assertTrue("The result of an aggregate should be a single tuple", ar.getTuples().size() == 1);
		IntField agg = (IntField) ar.getTuples().get(0).getField(0);
		assertTrue("The max of these values was incorrect", agg.getValue() == 8);
	}
	
	@Test
	public void testMultiJoin() throws Exception {

		Query q = new Query("SELECT c1, c2, a1, a2, b1, b2 FROM test JOIN A ON test.c1 = a.a1 JOIN B on a.a2 = b.b2");
		Relation r = q.execute();
		
		assertTrue("MultiJoin should return 5 tuples", r.getTuples().size() == 5);
		assertTrue("Tuple size should increase since columns were added with two joins", r.getDesc().getSize() == 149);


	}
	
	@Test
	public void testRightJoin() throws Exception {

		Query q = new Query("SELECT c1, c2, a1, a2 FROM test RIGHT JOIN A ON test.c1 = a.a1");
		Relation r = q.execute();
		
		assertTrue("RightJoin should return 8 tuples", r.getTuples().size() == 8);
		assertTrue("Tuple size should increase since columns were added with Right join", r.getDesc().getSize() == 141);

	}
	
	

}
