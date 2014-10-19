package Warehouse;

import java.sql.Time;
import java.util.Vector;

public class Task {
	
	private Vector <OrderItem> items;
	private int finish, start; 
	private Time executionTime;
	private Time deadline;
    private double l;
	
	Task(Time deadline){
		this.deadline = new Time(deadline.getTime());
		this.executionTime = new Time(0);
		items = new Vector<OrderItem> ();
		this.l = 0;
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

    public final double getL() {
        return  l;
    }

    public final int getSize() {
        return items.size();
    }

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
        Warehouse warehouse = Warehouse.getInstance();
        long sec = 0;//time in sec for pick up
		double l = warehouse.getRealDistance(start,items.get(0).getIndex());
		for (int i = 0; i < items.size()-1; i++){
			l += warehouse.getRealDistance(items.get(i).getIndex(),items.get(i+1).getIndex());
            sec += items.get(i).getNumberOfBoxes(items.get(i).getVolume())*warehouse.getTimeOfRestacking()+warehouse.getTimeOfDeliveryPreparing();
		}
		

		l += warehouse.getRealDistance(items.get(items.size()-1).getIndex(),finish);

        this.l = l;//add set length of path
		
		executionTime.setTime((long) Math.ceil((l * 1000) / warehouse.getSpeed())+sec*1000+(long)warehouse.getTimeOfContainerPreparing()*1000+(long)warehouse.getTimeOfLabeling()*1000);//time = pick up+path+empty cont
	}
}