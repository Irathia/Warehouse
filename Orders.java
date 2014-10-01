package Warehouse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class Orders {
	
	private Vector <Order> orders;
	private Items items;
	private Vector <Task> tasks;
	
	Orders(String filenameForItems, String fileForShop, Items items)
	{
		this.items = items;
		readFromFile(filenameForItems, fileForShop);
	};
	
	private void readFromFile(String filenameForItems, String fileForShop)
	{
        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileForShop));

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

            Vector <OrderItem> v = null;
            while((line = br.readLine()) != null){
                String[] elements = line.split(";");
                //indexForShop,indecForGoods,-,volume,-
                if (currentShop != Long.parseLong(elements[0]) && currentShop != 0L){
                    this.getOrderByShop(currentShop).setItems(v);
                    
                    v.clear();
                    currentShop = Long.parseLong(elements[0]);
                    //
                    int indexOfShelf = items.getShelfsIndex(Long.parseLong(elements[1]));
                    OrderItem oi = new OrderItem(indexOfShelf,items.getItems(indexOfShelf).getRigidity(),Double.parseDouble(elements[3]));
                    v.add(oi);
                }
                else{
                	 int indexOfShelf = items.getShelfsIndex(Long.parseLong(elements[1]));
                     OrderItem oi = new OrderItem(indexOfShelf,items.getItems(indexOfShelf).getRigidity(),Double.parseDouble(elements[3]));
                     v.add(oi);
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
        
        this.sortAll();
	}

    private Order getOrderByShop(long indexOfShop) {
        for (int i = 0; i < orders.size(); i++){
            if(orders.get(i).getIndexOfShop() == indexOfShop){
                return orders.get(i);
            }
        }
        return null;
    };
	
	public void divideOrdersToTasks(){
		for (int i = 0; i < orders.size(); i++){
			orders.get(i).divideOrderToTasks();
			Vector <Task> t = orders.get(i).getTasks();
			
			for (int j = 0; j < t.size(); j++){
				tasks.add(t.get(j));
			}
		}
			
	};
	
	public final Order getOrder(int index)
	{
		return orders.get(index);
	}
	
	private void sortAll(){
		for(int i = 0; i < orders.size(); i++){
			orders.get(i).sortItemsInOrder();
		}
	}
}
