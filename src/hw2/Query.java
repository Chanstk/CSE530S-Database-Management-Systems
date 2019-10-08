package hw2;

import java.util.ArrayList;
import java.util.List;

import hw1.Catalog;
import hw1.Database;
import hw1.Tuple;
import hw1.TupleDesc;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * 
 * @author Junji Heng
 *
 */
public class Query {

	private String q;
	
	public Query(String q) {
		this.q = q;
	}
	
	public Relation execute()  {
		Statement statement = null;
		try {
			statement = CCJSqlParserUtil.parse(q);
		} catch (JSQLParserException e) {
			System.out.println("Unable to parse query");
			e.printStackTrace();
		}
		Select selectStatement = (Select) statement;
		PlainSelect sb = (PlainSelect)selectStatement.getSelectBody();
		
		List<SelectItem> selectitems = sb.getSelectItems();
		FromItem fromitem = sb.getFromItem();
		List<Join> joins = sb.getJoins();
		Catalog cata = Database.getCatalog();
		
		int tableid = cata.getTableId(fromitem.toString());
		ArrayList<Tuple> tuples = cata.getDbFile(tableid).getAllTuples();
		TupleDesc td = cata.getTupleDesc(tableid);
		Relation ar = new Relation(tuples, td);
		
		//1.Join
		
		if(joins != null) {
			for(Join join : joins) {
				FromItem rtiem = join.getRightItem();
				int rtableid = cata.getTableId(rtiem.toString());
				ArrayList<Tuple> rtuples = cata.getDbFile(rtableid).getAllTuples();
				TupleDesc rtd = cata.getTupleDesc(rtableid);
				Relation rr = new Relation(rtuples,rtd);
				
				
				String str[] = join.getOnExpression().toString().split(" = ");
				String strl[] = str[0].split("\\.");
				String strr[] = str[1].split("\\.");
				int left=-1;
				int right=-1;		
				for (int i =0;i<ar.getDesc().numFields();i++) {
					if(ar.getDesc().getFieldName(i).equalsIgnoreCase(strl[1])) {
						left=i;
					}
					if(ar.getDesc().getFieldName(i).equalsIgnoreCase(strr[1])) {
						left=i;
					}	
				}
				
				for (int i =0;i<rtd.numFields();i++) {
					if(rtd.getFieldName(i).equalsIgnoreCase(strl[1])) {
						right=i;
					}
					if(rtd.getFieldName(i).equalsIgnoreCase(strr[1])) {
						right=i;
					}	
				}
				
				
				ar= ar.join(rr, left, right );	
				
			}

		}
		
		
		
		//2.Where
		WhereExpressionVisitor wv = new WhereExpressionVisitor();
		Expression w = sb.getWhere();
		if(w != null) {
			
			w.accept(wv);
			
			ar=ar.select(td.nameToId(wv.getLeft()),wv.getOp(),wv.getRight());
		}
		
		
		
		
		// 3.Project
		ArrayList<Integer> columnIds = new ArrayList<>();
		
		//not select all
		if(!selectitems.get(0).toString().equals("*")) {
			
			
			if(selectitems.get(0).toString().equals("COUNT(*)")){
				columnIds.add(0);
				ar=ar.project(columnIds);
				ar=ar.aggregate(AggregateOperator.COUNT, false);
				return ar;
			}else {			
				for(SelectItem selectitem : selectitems) {
					
					ColumnVisitor cv = new ColumnVisitor();
					selectitem.accept(cv);	
					
					for(int i=0;i<ar.getDesc().numFields();i++) {
						 if(ar.getDesc().getFieldName(i).equals(cv.getColumn())) {
							 columnIds.add(i);
						 }
					 }
				}
				ar = ar.project(columnIds);
				
				for(SelectItem selectitem : selectitems) {
					ColumnVisitor cv = new ColumnVisitor();
					selectitem.accept(cv);
					
					List<Expression> group = sb.getGroupByColumnReferences();
					if(!cv.isAggregate()) {
					}
					else {				
						if(group!=null) {
							ar=ar.aggregate(cv.getOp(), true);
						}
						else {
							ar=ar.aggregate(cv.getOp(), false);
						}
					}
				}
			}	
		}


		
		return ar;
		

	}
}
