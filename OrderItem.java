package Warehouse;

public class OrderItem implements Comparable<OrderItem>{
	
	private  int index;//index of shelf
	private  int rigidity;
	private  double volume;
	private double AllVolume;
    private int pieces;
    private int boxes;
    boolean signPicking;//1 - pieces, 0 - box
	
	OrderItem(){
	};

	OrderItem(int index, int rigidity, double volume, int pieces,int boxes, boolean signPicking){
		this.index = index;
		this.rigidity = rigidity;
		this.volume = volume;
        this.pieces = pieces;
        this.boxes = boxes;
        this.signPicking = signPicking;
        this.AllVolume = volume;
	}
	
	OrderItem(OrderItem item){
		this.index = item.index;
		this.rigidity = item.rigidity;
		this.volume = item.volume;
        this.pieces = item.pieces;
        this.boxes = item.boxes;
        this.signPicking = item.signPicking;
        this.AllVolume = item.AllVolume;
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

    public final int getNumberOfBoxes(double v, boolean floor){
    	if (floor == true){
    		return (int)Math.floor((long)(v*boxes/AllVolume));
    	}
    	else{
    		return (int)Math.ceil((long)(v*boxes/AllVolume));
    	}
    }

    public final int getNumberOfPieces(double v, boolean floor){
        if (floor == true){
            return (int)Math.floor((long)(v*pieces/AllVolume));
        }
        else{
            return (int)Math.ceil((long)(v*pieces/AllVolume));
        }
    }

    public final double getPieces() {return pieces; }

    public final double getBoxes() {return boxes; }

    public final boolean getSignPicking() {return signPicking; }
    
    public final double getAllVolume() {return AllVolume; }

    public void setIndex(int index){
        this.index = index;
    }

    public void setRigidity(int rigidity){
        this.rigidity = rigidity;
    }
    
    public void setVolume(double volume){
    	this.volume = volume;
    }

    public void setBoxes(int boxes) {this.boxes = boxes;}

    public void setPieces(int pieces) {this.pieces = pieces;}

    @Override
    public int compareTo(OrderItem o) {
        if (this.rigidity != o.rigidity) {
            return o.rigidity - this.rigidity;
        }
        return this.index - o.index;
    }
}
