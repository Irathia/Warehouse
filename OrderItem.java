package Warehouse;

public class OrderItem implements Comparable<OrderItem>{
	
	private  int index;//index of shelf
	private  int rigidity;
	private  double volume;
    private double liters;//1 box = liters
	
	OrderItem(){
		
	};
	
	OrderItem(int index, int rigidity, double volume, double liters){
		this.index = index;
		this.rigidity = rigidity;
		this.volume = volume;
        this.liters = liters;
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

    public final int getNumberOfBoxes(double v){
        double b = Math.floor(v/liters);
        return (int)b;
    }

    public final double getLiters(){
        return liters;
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

    @Override
    public int compareTo(OrderItem o) {
        if (this.rigidity != o.rigidity) {
            return o.rigidity - this.rigidity;
        }
        return this.index - o.index;
    }
}
