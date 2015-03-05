package Warehouse;

public class OrderItem implements Comparable<OrderItem>{
	
	private  int index;//index of shelf
	private  int rigidity;
	private  double volume;
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
	}
	
	OrderItem(OrderItem item){
		this.index = item.index;
		this.rigidity = item.rigidity;
		this.volume = item.volume;
        this.pieces = item.pieces;
        this.boxes = item.boxes;
        this.signPicking = item.signPicking;
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
    		return (int)Math.floor((long)(v*boxes/volume));
    	}
    	else{
    		return (int)Math.ceil((long)(v*boxes/volume));
    	}
    }

    public final int getNumberOfPieces(double v, boolean floor){
        if (floor == true){
            return (int)Math.floor((long)(v*pieces/volume));
        }
        else{
            return (int)Math.ceil((long)(v*pieces/volume));
        }
    }

    public final double getPieces() {return pieces; }

    public final double getBoxes() {return boxes; }

    public final boolean getSignPicking() {return signPicking; }

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
