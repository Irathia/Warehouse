package Warehouse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
    private Vector <Double> counter;//for replenishment() in liters
	
	Orders(String filenameForItems, String fileForShop, String filename) throws Exception
	{
		orders = new Vector<Order>();
		tasks = new Vector<Task>();
		indexOfFirstOrderTask = new Vector<Integer>();
		this.items = new Items(filename);
		readFromFile(filenameForItems, fileForShop);
	};
	
	private void readFromFile(String filenameForItems, String fileForShop) throws Exception
	{
        String line = "";
        int lineCounter = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileForShop));
            line = br.readLine();
            lineCounter++;
            while((line = br.readLine()) != null){
                lineCounter++;
                String[] elements = line.split(";");
                //indexForShop,-,time,expedition
                if (elements.length == 0) {
                    break;
                } else if (elements.length < 4) {
                    throw new Exception(I18n.wrongFormatOfFile(fileForShop)); 
                }
                DateFormat formatter = new SimpleDateFormat("HH:mm");
                String[] time = elements[2].split("-");
                Expedition exp;
                if (elements[3].equalsIgnoreCase("South")){
                    exp = Expedition.South;
                } else  if (elements[3].equalsIgnoreCase("North")) {
                    exp = Expedition.North;
                } else {
                    throw new Exception(I18n.wrongCell(lineCounter, 4, fileForShop));
                }
                
                try {
                    Time deadline = null;
                    try {
                        deadline = new Time(formatter.parse(time[1]).getTime());
                    } catch(Exception ex) {
                        throw new Exception(I18n.WRONG_TIME_INTERVAL_FORMAT + "\n" + I18n.wrongCell(lineCounter, 3, fileForShop));
                    }
                    Time startOfWork = Warehouse.getInstance().getStartOfWork();
                    Time startOfBreak = Warehouse.getInstance().getStartOfBreak();
                    Time finishOfBreak = Warehouse.getInstance().getFinishOfBreak();
                    Time midnight = new Time(formatter.parse("24:00").getTime());

                    if (deadline.getTime() <= startOfBreak.getTime() ) {
                        if (deadline.getTime() < startOfWork.getTime()) {
                            deadline.setTime(deadline.getTime()+(midnight.getTime()-startOfWork.getTime()));
                        }
                        else {
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
                    Order o;
                    try {
                        o = new Order(deadline, exp, Long.parseLong(elements[0]));
                    } catch (Exception ex) {
                        throw new Exception(I18n.wrongCell(lineCounter, 1, fileForShop));
                    }
                    orders.add(o);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //read vector

        line = "";
        long currentShop = 0L;
        lineCounter = 0;
        try {
            br = new BufferedReader(new FileReader(filenameForItems));
            line = br.readLine();
            lineCounter++;
            Vector <OrderItem> v = new Vector<OrderItem>();
            while((line = br.readLine()) != null){
                lineCounter++;
                String[] elements = line.split(";");
                //indexForShop,indexForGoods,pieces,volume,boxes,flag
                
                if (elements.length == 0) {
                    break;
                } else if (elements.length < 6) {
                    throw new Exception(I18n.wrongFormatOfFile(filenameForItems)); 
                }
                
                try {
                    if (currentShop != Long.parseLong(elements[0]) && currentShop != 0L){
                        this.getOrderByShop(currentShop).setItems(v);
                        
                        v.clear();
                        currentShop = Long.parseLong(elements[0]);
                        //
                        if (items.isExist(Long.parseLong(elements[1])) == false) {
                        	throw new Exception(I18n.itemNotFound(elements[1]));
                        }
                        int indexOfShelf = items.getShelfsIndex(Long.parseLong(elements[1]));
                        if (indexOfShelf == -1) {
                            throw new Exception(I18n.wrongFormatOfFile(filenameForItems) + I18n.itemNotFound(elements[1]));
                        }
                        boolean signPicking = false;
                        int restackingFlag = Integer.parseInt(elements[5]);
                        if(restackingFlag == 1) {
                            signPicking = true;
                        }
                        else if (restackingFlag != 0) {
                            throw new Exception(I18n.wrongFormatOfFile(filenameForItems) + I18n.wrongFlag(elements[5]));
                        }
                        OrderItem oi = new OrderItem(indexOfShelf,items.getItem(indexOfShelf).getIndex(),items.getItem(indexOfShelf).getRigidity(),Double.parseDouble(elements[3].replace(',','.')),(int)Math.ceil(Double.parseDouble(elements[2].replaceAll(",", "."))),(int)Math.ceil(Double.parseDouble(elements[4].replaceAll(",", "."))),signPicking);
                        v.add(oi);
                    }
                    else {
                        currentShop = Long.parseLong(elements[0]);
                        if (items.isExist(Long.parseLong(elements[1])) == false) {
                        	throw new Exception(I18n.itemNotFound(elements[1]));
                        }
                        int indexOfShelf = items.getShelfsIndex(Long.parseLong(elements[1]));
                        if (indexOfShelf == -1) {
                            throw new Exception(I18n.wrongFormatOfFile(filenameForItems) + I18n.itemNotFound(elements[1]));
                        }
                        boolean signPicking = false;
                        int restackingFlag = Integer.parseInt(elements[5]);
                        if(restackingFlag == 1) {
                            signPicking = true;
                        }
                        else if (restackingFlag != 0) {
                            throw new Exception(I18n.wrongFormatOfFile(filenameForItems) + I18n.wrongFlag(elements[5]));
                        }
                        OrderItem oi = new OrderItem(indexOfShelf,items.getItem(indexOfShelf).getIndex(),items.getItem(indexOfShelf).getRigidity(),Double.parseDouble(elements[3].replace(',','.')),(int)Math.ceil(Double.parseDouble(elements[2].replaceAll(",", "."))),(int)Math.ceil(Double.parseDouble(elements[4].replaceAll(",", "."))),signPicking);
                        v.add(oi);
                    }
                } catch (Exception ex) {
                    throw new Exception(ex.getMessage() + "\n" + I18n.errorLine(lineCounter, filenameForItems));
                }
            }
            this.getOrderByShop(currentShop).setItems(v);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    
    public final Vector <Double> getReplenishment(){
    	return counter;
    }

	public void divideOrdersToTasks() throws Exception {
	    for (int i = 0; i < orders.size(); i++){
			orders.get(i).divideOrderToTasks();
			Vector <Task> t = orders.get(i).getTasks();
			indexOfFirstOrderTask.add(tasks.size());
			for (int j = 0; j < t.size(); j++){
				tasks.add(t.get(j));
			}
			System.out.println(orders.get(i).getIndexOfShop() + " " + tasks.size());
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
	    if (remaining.size() == 0) {
            return -1;
        }
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
			if (minIndex != -1 || i == remaining.size()) { break; }
			deadline = tasks.get(remaining.get(i)).getDeadline();
			i--;
		}
		
		return minIndex;
	}
	
	public final int getTaskWithMaxExecTime(Time start, ArrayList <Integer> remaining) {
        if (remaining.size() == 0) {
            return -1;
        }
	    
        int indexOfTaskWithMaxTime = 0;
        Time deadline = tasks.get(remaining.get(indexOfTaskWithMaxTime)).getDeadline();
        int i = 1;
        while (i < remaining.size() && tasks.get(remaining.get(i)).getDeadline().getTime() == deadline.getTime())
        {
            if (tasks.get(remaining.get(indexOfTaskWithMaxTime)).getExecutionTime().getTime() < tasks.get(remaining.get(i)).getExecutionTime().getTime())
            {
                indexOfTaskWithMaxTime = i;
            }
            i++;
        }
        long startL = start.getTime();
        long executionL = tasks.get(remaining.get(indexOfTaskWithMaxTime)).getExecutionTime().getTime();
        long deadlineL = tasks.get(remaining.get(indexOfTaskWithMaxTime)).getDeadline().getTime();
        
        if (startL + executionL  > deadlineL) {
            return -1;
        }
        return indexOfTaskWithMaxTime;
    }
	
    public final int getTaskWithMaxExecTime(int indexOfPreviousTask, Time current, ArrayList <Integer> remaining) {
        if (remaining.size() == 0) {
            return -1;
        }
        int indexOfTaskWithMaxTime = -1;
        Time maxTime = new Time(0);
        Time deadline = tasks.get(remaining.get(0)).getDeadline();
        for (int i = 0; i < remaining.size(); i++){
            while (i < remaining.size() && tasks.get(remaining.get(i)).getDeadline().getTime() == deadline.getTime()){
                if (maxTime.getTime() < tasks.get(remaining.get(i)).getExecutionTime().getTime()
                        && tasks.get(remaining.get(i)).getExecutionTime().getTime() + current.getTime() + getTimeForMovingBetweenTasks(indexOfPreviousTask,remaining.get(i)).getTime() <= deadline.getTime()) {
                    indexOfTaskWithMaxTime = i;
                    maxTime = tasks.get(remaining.get(i)).getExecutionTime();
                }
                i++;
            }
            if (indexOfTaskWithMaxTime != -1 || i == remaining.size()) { break; }
            deadline = tasks.get(remaining.get(i)).getDeadline();
            i--;
        }
        
        return indexOfTaskWithMaxTime;
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
        counter = new Vector <Double>();
        Vector <Double> v = new Vector <Double>();
        for( int i = 0; i < its.size(); i++){
            v.add(0.0);
        }

        for(int i = 0; i < tasks.size(); i++){
            for (int j = 0; j < tasks.get(i).getSize(); j++){
            	v.set(tasks.get(i).getItem(j).getIndex(), v.get(tasks.get(i).getItem(j).getIndex()) + tasks.get(i).getItem(j).getVolume());
            }
        }
        
        for( int i = 0; i < its.size(); i++){
            counter.add(Math.max(0, v.get(i) - its.get(i).getVolume()));
        }

    }
    
    public void writeToLOG(){
        Logger logger = Logger.getLogger("Test");
    	
    	//tasks
    	logger.info(I18n.TASKS_INFO);
    	for(int i = 0; i < tasks.size(); i++){
            logger.info( (i+1) + " " + I18n.TASK + ": " + tasks.get(i).toString());
        }
    	
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
    			logger.info(Warehouse.getInstance().getNameByIndex(i) + ": " + counter.get(i));
    		}
    	}
    }
    
    public void writeToCSV(BufferedWriter writer) throws IOException {
        writer.write(I18n.REPLENISHMENT + ";\n");
        for(int i = 0; i < counter.size(); i++){
            if (counter.get(i) != 0){
                writer.write(Warehouse.getInstance().getNameByIndex(i) + ";" + Double.toString(counter.get(i)).replace(".", ",") + ";\n");
            }
        }
    }
}
