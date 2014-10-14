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

public class Orders {
	
	private Vector <Order> orders;
	private Items items;
	private Vector <Task> tasks;
    private Vector <Integer> delivery;
	
	Orders(String filenameForItems, String fileForShop, String filename)
	{
		orders = new Vector<Order>();
		tasks = new Vector<Task>();
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
                if (elements[3] == "South"){
                    exp = Expedition.South;
                }
                try {
                    Order o = new Order(new Time(formatter.parse(time[1]).getTime()), exp, Long.parseLong(elements[0]));
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
                    int indexOfShelf = items.getShelfsIndex(Long.parseLong(elements[1]));
                    OrderItem oi = new OrderItem(indexOfShelf,items.getItems(indexOfShelf).getRigidity(),Double.parseDouble(elements[3].replace(',','.')));
                    v.add(oi);
                }
                else{
                    currentShop = Long.parseLong(elements[0]);
                    int indexOfShelf = items.getShelfsIndex(Long.parseLong(elements[1]));
                    OrderItem oi = new OrderItem(indexOfShelf,items.getItems(indexOfShelf).getRigidity(),Double.parseDouble(elements[3].replace(',','.')));
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

	public void divideOrdersToTasks(){
		for (int i = 0; i < orders.size(); i++){
			orders.get(i).divideOrderToTasks();
			Vector <Task> t = orders.get(i).getTasks();
			
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
	
	public final Vector <Task> getTask(){
		return tasks;
	}
	
	public final int getNearestTask(int indexOfPreviousTask, Time current, ArrayList <Integer> remaining){
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
	
	public Time getTimeForMovingBetweenTasks(int firstTask, int secondTask) {
	    double result = Warehouse.getInstance().getRealDistance(tasks.get(firstTask).getFinish(), tasks.get(secondTask).getStart());
	    return new Time(Math.round(result/tasks.get(0).v) * 1000);
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
    }
}
