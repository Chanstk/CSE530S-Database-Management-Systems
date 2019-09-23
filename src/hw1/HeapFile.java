package hw1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A heap file stores a collection of tuples. It is also responsible for managing pages.
 * It needs to be able to manage page creation as well as correctly manipulating pages
 * when tuples are added or deleted.
 * @author Sam Madden modified by Doug Shook, implemented by Junji Heng
 *
 */
public class HeapFile {
	
	public static final int PAGE_SIZE = 4096;
	private File file;
	private TupleDesc td;
	
	/**
	 * Creates a new heap file in the given location that can accept tuples of the given type
	 * @param f location of the heap file
	 * @param types type of tuples contained in the file
	 */
	public HeapFile(File f, TupleDesc type) {
		//your code here
		this.file = f;
		this.td = type;
		
	}
	
	public File getFile() {
		//your code here
		return this.file;
	}
	
	public TupleDesc getTupleDesc() {
		//your code here
		return this.td;
	}
	
	/**
	 * Creates a HeapPage object representing the page at the given page number.
	 * Because it will be necessary to arbitrarily move around the file, a RandomAccessFile object
	 * should be used here.
	 * @param id the page number to be retrieved
	 * @return a HeapPage at the given page number
	 */
	public HeapPage readPage(int id) {
		//your code here
		byte[] pagedata = new byte[PAGE_SIZE];
		try {
			int pos = id * PAGE_SIZE;
			RandomAccessFile raf = new RandomAccessFile(file,"r");
			raf.seek(pos);
			raf.read(pagedata);
			
			raf.close();
			return new HeapPage(id,pagedata,this.getId());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		
		return null;
	}
	
	/**
	 * Returns a unique id number for this heap file. Consider using
	 * the hash of the File itself.
	 * @return
	 */
	public int getId() {
		//your code here
		return file.hashCode();
	}
	
	/**
	 * Writes the given HeapPage to disk. Because of the need to seek through the file,
	 * a RandomAccessFile object should be used in this method.
	 * @param p the page to write to disk
	 */
	public void writePage(HeapPage p) {
		//your code here
		byte[] pagedata = p.getPageData();
		try {
			int pos = p.getId() * PAGE_SIZE;
			RandomAccessFile raf = new RandomAccessFile(file,"rw");
			raf.seek(pos);
			raf.write(pagedata);
			
			//close file
			raf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		

	}
	
	/**
	 * Adds a tuple. This method must first find a page with an open slot, creating a new page
	 * if all others are full. It then passes the tuple to this page to be stored. It then writes
	 * the page to disk (see writePage)
	 * @param t The tuple to be stored
	 * @return The HeapPage that contains the tuple
	 */
	public HeapPage addTuple(Tuple t) {
		//your code here
		int numOfPages = getNumPages();
		for(int i=0;i<numOfPages;i++) {
			HeapPage hp = readPage(i);
			try {
				hp.addTuple(t);
				this.writePage(hp);
				return hp;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		
		//page is full
		try {
			byte[] pagedata = new byte[PAGE_SIZE];
			HeapPage nhp = new HeapPage(getNumPages(),pagedata,getId());
			nhp.addTuple(t);
			this.writePage(nhp);
			return nhp;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
		return null;
	}
	
	/**
	 * This method will examine the tuple to find out where it is stored, then delete it
	 * from the proper HeapPage. It then writes the modified page to disk.
	 * @param t the Tuple to be deleted
	 */
	public void deleteTuple(Tuple t){
		//your code here
		int i = t.getPid();
		HeapPage hp = readPage(i);
		hp.deleteTuple(t);	
		this.writePage(hp);
		
	}
	
	/**
	 * Returns an ArrayList containing all of the tuples in this HeapFile. It must
	 * access each HeapPage to do this (see iterator() in HeapPage)
	 * @return
	 */
	public ArrayList<Tuple> getAllTuples() {
		//your code here
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		int num = this.getNumPages();
		for(int i=0;i<num;i++) {
			HeapPage temp = readPage(i);
			Iterator<Tuple> iterator = temp.iterator();
			while(iterator.hasNext()) {
				tuples.add(iterator.next());
			}
		}
		
		return tuples;
	}
	
	/**
	 * Computes and returns the total number of pages contained in this HeapFile
	 * @return the number of pages
	 */
	public int getNumPages() {
		//your code here
		
		int num = (int) (file.length()/PAGE_SIZE);
		return num;
	}
}
