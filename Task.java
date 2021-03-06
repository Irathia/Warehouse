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
	
	public String toString() {
	    StringBuffer strbuf = new StringBuffer("[" + I18n.TASK_EXECUTION_TIME + ": " + 
	            executionTime + "; " + I18n.TASK_SHELFS + ": [");
	    if (items.size() == 0) {
	        strbuf.append("]]");
	        return strbuf.toString();
	    }
	    Warehouse warehouse = Warehouse.getInstance();
	    strbuf.append(warehouse.getNameByIndex(items.get(0).getIndex()) + ": " + items.get(0).getVolume());
	    for (int i = 1; i < items.size(); i++) {
	        strbuf.append("; " + warehouse.getNameByIndex(items.get(i).getIndex()) + ": " +
	                items.get(i).getVolume());
	    }
	    strbuf.append("]]");
	    return strbuf.toString();
	}
	
	Task(Task t){
		this.deadline = new Time(t.getDeadline().getTime());
		this.executionTime = new Time(t.getExecutionTime().getTime());
		this.items = new Vector<OrderItem> (t.getItems());
		this.l = t.getL();
		this.start = t.getStart();
		this.finish = t.getFinish();
	}
	
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
	
	public final Vector <OrderItem> getItems(){
		return items;
	}

    public final double getL() {
        return  l;
    }

    public final int getSize() {
        return items.size();
    }

    public double getV(){
		double volume = 0;
		for(int i = 0; i < items.size(); i++){
			volume += items.get(i).getVolume();
		}
		return volume;
	}
	
	public void addItem(OrderItem value){
		items.add(value);
	};
	
	public void deleteItem(int index){
		items.remove(index);
	}
	
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
            if (items.get(i).getSignPicking() == true){
                sec += items.get(i).getNumberOfPieces(items.get(i).getVolume(), false)*warehouse.getTimeOfUnitRestacking()+warehouse.getTimeOfDeliveryPreparing();
            }
            else{
                sec += items.get(i).getNumberOfBoxes(items.get(i).getVolume(),false)*warehouse.getTimeOfBoxRestacking()+warehouse.getTimeOfDeliveryPreparing();
            }

		}
        if (items.get(items.size()-1).getSignPicking() == true){
            sec += items.get(items.size()-1).getNumberOfPieces(items.get(items.size()-1).getVolume(),false)*warehouse.getTimeOfUnitRestacking()+warehouse.getTimeOfDeliveryPreparing();
        }
        else{
            sec += items.get(items.size()-1).getNumberOfBoxes(items.get(items.size()-1).getVolume(),false)*warehouse.getTimeOfBoxRestacking()+warehouse.getTimeOfDeliveryPreparing();
        }

		l += warehouse.getRealDistance(items.get(items.size()-1).getIndex(),finish);

        this.l = l;//add set length of path
		executionTime.setTime((long) Math.ceil((l * 1000) / warehouse.getSpeed())+sec*1000+(long)warehouse.getTimeOfContainerPreparing()*1000+(long)warehouse.getTimeOfLabeling()*1000);//time = pick up+path+empty cont
	}
}