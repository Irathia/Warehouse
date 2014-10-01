package Warehouse;

import java.util.Vector;

public class OrderItem {
	
	Vector <Integer> index;//index of shelf
	int rigidity;
	double volume;
	
	OrderItem(){
		
	};
	
	OrderItem(Vector <Integer> index, int rigidity, double volume){
		this.index = index;
		this.rigidity = rigidity;
		this.volume = volume;
	}
	
	public final Vector <Integer> getIndex(){
        return index;
    }

    public final int getRigidity(){
        return rigidity;
    }
    
    public final double getVolume(){
    	return volume;
    }

    public void setIndex(Vector <Integer> index){
        this.index = index;
    }

    public void setRigidity(int rigidity){
        this.rigidity = rigidity;
    }
    
    public void setVolume(double volume){
    	this.volume = volume;
    }
}
