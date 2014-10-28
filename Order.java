package Warehouse;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Logger;

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
		Time hour = new Time(0);
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		try {
			hour.setTime(formatter.parse("01:00").getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
                    t.addItem(new OrderItem(oi.get(j)));
                    t.getItem(t.getSize()-1).setVolume(nOfBoxes*oi.get(j).getLiters());
		            oi.get(j).setVolume(volume - nOfBoxes*oi.get(j).getLiters());
	            }
		        if (deliverySide == Expedition.North) {
                    t.setFinish(Warehouse.getInstance().getNearestNorthDelivery(t.getItem(t.getSize()-1).getIndex()));//get finish point
                }
		        else {
                    t.setFinish(Warehouse.getInstance().getNearestSouthDelivery(t.getItem(t.getSize()-1).getIndex()));
                }
		        t.setStart();
		        tasks.add(t);
		        tasks.get(tasks.size()-1).calculateExecutionTime();
		        if (tasks.get(tasks.size()-1).getExecutionTime().getTime() > hour.getTime()){
		        	divideTask();
		        }
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
                t.setFinish(Warehouse.getInstance().getNearestNorthDelivery(t.getItem(t.getSize()-1).getIndex()));//get finish point
            }
            else {
                t.setFinish(Warehouse.getInstance().getNearestSouthDelivery(t.getItem(t.getSize()-1).getIndex()));
            }
            t.setStart();
            tasks.add(t);
            tasks.get(tasks.size()-1).calculateExecutionTime();
            if (tasks.get(tasks.size()-1).getExecutionTime().getTime() > hour.getTime()){
	        	divideTask();
	        }
        }
		
	/*	for(int j = 0; j < tasks.size(); j++){
			//tasks.get(j).calculateExecutionTime();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(tasks.get(j).getExecutionTime().getTime());
			System.out.println(cal.getTime());
		}*/
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
	
	private void divideTask(){
		Task t = new Task(tasks.get(tasks.size()-1));
		tasks.remove(tasks.size()-1);
		Time h = new Time(0);
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		try {
			h.setTime(formatter.parse("01:00").getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int numberOfTasks = (int)Math.ceil((double)t.getExecutionTime().getTime()/(double)h.getTime());
		int i = 0;
		int j = 0;
		boolean flag = false;
		while(i != numberOfTasks){
			flag = false;
			Task t1 =  new Task(deadline);
			for (; j < t.getSize(); j++){
				if (t1.getSize() == 0){
					t1.addItem(new OrderItem(t.getItem(j)));
					t1.setStart();
				}
				else{
					t1.addItem(new OrderItem(t.getItem(j)));
				}
				if (deliverySide == Expedition.North) {
	                t1.setFinish(Warehouse.getInstance().getNearestNorthDelivery(t1.getItem(t1.getSize()-1).getIndex()));//get finish point
	            }
	            else {
	                t1.setFinish(Warehouse.getInstance().getNearestSouthDelivery(t1.getItem(t1.getSize()-1).getIndex()));
	            }
				t1.calculateExecutionTime();
				if (t1.getExecutionTime().getTime() > h.getTime()){
					if(t1.getSize() == 1){
						int ab = t1.getItem(0).getNumberOfBoxes(t1.getItem(0).getVolume(),false);
						int boxes = (int)Math.floor((h.getTime() - (t1.getExecutionTime().getTime() - ab*Warehouse.getInstance().getTimeOfRestacking()*1000))/(Warehouse.getInstance().getTimeOfRestacking()*1000));
						
						t1.getItem(0).setVolume(boxes*t1.getItem(0).getLiters());
						t.getItem(j).setVolume(t.getItem(j).getVolume() - boxes*t1.getItem(0).getLiters());
						if (deliverySide == Expedition.North) {
			                t1.setFinish(Warehouse.getInstance().getNearestNorthDelivery(t1.getItem(t1.getSize()-1).getIndex()));//get finish point
			            }
			            else {
			                t1.setFinish(Warehouse.getInstance().getNearestSouthDelivery(t1.getItem(t1.getSize()-1).getIndex()));
			            }
						t1.calculateExecutionTime();
						tasks.add(t1);
						i++;
					}
					else{
						t1.deleteItem(t1.getSize()-1);
						if (deliverySide == Expedition.North) {
			                t1.setFinish(Warehouse.getInstance().getNearestNorthDelivery(t1.getItem(t1.getSize()-1).getIndex()));//get finish point
			            }
			            else {
			                t1.setFinish(Warehouse.getInstance().getNearestSouthDelivery(t1.getItem(t1.getSize()-1).getIndex()));
			            }
						t1.calculateExecutionTime();
						tasks.add(t1);
						i++;
					}
					flag = true;
					break;
				}
				
			}
			if (flag == false){
				if(t1.getSize() == 0) { break;}
				if (deliverySide == Expedition.North) {
	                t1.setFinish(Warehouse.getInstance().getNearestNorthDelivery(t1.getItem(t1.getSize()-1).getIndex()));//get finish point
	            }
	            else {
	                t1.setFinish(Warehouse.getInstance().getNearestSouthDelivery(t1.getItem(t1.getSize()-1).getIndex()));
	            }
				t1.calculateExecutionTime();
				tasks.add(t1);
				i++;
			}
			
		}
	}
}
