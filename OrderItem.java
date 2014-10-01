package Warehouse;

public class OrderItem {
	
	private  int index;//index of shelf
	private  int rigidity;
	private  double volume;
	
	OrderItem(){
		
	};
	
	OrderItem(int index, int rigidity, double volume){
		this.index = index;
		this.rigidity = rigidity;
		this.volume = volume;
	}
	
	public final int getIndex(){
        return index;
    }

    public final int getRigidity(){
        return rigidity;
    }
    
    public final double getVolume(){
    	return volume;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public void setRigidity(int rigidity){
        this.rigidity = rigidity;
    }
    
    public void setVolume(double volume){
    	this.volume = volume;
    }
}
