package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import hw1.Catalog;
import hw1.Database;
import hw1.HeapFile;
import hw1.HeapPage;
import hw1.IntField;
import hw1.StringField;
import hw1.Tuple;
import hw1.TupleDesc;
import hw4.BufferPool;
import hw4.Permissions;

public class YourHW4Tests {

	private Catalog c;
	private BufferPool bp;
	private HeapFile hf;
	private TupleDesc td;
	private int tid;
	private int tid2;
	
	@Before
	public void setup() {
		
		try {
			Files.copy(new File("testfiles/test.dat.bak").toPath(), new File("testfiles/test.dat").toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("unable to copy files");
			e.printStackTrace();
		}
		
		Database.reset();
		c = Database.getCatalog();
		c.loadSchema("testfiles/test.txt");
		c.loadSchema("testfiles/test2.txt");
		
		int tableId = c.getTableId("test");
		td = c.getTupleDesc(tableId);
		hf = c.getDbFile(tableId);
		
		Database.resetBufferPool(BufferPool.DEFAULT_PAGES);

		bp = Database.getBufferPool();
		
		
		tid = c.getTableId("test");
		tid2 = c.getTableId("test2");
	}

	@Test
	public void testLockUpgrade2() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_WRITE);
		bp.getPage(0, tid, 0, Permissions.READ_ONLY);
		if(!bp.holdsLock(0, tid, 0)) {
			fail("Should be able to upgrade locks");
		}
		assertTrue(true);
	}
	
	@Test
	public void testFailedLockUpgrade() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_ONLY);
		bp.getPage(1, tid, 0, Permissions.READ_ONLY);
		bp.getPage(0, tid, 0, Permissions.READ_WRITE);
		if(!bp.holdsLock(0, tid, 0) && !bp.holdsLock(1, tid, 0)) {
			fail("Lock upgrade should have failed");
		}
		if(bp.holdsLock(0, tid, 0) && bp.holdsLock(1, tid, 0)) {
			fail("Lock upgrade should have failed");
		}
		assertTrue(true);
	}
	
	@Test
	public void testWriteLocks() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_WRITE);
		try {
		bp.getPage(1, tid, 0, Permissions.READ_WRITE);
		} catch(Exception e) {
			
		}
		if(!bp.holdsLock(0, tid, 0) && !bp.holdsLock(1, tid, 0)) {
			fail("Deadlock - should not grant both locks");
		}
		
		if(bp.holdsLock(1, tid, 0)&& bp.holdsLock(0, tid, 0)) {
			fail("Deadlock - one transaction should survive");
		}
		assertTrue(true);
	}
	
	@Test
	public void testReadThenWrite() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_ONLY);
		try {
		bp.getPage(1, tid, 0, Permissions.READ_WRITE);
		} catch(Exception e) {
			
		}
		if(!bp.holdsLock(0, tid, 0) && !bp.holdsLock(1, tid, 0)) {
			fail("Deadlock - should not grant both locks");
		}
		
		if(bp.holdsLock(1, tid, 0)&& bp.holdsLock(0, tid, 0)) {
			fail("Deadlock - one transaction should survive");
		}
		assertTrue(true);
	}
	
	@Test
	public void testWriteThenRead() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_WRITE);
		try {
		bp.getPage(1, tid, 0, Permissions.READ_ONLY);
		} catch(Exception e) {
			
		}
		if(!bp.holdsLock(0, tid, 0) && !bp.holdsLock(1, tid, 0)) {
			fail("Deadlock - should not grant both locks");
		}
		
		if(bp.holdsLock(1, tid, 0)&& bp.holdsLock(0, tid, 0)) {
			fail("Deadlock - one transaction should survive");
		}
		assertTrue(true);
	}
	
	@Test
	public void testCommit() throws Exception {
		Tuple t = new Tuple(td);
		t.setField(0, new IntField(new byte[] {0, 0, 0, (byte)131}));
		byte[] s = new byte[129];
		s[0] = 2;
		s[1] = 98;
		s[2] = 121;
		t.setField(1, new StringField(s));
		
		bp.getPage(0, tid, 0, Permissions.READ_WRITE); //acquire lock for the page
		bp.insertTuple(0, tid, t); //insert the tuple into the page
		bp.transactionComplete(0, true); //should flush the modified page
		
		//reset the buffer pool, get the page again, make sure data is there
		Database.resetBufferPool(BufferPool.DEFAULT_PAGES);
		HeapPage hp = bp.getPage(1, tid, 0, Permissions.READ_ONLY);
		Iterator<Tuple> it = hp.iterator();
		assertTrue(it.hasNext());
		it.next();
		assertTrue(it.hasNext());
		it.next();
		assertFalse(it.hasNext());
	}
	
	@Test
	public void testAbort() throws Exception {
		Tuple t = new Tuple(td);
		t.setField(0, new IntField(new byte[] {0, 0, 0, (byte)131}));
		byte[] s = new byte[129];
		s[0] = 2;
		s[1] = 98;
		s[2] = 121;
		t.setField(1, new StringField(s));
		
		bp.getPage(0, tid, 0, Permissions.READ_WRITE); //acquire lock for the page
		bp.insertTuple(0, tid, t); //insert the tuple into the page
		bp.transactionComplete(0, false); //should abort, discard changes
		
		//reset the buffer pool, get the page again, make sure data is there
		Database.resetBufferPool(BufferPool.DEFAULT_PAGES);
		HeapPage hp = bp.getPage(1, tid, 0, Permissions.READ_ONLY);
		Iterator<Tuple> it = hp.iterator();
		assertTrue(it.hasNext());
		it.next();
		assertFalse(it.hasNext());
	}
	
	@Test
	public void testRelease() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_ONLY);
	    bp.releasePage(0, tid, 0);

	    //lock has been released so this should work
	    bp.getPage(1, tid, 0, Permissions.READ_WRITE);
	    assertTrue(true);
	}
	
	@Test
	public void testRelease2() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_WRITE);
	    bp.releasePage(0, tid, 0);

	    //lock has been released so this should work
	    bp.getPage(1, tid, 0, Permissions.READ_WRITE);
	    assertTrue(true);
	}
	
	@Test
	public void testDuplicateReads() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_ONLY);
		bp.getPage(0, tid, 0, Permissions.READ_ONLY);
		
		//should be ok since it already has the lock
		assertTrue("should hold read lock", bp.holdsLock(0, tid, 0));
	}
	
	@Test
	public void testDuplicateWrites() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_WRITE);
		bp.getPage(0, tid, 0, Permissions.READ_WRITE);
		
		//should be ok since it already has the lock
		assertTrue("should hold write lock", bp.holdsLock(0, tid, 0));
	}
	
	@Test
	public void testhfRemove() throws Exception {

		bp.getPage(0, tid, 0, Permissions.READ_WRITE);
		Tuple t = new Tuple(td);
		t.setField(0, new IntField(new byte[] {0, 0, 0, (byte)131}));
		byte[] s = new byte[129];
		s[0] = 2;
		s[1] = 98;
		s[2] = 121;
		t.setField(1, new StringField(s));
		bp.deleteTuple(0, tid, t);
		
		bp.transactionComplete(0, true);
		
		Database.resetBufferPool(BufferPool.DEFAULT_PAGES);
		HeapPage hp = bp.getPage(1, tid, 0, Permissions.READ_ONLY);
		Iterator<Tuple> it = hp.iterator();
		assertFalse("Deletion failed", it.hasNext());


	}
	
	@Test 
	public void testHoldLocks() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_ONLY);
		if(!bp.holdsLock(0, tid, 0)) {
			fail("Should be able to hold read locks");
		}
	    bp.transactionComplete(0, true);
	    if(bp.holdsLock(0, tid, 0)) {
			fail("Shouldn't be able to hold locks after completing transactions");
		}
		
	}
	
	@Test 
	public void testMultiPleWriteLocks() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_WRITE);
		try {
		bp.getPage(1, tid, 0, Permissions.READ_WRITE);
		} catch(Exception e) {
			
		}
		if(!bp.holdsLock(0, tid, 0)) {
			fail("T1 should hold the lock");
		}
		if(bp.holdsLock(1, tid, 0)) {
			fail("T2 should not hold the lock");
		}
		assertTrue(true);
		
	}
	
	@Test 
	public void testMultiPleReadLocks() throws Exception {
		bp.getPage(0, tid, 0, Permissions.READ_ONLY);
		try {
		bp.getPage(1, tid, 0, Permissions.READ_ONLY);
		} catch(Exception e) {
			
		}
		if(!bp.holdsLock(0, tid, 0)) {
			fail("T1 should hold the lock");
		}
		if(!bp.holdsLock(1, tid, 0)) {
			fail("T2 should hold the lock");
		}
		assertTrue(true);
		
	}

}
