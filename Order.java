package Warehouse;

import java.sql.Time;
import java.util.Vector;

public class Order implements Comparable<Order>{
	private long indexOfShop;
	private Vector <OrderItem> items;
	private Time deadline;
	private Expedition deliverySide;
	private Vector <Task> tasks;
	private double maximumVolumeOfTruck; 
	
	Order(Time deadline, Expedition deliverySide, long indexOfShop)
	{
		items = new Vector<OrderItem>();
		tasks = new Vector<Task>();
        this.indexOfShop = indexOfShop;
        this.deadline = deadline;
        this.deliverySide = deliverySide;
        this.maximumVolumeOfTruck = Warehouse.getInstance().getTruckCapacity();
	};
	
	Order(Vector <OrderItem>items,Time deadline, Expedition deliverySide, long indexOfShop){
		this.items = new Vector<OrderItem>();
		tasks = new Vector<Task>();
        this.indexOfShop = indexOfShop;
        this.items = items;
		this.deadline = deadline;
		this.deliverySide = deliverySide;
		this.maximumVolumeOfTruck = Warehouse.getInstance().getTruckCapacity();
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
        this.items = new Vector<OrderItem>(items);
    }

    public final OrderItem getItem(int index)
	{
		return items.get(index);
	};
	
	public void clearAll() {
		items.clear();
	};
	
	public void divideOrderToTasks() {
		Vector <OrderItem> oi = new Vector<OrderItem> (items);
		double volumeOfAllGoodsInContainer = 0;
		int i = 0;
		for (int j = 0; j < oi.size(); j++) {
		    double volume = oi.get(j).getVolume();
		    if (volumeOfAllGoodsInContainer + volume > maximumVolumeOfTruck) {
		        Task t = new Task(deadline);
		        for (;i < j;i++) {
		            t.addItem(oi.get(i));
		        }
		        if (volumeOfAllGoodsInContainer != maximumVolumeOfTruck) {
		            
                    //boxes
                    int nOfBoxes = oi.get(j).getNumberOfBoxes(maximumVolumeOfTruck - volumeOfAllGoodsInContainer,true);
                    t.addItem(oi.get(j));
                    t.getItem(t.getSize()-1).setVolume(nOfBoxes*oi.get(j).getLiters());
		            oi.get(j).setVolume(volume - nOfBoxes*oi.get(j).getLiters());
	            }
		        if (deliverySide == Expedition.North) {
                    t.setFinish(Warehouse.getInstance().getNearestNorthDelivery(i));//get finish point
                }
		        else {
                    t.setFinish(Warehouse.getInstance().getNearestSouthDelivery(i));
                }
		        t.setStart();
		        tasks.add(t);
                volumeOfAllGoodsInContainer = 0;
                i = j+1;
                if (oi.get(j).getVolume() != 0){
                	i = j;
                	j--;
                }
                
            }
		    else {
		        volumeOfAllGoodsInContainer += volume;
            }
		}
		if (i < oi.size()) {
		    Task t = new Task(deadline);
		    for (;i < oi.size();i++) {
                t.addItem(oi.get(i));
            }
		    if (deliverySide == Expedition.North) {
                t.setFinish(Warehouse.getInstance().getNearestNorthDelivery(i));//get finish point
            }
            else {
                t.setFinish(Warehouse.getInstance().getNearestSouthDelivery(i));
            }
            t.setStart();
            tasks.add(t);
        }
		
		for(int j = 0; j < tasks.size(); j++){
			tasks.get(j).calculateExecutionTime();
		}
	};
	
	public final Time executionTimeOfAllTasks()	{
		Time t = new Time(0);
		for (int i = 0; i < tasks.size(); i++)
			t.setTime(tasks.get(i).getExecutionTime().getTime()+t.getTime()* 1000);
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
