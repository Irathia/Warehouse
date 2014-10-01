package Warehouse;

import java.sql.Time;
import java.util.Vector;

public class Task {
	
	private Vector <OrderItem> items;
	private int finish; 
	private Time executionTime;
	
	Task(){
		this.executionTime = null;
		items = null;
	};
	
	public final Time getExecutionTime(){
		return executionTime;
	};
	
	public final int getFinish(){
		return finish;
	};
	
	public final OrderItem getItem(int index){
		return items.get(index);
	};
	
	public void addItem(OrderItem value){
		items.add(value);
	};
	
	public void clearAll(){
		items.clear();
	};
	
	public void setFinish(int finish){
		this.finish = finish;
	}
	
	public void calculateExecutionTime(){
		//how?
	}
}