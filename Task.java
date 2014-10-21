package Warehouse;

import java.sql.Time;
import java.util.Vector;

public class Task {
	
	private Vector <OrderItem> items;
	private int finish, start; 
	private Time executionTime;
	private long a;
	private Time deadline;
    private double l;
	
	Task(Time deadline){
		this.deadline = new Time(deadline.getTime());
		this.executionTime = new Time(0);
		a = 0;
		items = new Vector<OrderItem> ();
		this.l = 0;
	};
	
	public final Time getExecutionTime(){
		return executionTime;
	};
	public final long getA(){
		return a;
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
		start = 0;
		finish = 0;
		l = 0;
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
	
	public void setExecutionTime(Time executionTime){
		this.executionTime = executionTime;
	}
	
	public void calculateExecutionTime(){
        Warehouse warehouse = Warehouse.getInstance();
        long sec = 0L;//time in sec for pick up
		double l = warehouse.getRealDistance(start,items.get(0).getIndex());
		for (int i = 0; i < items.size()-1; i++){
			l += warehouse.getRealDistance(items.get(i).getIndex(),items.get(i+1).getIndex());
            sec += items.get(i).getNumberOfBoxes(items.get(i).getVolume(),false)*warehouse.getTimeOfRestacking()+warehouse.getTimeOfDeliveryPreparing();
		}
		sec += items.get(items.size()-1).getNumberOfBoxes(items.get(items.size()-1).getVolume(),false)*warehouse.getTimeOfRestacking()+warehouse.getTimeOfDeliveryPreparing();

		l += warehouse.getRealDistance(items.get(items.size()-1).getIndex(),finish);

        this.l = l;//add set length of path
		a += (long) Math.ceil((l) / warehouse.getSpeed())+sec+(long)warehouse.getTimeOfContainerPreparing()+(long)warehouse.getTimeOfLabeling();
		executionTime.setTime((long) Math.ceil((l * 1000) / warehouse.getSpeed())+sec*1000+(long)warehouse.getTimeOfContainerPreparing()*1000+(long)warehouse.getTimeOfLabeling()*1000);//time = pick up+path+empty cont
	}
}