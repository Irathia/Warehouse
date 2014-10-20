package Warehouse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Logger;

public class Orders {
	
	private Vector <Order> orders;
	private Items items;
	private Vector <Task> tasks;
	private Vector <Integer> indexOfFirstOrderTask;
    private Vector <Integer> delivery;
    private Vector <Integer> counter;//for replenishment()
	
	Orders(String filenameForItems, String fileForShop, String filename)
	{
		orders = new Vector<Order>();
		tasks = new Vector<Task>();
		indexOfFirstOrderTask = new Vector<Integer>();
		this.items = new Items(filename);
		readFromFile(filenameForItems, fileForShop);
	};
	
	private void readFromFile(String filenameForItems, String fileForShop)
	{
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileForShop));
            line = br.readLine();
            while((line = br.readLine()) != null){
                String[] elements = line.split(";");
                //indexForShop,-,time,expedition
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                String[] time = elements[2].split("-");
                Expedition exp = Expedition.North;
                if (elements[3].equals("South")){
                    exp = Expedition.South;
                }
                try {
                	Time deadline = new Time(formatter.parse(time[1]).getTime());
                	Time startOfWork = Warehouse.getInstance().getStartOfWork();
                	Time startOfBreak = Warehouse.getInstance().getStartOfBreak();
                	Time finishOfBreak = Warehouse.getInstance().getFinishOfBreak();
                	Time midnight = new Time(formatter.parse("24:00").getTime());
                	
                	if (deadline.getTime() < startOfBreak.getTime() ){
                		if (deadline.getTime() < startOfWork.getTime()){
                			deadline.setTime(deadline.getTime()+(midnight.getTime()-startOfWork.getTime()));
                		}
                		else{
                			deadline.setTime(deadline.getTime()-startOfWork.getTime());
                		}
                	}
                	else {
                		if (deadline.getTime() < startOfWork.getTime()){
                			deadline.setTime(deadline.getTime()+(midnight.getTime()-startOfWork.getTime())-(finishOfBreak.getTime()-startOfBreak.getTime()));
                		}
                		else{
                			deadline.setTime(deadline.getTime()-startOfWork.getTime()-(finishOfBreak.getTime()-startOfBreak.getTime()));
                		}
                	}
                    Order o = new Order(deadline, exp, Long.parseLong(elements[0]));
                    orders.add(o);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //read vector

        line = "";
        long currentShop = 0L;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filenameForItems));
            line = br.readLine();
            Vector <OrderItem> v = new Vector<OrderItem>();
            while((line = br.readLine()) != null){
                String[] elements = line.split(";");
                //indexForShop,indexForGoods,-,volume,-
                
                if (elements.length == 0) { break; }
                if (currentShop != Long.parseLong(elements[0]) && currentShop != 0L){
                    this.getOrderByShop(currentShop).setItems(v);
                    
                    v.clear();
                    currentShop = Long.parseLong(elements[0]);
                    //
                    if (items.isExist(Long.parseLong(elements[1])) == false) {break;}
                    int indexOfShelf = items.getShelfsIndex(Long.parseLong(elements[1]));
                    double liters = items.getItem(indexOfShelf).getLiters();
                    OrderItem oi = new OrderItem(indexOfShelf,items.getItem(indexOfShelf).getRigidity(),Double.parseDouble(elements[3].replace(',','.')),liters);
                    v.add(oi);
                }
                else{
                    currentShop = Long.parseLong(elements[0]);
                    if (items.isExist(Long.parseLong(elements[1])) == false) {break;}
                    int indexOfShelf = items.getShelfsIndex(Long.parseLong(elements[1]));
                    double liters = items.getItem(indexOfShelf).getLiters();
                    OrderItem oi = new OrderItem(indexOfShelf,items.getItem(indexOfShelf).getRigidity(),Double.parseDouble(elements[3].replace(',','.')),liters);
                    v.add(oi);
                }
            }
            this.getOrderByShop(currentShop).setItems(v);
            
            br.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.sortAll();//sort items orders by deadline and items by rigidity and index of shelf 
        this.divideOrdersToTasks();
        this.numberOfDelivery();//info about delivery
        this.replenishment();//info about replenishment
        this.writeToLOG();
    }

    private Order getOrderByShop(long indexOfShop) {
        for (int i = 0; i < orders.size(); i++){
            if(orders.get(i).getIndexOfShop() == indexOfShop){
                return orders.get(i);
            }
        }
        return null;
    }

    public final Vector <Integer> getDelivery() {
        return delivery;
    }
    
    public final Vector <Integer> getReplenishment(){
    	return counter;
    }

	public void divideOrdersToTasks(){
	    for (int i = 0; i < orders.size(); i++){
			orders.get(i).divideOrderToTasks();
			Vector <Task> t = orders.get(i).getTasks();
			indexOfFirstOrderTask.add(tasks.size());
			for (int j = 0; j < t.size(); j++){
				tasks.add(t.get(j));
			}
		}
	}
	
	public final Order getOrder(int index)
	{
		return orders.get(index);
	}
	
	private void sortAll(){
		Collections.sort(orders);
		for(int i = 0; i < orders.size(); i++){
			orders.get(i).sortItemsInOrder();
		}
	}
	
	public final int getTasksSize(){
		return tasks.size();
	}
	
	public final Vector <Task> getTasks(){
		return tasks;
	}
	
	public final int getNearestTask(int indexOfPreviousTask, Time current, ArrayList <Integer> remaining) {
		int finish = tasks.get(indexOfPreviousTask).getFinish();
		
		int minIndex = -1;
		double minDistance = Double.POSITIVE_INFINITY;
		Time deadline = tasks.get(remaining.get(0)).getDeadline();
		for (int i = 0; i < remaining.size(); i++){
			while (i < remaining.size() && tasks.get(remaining.get(i)).getDeadline().getTime() == deadline.getTime()){
				if (Warehouse.getInstance().getRealDistance(finish, tasks.get(remaining.get(i)).getStart()) < minDistance && tasks.get(remaining.get(i)).getExecutionTime().getTime()+current.getTime()+getTimeForMovingBetweenTasks(indexOfPreviousTask,remaining.get(i)).getTime() <= deadline.getTime()){
					minIndex = i;
					minDistance = Warehouse.getInstance().getRealDistance(finish, tasks.get(remaining.get(i)).getStart());
				}
				i++;
			}
			if (minIndex != -1 || i ==remaining.size()) { break; }
			deadline = tasks.get(remaining.get(i)).getDeadline();
		}
		
		return minIndex;
	}
	
	private int lastTaskIndexOfOrder(int taskIndex) {
	    int lastIndexOfTask = -1;
	    for (int i = 1; i < indexOfFirstOrderTask.size(); i++) {
            if (indexOfFirstOrderTask.get(i) > taskIndex) {
                lastIndexOfTask = indexOfFirstOrderTask.get(i) - 1;
                break;
            }
        }
        if (lastIndexOfTask == -1 || indexOfFirstOrderTask.size() > 0) {
            lastIndexOfTask = tasks.size() - 1; 
        }
	    return lastIndexOfTask;
	}
	
	public final int getNearestTaskFromOrderWithMinNumber(int indexOfPreviousTask, Time current, ArrayList <Integer> remaining) {
        if (remaining.size() < 1) { return -1; }
	    int finish = tasks.get(indexOfPreviousTask).getFinish();
        int lastIndexOfMinOrder = lastTaskIndexOfOrder(remaining.get(0));
        
        int minIndex = -1;
        double minDistance = Double.POSITIVE_INFINITY;
        Time deadline = tasks.get(remaining.get(0)).getDeadline();
        for (int i = 0; i < remaining.size() && remaining.get(i) <= lastIndexOfMinOrder; i++) { 
            if (Warehouse.getInstance().getRealDistance(finish, tasks.get(remaining.get(i)).getStart()) < minDistance && ( tasks.get(remaining.get(i)).getExecutionTime().getTime() + current.getTime() + getTimeForMovingBetweenTasks(indexOfPreviousTask, remaining.get(i)).getTime() ) <= deadline.getTime()){
                minIndex = i;
                minDistance = Warehouse.getInstance().getRealDistance(finish, tasks.get(remaining.get(i)).getStart());
            }
        }
        return minIndex;
    }
	
	public Time getTimeForMovingBetweenTasks(int firstTask, int secondTask) {
	    Warehouse warehouse = Warehouse.getInstance();
	    double result = warehouse.getRealDistance(tasks.get(firstTask).getFinish(), tasks.get(secondTask).getStart());
	    return new Time((long) Math.ceil((result * 1000) / warehouse.getSpeed()));
	}

	public double getDistanceForMovingBetweenTasks(int firstTask, int secondTask) {
        Warehouse warehouse = Warehouse.getInstance();
        return warehouse.getRealDistance(tasks.get(firstTask).getFinish(), tasks.get(secondTask).getStart());
    }
	
    private void numberOfDelivery(){
        //calculate information for delivery
        int last = Warehouse.getInstance().getIndexOfLastDelivery();
        int first = Warehouse.getInstance().getIndexOfFirstDelivery();
        delivery = new Vector<Integer>();
        int deliverySize = last - first + 1;
        
        for (int i = 0; i < deliverySize; i++){
            delivery.add(0);
        }

        for (int i = 0; i < tasks.size(); i++){
            delivery.set(tasks.get(i).getFinish() - first, delivery.get(tasks.get(i).getFinish() - first) + 1);
        }

    }

    public void replenishment(){
        //we write info straight into logs
        Vector <Item> its = new Vector<Item>(items.getItems());
        counter = new Vector <Integer>();

        for( int i = 0; i < its.size(); i++){
            counter.add(0);
        }

        for(int i = 0; i < tasks.size(); i++){
            for (int j = 0; j < tasks.get(i).getSize(); j++){
                its.get(tasks.get(i).getItem(j).getIndex()).setBoxes(its.get(tasks.get(i).getItem(j).getIndex()).getBoxes()-tasks.get(i).getItem(j).getNumberOfBoxes(tasks.get(i).getItem(j).getVolume(),false));
                if (its.get(tasks.get(i).getItem(j).getIndex()).getBoxes() <= 0){
                    its.get(tasks.get(i).getItem(j).getIndex()).setBoxes(items.getItem(tasks.get(i).getItem(j).getIndex()).getBoxes());
                    counter.set(tasks.get(i).getItem(j).getIndex(),counter.get(tasks.get(i).getItem(j).getIndex())+1);
                }
            }
        }

    }
    
    public void writeToLOG(){
    	Logger logger = Logger.getLogger("Test");
    	
    	//delivery
    	logger.info(I18n.DELIVERY);
    	int first = Warehouse.getInstance().getIndexOfFirstDelivery();
    	for(int i = 0; i < delivery.size(); i++){
    		logger.info(Warehouse.getInstance().getNameByIndex(i+first)+": "+delivery.get(i)+I18n.TIMES);
    	}
    	
    	//replenishment
    	logger.info(I18n.REPLENISHMENT);
    	for(int i = 0; i < counter.size(); i++){
    		if (counter.get(i) != 0){
    			logger.info(Warehouse.getInstance().getNameByIndex(i)+": "+counter.get(i)+I18n.TIMES);
    		}
    	}
    }
}
