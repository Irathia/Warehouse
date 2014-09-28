
public class RouteDistance {

	private static double moveUpDistanceInOnePathway(Point from, Point to) {
		if (from.getY() > to.getY()) {
			return Double.POSITIVE_INFINITY;
		}
		return Math.abs(to.getX() - from.getX()) + Math.abs(from.getY() - to.getY()); 
	}
	
	private static double moveDownDistanceInOnePathway(Point from, Point to) {
		return moveUpDistanceInOnePathway(to, from);
	}
	
	private static double moveUpDistance(Point from, int fromPathwayIndex, Point to, int toPathwayIndex, PathwayStorage pathways) {
		if (fromPathwayIndex < 0 || toPathwayIndex < 0) {
			return Double.POSITIVE_INFINITY;
		}
		if (fromPathwayIndex == toPathwayIndex) {moveUpDistanceInOnePathway(from, to);}
		double distance = Math.abs(to.getX() - from.getX());
		double criticalHeight = pathways.getHighestRoadlock(fromPathwayIndex, toPathwayIndex);
		if (from.getY() > criticalHeight || to.getY() > criticalHeight) {
			distance += Math.abs(from.getY() - to.getY());
		} 
		else {
			distance += 2.0*(criticalHeight + ShelfRow.DISTANCE_BETWEEN_SHELF_AND_TRUCK) - from.getY() - to.getY();
		}
		return distance;
	}
	
	private static double moveDownDistance(Point from, int fromPathwayIndex, Point to, int toPathwayIndex, PathwayStorage pathways) {
		if (fromPathwayIndex < 0 || toPathwayIndex < 0) {
			return Double.POSITIVE_INFINITY;
		}
		if (fromPathwayIndex == toPathwayIndex) {moveDownDistanceInOnePathway(from, to);}
		double distance = Math.abs(to.getX() - from.getX());
		double criticalHeight = pathways.getLowestRoadlock(fromPathwayIndex, toPathwayIndex);
		if (from.getY() < criticalHeight || to.getY() < criticalHeight) {
			distance += Math.abs(to.getY() - from.getY());
		}
		else {
			distance += from.getY() + to.getY() - 2.0*(criticalHeight - ShelfRow.DISTANCE_BETWEEN_SHELF_AND_TRUCK);
		}
		return distance;
	}
	
	private static double moveUpAndDownDistance(Point from, int fromPathwayIndex, Point to, int toPathwayIndex, PathwayStorage pathways) {
		if (fromPathwayIndex < 0 || toPathwayIndex < 0) {
			return Double.POSITIVE_INFINITY;
		}
		if (fromPathwayIndex == toPathwayIndex) {
			return moveUpDistanceInOnePathway(from, to);
		}
		if (Math.abs(fromPathwayIndex-toPathwayIndex)%2 == 1) {
			return moveUpDistance(from, fromPathwayIndex, to, toPathwayIndex, pathways);
		}
		
		double distance = Math.abs(to.getX() - from.getX());
		int turningPathwayIndex;
		if (fromPathwayIndex < toPathwayIndex) {
			turningPathwayIndex = toPathwayIndex - 1;
		}
		else {
			turningPathwayIndex = toPathwayIndex + 1;
		}
		double criticalHighestHeight = pathways.getHighestRoadlock(fromPathwayIndex, turningPathwayIndex);
		double criticalLowestHeight = pathways.getLowestRoadlock(turningPathwayIndex, toPathwayIndex);
		if (from.getY() > criticalHighestHeight && to.getY() < criticalLowestHeight) {
			return distance + Math.abs(from.getY() - to.getY());
		} 
		if (from.getY() > criticalHighestHeight) {
			return distance + from.getY() + to.getX() - 2.0*(criticalLowestHeight - ShelfRow.DISTANCE_BETWEEN_SHELF_AND_TRUCK);
		}
		if (to.getY() < criticalLowestHeight) {
			return distance + 2.0*(criticalHighestHeight + ShelfRow.DISTANCE_BETWEEN_SHELF_AND_TRUCK) - from.getY() - to.getY();
		}
		distance += to.getY() - from.getY() + 2.0*(criticalHighestHeight - criticalLowestHeight) + 4.0*ShelfRow.DISTANCE_BETWEEN_SHELF_AND_TRUCK;
		return distance;
	}
	
	private static double moveDownAndUpDistance(Point from, int fromPathwayIndex, Point to, int toPathwayIndex, PathwayStorage pathways) {
		if (fromPathwayIndex < 0 || toPathwayIndex < 0) {
			return Double.POSITIVE_INFINITY;
		}
		if (fromPathwayIndex == toPathwayIndex) {
			return moveDownDistanceInOnePathway(from, to);
		}
		if (Math.abs(fromPathwayIndex-toPathwayIndex)%2 == 1) {
			return moveDownDistance(from, fromPathwayIndex, to, toPathwayIndex, pathways);
		}
		double distance = Math.abs(to.getX() - from.getX());
		int turningPathwayIndex;
		if (fromPathwayIndex < toPathwayIndex) {
			turningPathwayIndex = toPathwayIndex - 1;
		}
		else {
			turningPathwayIndex = toPathwayIndex + 1;
		}
		double criticalLowestHeight = pathways.getLowestRoadlock(fromPathwayIndex, turningPathwayIndex);
		double criticalHighestHeight = pathways.getHighestRoadlock(turningPathwayIndex, toPathwayIndex);
		if (to.getY() > criticalHighestHeight && from.getY() < criticalLowestHeight) {
			return distance + Math.abs(from.getY() - to.getY());
		} 
		if (from.getY() < criticalLowestHeight) {
			return distance + 2.0*(criticalHighestHeight + ShelfRow.DISTANCE_BETWEEN_SHELF_AND_TRUCK) - from.getY() - to.getY();
		}
		if (to.getY() > criticalHighestHeight) {
			return distance + from.getY() + to.getX() - 2.0*(criticalLowestHeight - ShelfRow.DISTANCE_BETWEEN_SHELF_AND_TRUCK);
		}
		distance += from.getY() - to.getY() + 2.0*(criticalHighestHeight - criticalLowestHeight) + 4.0*ShelfRow.DISTANCE_BETWEEN_SHELF_AND_TRUCK;
		return distance;
	}
	
	public static double computeDistance(Direction fromPathwayDirection, Point from, int fromPathwayIndex, Point to, int toPathwayIndex, PathwayStorage pathways) {
		if (fromPathwayDirection == Direction.Down) {
			return moveDownAndUpDistance(from, fromPathwayIndex, to, toPathwayIndex, pathways);
		}
		return moveUpAndDownDistance(from, fromPathwayIndex, to, toPathwayIndex, pathways);
	}
	
	public static double shortestDistance(Point from, int fromPathwayIndex, Point to, int toPathwayIndex, PathwayStorage pathways) {
		return Math.min(moveDownDistance(from, fromPathwayIndex, to, toPathwayIndex, pathways), moveUpDistance(from, fromPathwayIndex, to, toPathwayIndex, pathways));
	}
}
