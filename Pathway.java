
public class Pathway {
	private ShelfRow leftRow;
	private ShelfRow rightRow;
	
	public Pathway() {
		leftRow = new ShelfRow(Side.Left);
		rightRow = new ShelfRow(Side.Right);
	}
	
	public boolean addToLeftRow(Shelf sh) { 
		return leftRow.add(sh);
	}
	
	public boolean addToRightRow(Shelf sh) {
		return rightRow.add(sh);
	}
	
	public PointWithName getPickupPointOfLeftRow(int index) {
		return leftRow.get(index);
	}
	
	public PointWithName getPickupPointOfRightRow(int index) {
		return rightRow.get(index);
	}
	
	public int sizeOfLeftRow() {
		return leftRow.size();
	}
	
	public int sizeOfRightRow() {
		return rightRow.size();
	}
	
	public int totalSize() {
		return leftRow.size() + rightRow.size();
	}
	
	public int findName(String name) {
		int result = leftRow.findName(name);
		if (result != -1) {
			return result;
		}
		result = rightRow.findName(name);
		if (result != -1) {
			return leftRow.size() + result;
		}
		return -1;
	}
	
	/*side variants: 'l', 'r', 'b'*/
	public double getHighestRoadlock(char side) {
		switch (side) {
	        case 'l':  
	        	return leftRow.getTopLeftCornerOfRow().getY();
	        case 'r':
	    		return rightRow.getTopLeftCornerOfRow().getY();
	        case 'b':
	        	return Math.max(leftRow.getTopLeftCornerOfRow().getY(), rightRow.getTopLeftCornerOfRow().getY());
	    	default:
	    		return Double.NEGATIVE_INFINITY;
		}
	}
	
	/*side variants: 'l', 'r', 'b'*/
	public double getLowestRoadlock(char side) {
		switch (side) {
			case 'l':
				return leftRow.getBottomRightCornerOfRow().getY();
			case 'r':
				return rightRow.getBottomRightCornerOfRow().getY();
			case 'b':
				return Math.min(leftRow.getBottomRightCornerOfRow().getY(), rightRow.getBottomRightCornerOfRow().getY());
			default:
				return Double.POSITIVE_INFINITY;
		}
	}
	
	public boolean isLocatedBetweenRows(Point p) {
		Point bottomLeft = leftRow.getBottomRightCornerOfRow();
		Point topRight = rightRow.getTopLeftCornerOfRow();
		if ( p.getX() >= bottomLeft.getX() && p.getX() <= topRight.getX() &&
			 p.getY() >= bottomLeft.getY() && p.getY() <= topRight.getY() )
		{
			return true;
		}
		return false;
	}
	
	public boolean isLocatedInsideHorizontalBar(Point p) {
		Point topLeft = leftRow.getTopLeftCornerOfRow();
		Point bottomRight = rightRow.getBottomRightCornerOfRow();
		if ( p.getX() >= topLeft.getX() && p.getX() <= bottomRight.getX() ) {
			return true;
		}
		return false;
	}
}