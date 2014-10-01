import java.util.Vector;


public class ShelfRow extends Rectangle {
	private final Side SIDE;
	public final static double DISTANCE_BETWEEN_SHELF_AND_TRUCK = 0.5;
	
	private Vector<PointWithName> pickupPoints;
	
	public ShelfRow(Side side) {
		super(0,0,0,0);
		this.SIDE = side;
	}
	
	public PointWithName get(int index) {
		return pickupPoints.get(index);
	}

	public boolean add(Shelf sh) {
		if (pickupPoints.size() == 0) {
			this.setTopLeft(sh.getTopLeft());
			this.setBottomRight(sh.getBottomRight());
		}
		else {
			if (sh.getTopLeftY() > this.getTopLeftY()) {
				this.setTopLeft(sh.getTopLeft());
			}
			if (sh.getBottomRightY() < this.getBottomRightY()) {
				this.setBottomRight(sh.getBottomRight());
			}
		}
		// �������� �������� � ������
		if (sh.getName().toUpperCase().contains(EmptyContainer.NAME)) {
			return true;
		}
		double y = (sh.getTopLeftY()-sh.getBottomRightY())/2.0;
		double x;
		
		if (SIDE == Side.Left) {
			x = sh.getBottomRightX() + DISTANCE_BETWEEN_SHELF_AND_TRUCK;
		}
		else {
			x = sh.getTopLeftX() - DISTANCE_BETWEEN_SHELF_AND_TRUCK;
		}
		
		if (pickupPoints.size() == 0 || pickupPoints.get(pickupPoints.size() - 1).getY() >= y)
		{
			return pickupPoints.add(new PointWithName(x, y, sh.getName()));
		}
		else if (pickupPoints.get(0).getY() < y) {
			pickupPoints.add(0, new PointWithName(x, y, sh.getName()));
		} else {
			pickupPoints.add(pickupPoints.size() - 2,new PointWithName(x, y, sh.getName()));
		}
		return true;
	}
	
	public void clear() {
		pickupPoints.clear();
		setTopLeftX(0);
		setTopLeftY(0);
		setBottomRightX(0);
		setBottomRightY(0);
	}
	
	public int size() {
		return pickupPoints.size();
	}
	
	public int findName(String name) {
		for (int i = 0; i < pickupPoints.size(); i++) {
			if (pickupPoints.get(i).getName().contains(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public Point getTopLeftCornerOfRow() {
		return getTopLeft();
	}
	
	public Point getBottomRightCornerOfRow() {
		return getBottomRight();
	}
}