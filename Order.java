package Warehouse;

import java.sql.Time;
import java.util.Vector;

public class Order implements Comparable<Order>{
	private long indexOfShop;
	private Vector <OrderItem> items;
	private Time deadline;
	private Expedition deliverySide;
	private Vector <Task> tasks;
	
	Order(Time deadline, Expedition deliverySide, long indexOfShop)
	{
		items = new Vector<OrderItem>();
		tasks = new Vector<Task>();
        this.indexOfShop = indexOfShop;
        this.deadline = deadline;
        this.deliverySide = deliverySide;
	};
	
	Order(Vector <OrderItem>items,Time deadline, Expedition deliverySide, long indexOfShop){
		this.items = new Vector<OrderItem>();
		tasks = new Vector<Task>();
        this.indexOfShop = indexOfShop;
        this.items = items;
		this.deadline = deadline;
		this.deliverySide = deliverySide;
	};
	
	public final Time getDeadline()
	{
		return deadline;
	};
	
	public final Expedition getDeliverySide()
	{
		return deliverySide;
	};

    public final long getIndexOfShop(){
        return indexOfShop;
    }
    
    public final Vector <Task> getTasks(){
    	return tasks;
    }
	
	public void setDeadline(Time value){
		//need check?
		deadline = value;
	};
	
	public void setDeliverySide(Expedition value)	{
		//need check?
		deliverySide = value;
	};

    public void setIndexOfShop(long indexOfShop){
        this.indexOfShop = indexOfShop;
    }

    public void setItems(Vector<OrderItem> items) {
        this.items = items;
    }

    public final OrderItem getItem(int index)
	{
		return items.get(index);
	};
	
	public void clearAll() {
		items.clear();
	};
	
	public void divideOrderToTasks() {
		Vector <OrderItem> oi = items;
		double volumeOfEmptyContainer = 100;//need to change
		double volumeOfAllGoodsInContainer = 0;
		int i  = 0;
		while ( oi.size() != 0){
			double volume = oi.get(i).getVolume();
			if (volumeOfAllGoodsInContainer + volume >= 100){
				Task t = new Task(deadline);
				//add all goods before i
				for (int j = 0; j < i; j++){
					t.addItem(oi.get(j));
				}
				// add i
				OrderItem ori = new OrderItem(oi.get(i).getIndex(),oi.get(i).getRigidity(),volumeOfEmptyContainer - volumeOfAllGoodsInContainer);
				oi.get(i).setVolume(volume - (volumeOfEmptyContainer - volumeOfAllGoodsInContainer));
				
				
				if (deliverySide == Expedition.North){
					t.setFinish(Warehouse.getInstance().getNearestNorthDelivery(i));//get finish point
				}else{
					t.setFinish(Warehouse.getInstance().getNearestSouthDelivery(i));
				}
				
				if (oi.get(i).getVolume() == 0){
					i++;
				}
				
				
				tasks.add(t);
				volumeOfAllGoodsInContainer = 0;
			}else{
				volumeOfAllGoodsInContainer += volume;
				i++;
			}
		}
		
		for(int j = 0; j < tasks.size(); j++){
			tasks.get(j).calculateExecutionTime();
		}
	};
	
	public final Time executionTimeOfAllTasks()	{
		Time t = new Time(0);
		for (int i = 0; i < tasks.size(); i++)
			t.setTime(tasks.get(i).getExecutionTime().getTime()+t.getTime());
		return t;
	};
	
	public void sortItemsInOrder(){		
		Warehouse.getInstance().sortIndexesForHeuristic(items);
	}

	@Override
	public int compareTo(Order arg0) {
		if (this.deadline.getTime() - arg0.deadline.getTime() > 0) {
			return 1;
		} else if (this.deadline.getTime() - arg0.deadline.getTime() < 0) {
			return -1;
		}
		return 0;
	}
}
