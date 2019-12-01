package hw4;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import hw1.Database;
import hw1.HeapFile;
import hw1.HeapPage;
import hw1.IntField;
import hw1.Tuple;
import hw4.Permissions;
/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool which check that the transaction has the appropriate
 * locks to read/write the page.
 */
public class BufferPool {
	class Lock {
		int pageId;
		int transId;
		int tableId;
		Permissions permisson;
		public Lock(int pageId, int transId, int tableId, Permissions p) {
			this.pageId = pageId;
			this.transId = transId;
			this.tableId = tableId;
			this.permisson = p;
		}
	}
	
	class Pair {
	    final int a;
	    final int b;
	    public Pair(int a, int b) {
	    	this.a = a;
	    	this.b = b;
	    }
	    
	    @Override
	    public boolean equals(Object obj) {
	    	return ((Pair)obj).a == this.a && ((Pair)obj).b == this.b;
	    }
	    @Override
	    public int hashCode() {
	    	
	    	return a* 11 + b; 
	    }
	}
    /** Bytes per page, including header. */
    public static final int PAGE_SIZE = 4096;

    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;
    private Map<HeapPage, Boolean> dirtyIndicator;//indicate whether a heappage is dirty
    private Map<Pair, HeapPage> cache;
    private Map<Integer, List<Lock>> transLock;
    private Map<Pair, List<Lock>> pageReadLocks;
    private Map<Pair, List<Lock>> pageWriteLocks;
    private int maxPage;
    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
        this.dirtyIndicator = new HashMap<>();
        this.cache = new HashMap<>();
        this.transLock = new HashMap<>();
        this.pageReadLocks = new HashMap<>();
        this.pageWriteLocks = new HashMap<>();
        this.maxPage = numPages;
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, an page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param tableId the ID of the table with the requested page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public HeapPage getPage(int tid, int tableId, int pid, Permissions perm)
        throws Exception {
        HeapPage hp = Database.getCatalog().getDbFile(tableId).readPage(pid);
        Pair tpid = new Pair(tableId, pid);
        pageWriteLocks.putIfAbsent(tpid, new ArrayList<>());
        pageReadLocks.putIfAbsent(tpid, new ArrayList<>());
        transLock.putIfAbsent(tid, new ArrayList<>());
        if(perm.toString().equals("READ_WRITE")){
        	for(Lock lock : pageReadLocks.get(tpid)) 
        		if(tid != lock.transId && lock.pageId == pid && lock.tableId == tableId) {
        			transactionComplete(tid, false);
        			return hp;
        		}
        	for(Lock lock : pageWriteLocks.get(tpid)) 
        		if(tid != lock.transId && lock.pageId == pid && lock.tableId == tableId) {
        			transactionComplete(tid, false);
        			return hp;
        		}
        }else {
        	for(Lock lock : pageWriteLocks.get(tpid)) 
        		if(tid != lock.transId && lock.pageId == pid && lock.tableId == tableId) {
        			transactionComplete(tid, false);
        			return hp;
        		}
        }
        //acquire lock
        Lock lock = new Lock(pid, tid, tableId, perm);
        transLock.putIfAbsent(tid, new ArrayList<>());
        upgradeOrAddLoack(transLock.get(tid), lock);
        if(perm.toString().equals("READ_ONLY")) 
        	{
        		upgradeOrAddLoack(pageReadLocks.get(tpid), lock);
        		pageWriteLocks.remove(tpid);
        	}
        else 
        	upgradeOrAddLoack(pageWriteLocks.get(tpid), lock);
        
        if(cache.containsKey(tpid))
        	return hp;
        if(cache.size() >= this.maxPage)
        	this.evictPage();
        cache.put(tpid, hp);
        //this.maxPage++;
        return hp;
    }
    
    private void upgradeOrAddLoack(List<Lock> src, Lock target){
    	for(int i = 0; i < src.size(); i++)
    		if(src.get(i).pageId == target.pageId && src.get(i).tableId == target.tableId) {
    			src.set(i, target);
    			return;
    		}
    	src.add(target);
    }
    
    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param tableID the ID of the table containing the page to unlock
     * @param pid the ID of the page to unlock
     */
    public  void releasePage(int tid, int tableId, int pid) {
    	Pair tpid = new Pair(tableId, pid);
    	List<Lock> locks = transLock.get(tid);
    	List<Lock> filtredLocks = locks
    			.stream()
    			.filter(e -> e.pageId != pid && e.tableId != tableId)
    			.collect(Collectors.toList());
    	transLock.put(tid, filtredLocks);
    	pageReadLocks.putIfAbsent(tpid, new ArrayList<>());
    	pageWriteLocks.putIfAbsent(tpid, new ArrayList<>());
    	pageReadLocks.get(tpid).clear();
    	pageWriteLocks.get(tpid).clear();
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public boolean holdsLock(int tid, int tableId, int pid) {
    	List<Lock> locks = transLock.getOrDefault(tid, new ArrayList<>());
    	if(locks.size() == 0)
    		return false;
    	for(Lock lock : locks)
    		if(lock.pageId == pid && lock.tableId == tableId)
    			return true;
    	return false;
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction. If the transaction wishes to commit, write
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public void transactionComplete(int tid, boolean commit)
        throws IOException {
        for(Lock lock: transLock.get(tid)) {
        	releasePage(tid, lock.tableId, lock.pageId);
        	if(commit)
        		flushPage(lock.tableId, lock.pageId);
        }
        transLock.get(tid).clear();
    }

    /**
     * Add a tuple to the specified table behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to. May block if the lock cannot 
     * be acquired.
     * 
     * Marks any pages that were dirtied by the operation as dirty
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public  void insertTuple(int tid, int tableId, Tuple t)
        throws Exception {
        // your code here
    	int pid = t.getPid();
    	HeapFile hf = Database.getCatalog().getDbFile(tableId);
    	Pair tpid = new Pair(tableId, pid);
    	HeapPage hp = hf.readPage(pid);
    	if(this.pageWriteLocks.containsKey(tpid)==false) {
    		throw new Exception();
    	}
    	hf.addTuple(t);
    	dirtyIndicator.putIfAbsent(hp, true);
    	
    	
    	
    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from. May block if
     * the lock cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty.
     *
     * @param tid the transaction adding the tuple.
     * @param tableId the ID of the table that contains the tuple to be deleted
     * @param t the tuple to add
     */
    public  void deleteTuple(int tid, int tableId, Tuple t)
        throws Exception {
    	int pid = t.getPid();
    	HeapFile hf = Database.getCatalog().getDbFile(tableId);
    	
    	HeapPage hp = hf.readPage(pid);
    	hf.deleteTuple(t);
    	dirtyIndicator.putIfAbsent(hp, true);

    	

    	
    }

    private synchronized  void flushPage(int tableId, int pid) throws IOException {
    	HeapFile hf = Database.getCatalog().getDbFile(tableId);
    	hf.writePage(cache.get(new Pair(tableId, pid)));
    }

    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private synchronized  void evictPage() throws Exception {
    	Iterator<Pair> iter = this.cache.keySet().iterator();
    	Pair pair = iter.next();
    	HeapPage hp= Database.getCatalog().getDbFile(pair.a).readPage(pair.b);
    	while(iter.hasNext()&& dirtyIndicator.containsKey(hp)) {
    		pair = iter.next();
    		hp= Database.getCatalog().getDbFile(pair.a).readPage(pair.b);
    	}
    	// There are dirty pages can't flush
    	if(!iter.hasNext() || dirtyIndicator.isEmpty() == false) {
    		throw new Exception();
    	}
    	flushPage(pair.a,pair.b);
    	dirtyIndicator.remove(hp);
    	this.cache.remove(pair);


    	
    }

}
