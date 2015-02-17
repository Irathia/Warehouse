package Warehouse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Items {
	
	private Vector <Item> items;
	
	Items(String filename) throws Exception
	{
		items = new Vector<Item>();
		readFromFile(filename);
	};
	
	public void readFromFile(String filename) throws Exception
	{
		String line = "";
        int lineCounter = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            line = br.readLine();
            lineCounter++;
            while((line = br.readLine()) != null){
                lineCounter++;
                String[] elements = line.split(";");
                if (elements.length < 5) { throw new Exception(I18n.wrongFormatOfFile(filename)); }
                //shelf,index,boxes,volume,rigidity
                Item it; 
                try {
                    it = new Item(Long.parseLong(elements[1]),Integer.parseInt(elements[4]),Integer.parseInt(elements[2].replaceAll(" ","")),Integer.parseInt(elements[3].replaceAll(" ","")));
                } catch(Exception ex) {
                    throw new Exception(I18n.errorLine(lineCounter, filename));
                }
                
                int index = Warehouse.getInstance().getIndexOfShelf(elements[0]);
                if ( index == -1) {
                    throw new Exception(I18n.shelfNotFound(elements[0]) + I18n.errorLine(lineCounter, filename));
                }
                
                while (index > items.size()) {
                    items.add(new Item());
                }
                if (index == items.size()) {
                    items.add(it);
                }
                else {
                    items.set(index, it);
                }
            }
            br.close();
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
    };
	
	public final Item getItem(int index)
	{
		return items.get(index);
	};
	
	public final int getShelfsIndex(long indexOfItem){
		
		for(int i = 0; i < items.size(); i++){
			if (items.get(i).getIndex() == indexOfItem){
				return i;
			}
		}
		return 0;
	};

    public void addItem(Item item){
        items.add(item);
    }

    public void deleteItem(int index){
        for (int i = 0; i < items.size(); i++){
            if (items.get(i).getIndex() == index){
                items.remove(i);
                return;
            }
        }
    }

    public final int getSize(){
        return items.size();
    }

    public final Vector <Item> getItems(){
        return items;
    }
    
    public boolean isExist(long index){
    	for (int i = 0; i < items.size(); i++){
    		if (items.get(i).getIndex() == index){
    			return true;
    		}
    	}
    	return false;
    }
}
