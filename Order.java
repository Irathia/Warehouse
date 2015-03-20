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
	
	public void divideOrderToTasks() throws Exception{
		Vector <OrderItem> oi = new Vector<OrderItem> (items);
		double volumeOfAllGoodsInContainer = 0;
		double volumeOfAllGoodsInLastTask = 0;
		int i = 0;
		boolean flag = false;
		for (int j = 0; j < oi.size(); j++) {
			//if (tasks.size() > 80)
			//	System.out.println("g");
		    double volume = oi.get(j).getVolume();
		    if (volumeOfAllGoodsInContainer + volume > maximumVolumeOfTruck) {
		    	Task t;
		    	if (flag == true){
		    		volumeOfAllGoodsInLastTask = 0;
		    		t = new Task(tasks.get(tasks.size()-1));
		    		tasks.remove(tasks.get(tasks.size()-1));
		    	}
		    	else{
		    		t = new Task(deadline);
		    	}
		    	flag = false;
		        for (;i < j;i++) {
		        	if (t.getItems().size() != 0 && t.getItem(t.getItems().size()-1).getIndex() == oi.get(i).getIndex()){
		        		t.getItem(t.getItems().size()-1).setVolume(t.getItem(t.getItems().size()-1).getVolume() + oi.get(i).getVolume());
		        	}
		        	else{
		        		t.addItem(oi.get(i));
		        	}
		        }
		        if (volumeOfAllGoodsInContainer != maximumVolumeOfTruck) {
		            
                    if(oi.get(j).getSignPicking() == true){
                        //pieces
                        int nOfPieces = oi.get(j).getNumberOfPieces(maximumVolumeOfTruck - volumeOfAllGoodsInContainer,true);
                        double previousVolume = 0;
                        if (t.getItems().size() != 0 && t.getItem(t.getItems().size()-1).getIndex() == oi.get(j).getIndex()){
                            previousVolume = t.getItem(t.getItems().size()-1).getVolume();
                            t.getItem(t.getItems().size()-1).setVolume(t.getItem(t.getItems().size()-1).getVolume() + oi.get(j).getVolume());
                        }
                        else{
                            t.addItem(new OrderItem(oi.get(j)));
                        }

                        t.getItem(t.getSize()-1).setVolume(nOfPieces*oi.get(j).getAllVolume()/oi.get(j).getPieces() + previousVolume);
                        oi.get(j).setVolume(volume - nOfPieces * oi.get(j).getAllVolume() / oi.get(j).getPieces());
                    }
                    else{
                        //boxes
                        int nOfBoxes = oi.get(j).getNumberOfBoxes(maximumVolumeOfTruck - volumeOfAllGoodsInContainer,true);
                        double previousVolume = 0;
                        if (t.getItems().size() != 0 && t.getItem(t.getItems().size()-1).getIndex() == oi.get(j).getIndex()){
                            previousVolume = t.getItem(t.getItems().size()-1).getVolume();
                            t.getItem(t.getItems().size()-1).setVolume(t.getItem(t.getItems().size()-1).getVolume() + oi.get(j).getVolume());
                        }
                        else{
                            t.addItem(new OrderItem(oi.get(j)));
                        }

                        t.getItem(t.getSize()-1).setVolume(nOfBoxes*oi.get(j).getAllVolume()/oi.get(j).getBoxes() + previousVolume);
                        oi.get(j).setVolume(volume - nOfBoxes*oi.get(j).getAllVolume()/oi.get(j).getBoxes());
                    }

	            }
		        if(t.getItem(t.getSize()-1).getVolume() == 0){
		        	t.deleteItem(t.getSize()-1);
		        }
		        if (t.getItems().size() == 0){
					throw new Exception(I18n.cantPlaceItem(this.indexOfShop,oi.get(j).getIndexOfItem()));
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
		        
		        if (tasks.get(tasks.size()-1).getExecutionTime().getTime() > Warehouse.getInstance().getMaximumOrderExecutionTime()){
		        	divideTask();
		        	volumeOfAllGoodsInLastTask = tasks.get(tasks.size()-1).getV();
		        	flag = true;
		        }
                volumeOfAllGoodsInContainer = volumeOfAllGoodsInLastTask;
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
		    	if (t.getItems().size() != 0 && t.getItem(t.getItems().size()-1).getIndex() == oi.get(i).getIndex()){
	        		t.getItem(t.getItems().size()-1).setVolume(t.getItem(t.getItems().size()-1).getVolume() + oi.get(i).getVolume());
	        	}
	        	else{
	        		t.addItem(oi.get(i));
	        	}
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
            if (tasks.get(tasks.size()-1).getExecutionTime().getTime() > Warehouse.getInstance().getMaximumOrderExecutionTime()){
	        	divideTask();
	        }
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
	
	private void divideTask(){
		Task t = new Task(tasks.get(tasks.size()-1));
		tasks.remove(tasks.size()-1);
		long maxExecutionTime = Warehouse.getInstance().getMaximumOrderExecutionTime();
		int numberOfTasks = (int)Math.ceil((double)t.getExecutionTime().getTime()/(double)maxExecutionTime);
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
				if (t1.getExecutionTime().getTime() >= maxExecutionTime){
					int index = t1.getItems().size()-1;

                    if (t1.getItem(index).getSignPicking() == true){
                        //pieces
                        int the_number_of_pieces_for_full_volume = t1.getItem(index).getNumberOfPieces(t1.getItem(index).getVolume(), false);

                        int pieces = (int)Math.max(0, (int)Math.floor((maxExecutionTime - (t1.getExecutionTime().getTime() - the_number_of_pieces_for_full_volume*Warehouse.getInstance().getTimeOfUnitRestacking()*1000))/(Warehouse.getInstance().getTimeOfUnitRestacking()*1000)));
                        if (pieces == 0){
                            t1.getItems().remove(index);
                        }
                        else{
                            t1.getItem(index).setVolume(pieces*t1.getItem(index).getAllVolume()/t1.getItem(index).getPieces());
                            t.getItem(j).setVolume(t.getItem(j).getVolume() - t1.getItem(index).getVolume());
                        }
                    }
                    else{
                        //boxes
                        int the_number_of_boxes_for_full_volume = t1.getItem(index).getNumberOfBoxes(t1.getItem(index).getVolume(),false);

                        int boxes = (int)Math.max(0, (int)Math.floor((maxExecutionTime - (t1.getExecutionTime().getTime() - the_number_of_boxes_for_full_volume*Warehouse.getInstance().getTimeOfBoxRestacking()*1000))/(Warehouse.getInstance().getTimeOfBoxRestacking()*1000)));
                        if (boxes == 0){
                            t1.getItems().remove(index);
                        }
                        else{
                            t1.getItem(index).setVolume(boxes*t1.getItem(index).getAllVolume()/t1.getItem(index).getBoxes());
                            t.getItem(j).setVolume(t.getItem(j).getVolume() - t1.getItem(index).getVolume());
                        }
                    }

                    if (deliverySide == Expedition.North) {
                        t1.setFinish(Warehouse.getInstance().getNearestNorthDelivery(t1.getItem(t1.getSize()-1).getIndex()));//get finish point
                    }
                    else {
                        t1.setFinish(Warehouse.getInstance().getNearestSouthDelivery(t1.getItem(t1.getSize()-1).getIndex()));
                    }
					t1.calculateExecutionTime();
					tasks.add(t1);
					i++;
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
