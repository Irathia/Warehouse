package Warehouse;

import java.sql.Time;
import java.util.Vector;

public class Task {
	
	private Vector <OrderItem> items;
	private int finish, start; 
	private Time executionTime;
	private Time deadline;
	public final double v = 0.89; // 3.2 km/h
	
	Task(Time deadline){
		this.deadline = new Time(deadline.getTime());
		this.executionTime = new Time(0);
		items = new Vector<OrderItem> ();
	};
	
	public final Time getExecutionTime(){
		return executionTime;
	};
	
	public final int getFinish(){
		return finish;
	};
	
	public final int getStart(){
		return start;
	}
	
	public final Time getDeadline(){
		return deadline;
	}
	
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
	
	public void setStart(){
		start = Warehouse.getInstance().getNearestEmptyContainer(items.get(0).getIndex());
	}
	
	public void setDeadline(Time deadline){
		this.deadline = deadline;
	}
	
	public void calculateExecutionTime(){
		//without any stops
		double l = Warehouse.getInstance().getRealDistance(start,items.get(0).getIndex());;
		for (int i = 0; i < items.size()-1; i++){
			l += Warehouse.getInstance().getRealDistance(items.get(i).getIndex(),items.get(i+1).getIndex());
		}
		
		l += Warehouse.getInstance().getRealDistance(items.get(items.size()-1).getIndex(),finish);
		
		executionTime.setTime(Math.round(l/v) * 1000);
	}
}