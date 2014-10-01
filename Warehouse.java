package Warehouse;

import java.util.Vector;

public class Warehouse {
    private static Warehouse instance;
    
    private PathwayStorage pathways;
    private Vector<PointWithIndex> emptyContainers;
    private Vector<PointWithIndex> northDelivery;
    private Vector<PointWithIndex> southDelivery;
    int numberOfObjects;
    
    
    /* [0, (numberOfShelves-1)] - indexes of shelves
     * [numberOfShelves, (numberOfShelves + emptyContainers.size() - 1)] - indexes of containers
     * [...,...+ northDelivery.size() - 1] - indexes of north deliveries
     * [...,...+ southDelivery.size() - 1] - indexes of south deliveries
     *  */
    private Vector<Vector<Double>> distanceBetweenObjects; // size is equal to numberOfObjects * numberOfObjects
    
    /* [0, (numberOfShelves-1)] - indexes of shelves
     * [numberOfShelves, (numberOfShelves + emptyContainers.size() - 1)] - indexes of containers
     * [...,...+ northDelivery.size() - 1] - indexes of north deliveries
     * [...,...+ southDelivery.size() - 1] - indexes of south deliveries
     *  */
    private Vector<Vector<Double>> distanceBetweenObjectsForHeuristic; // size is equal to numberOfObjects * numberOfObjects
    
    private Vector<Integer> nearestNorthDeliveryToShelf; // size of Vector is equal to numberOfShelves
    private Vector<Integer> nearestSouthDeliveryToShelf; // size of Vector is equal to numberOfShelves
    private Vector<Integer> nearestContainerToDelivery; // size of Vector is equal to northDelivery.size() + southDelivery.size()
    
    private Warehouse() {}
    
    public static Warehouse getInstance() {
        if (instance == null) {
            instance = new Warehouse();
        }
        return instance;
    }
    
    private int getNearestEmptyContainer(int indexOfObject) {
        if (indexOfObject < 0 || indexOfObject >= numberOfObjects || emptyContainers.size() < 1) {
            return -1;
        }
        int indexOfNearestContainer = pathways.getNumberOfShelves(); 
        int indexOfLastContainer = indexOfNearestContainer + emptyContainers.size() - 1;
        for(int i = indexOfNearestContainer + 1; i <= indexOfLastContainer; i++) {
            if (distanceBetweenObjectsForHeuristic.get(indexOfObject).get(i) < distanceBetweenObjectsForHeuristic.get(indexOfObject).get(indexOfNearestContainer)) {
                indexOfNearestContainer = i; 
            }
        }
        return indexOfNearestContainer;
    }
    
    private int getNearestNorthDelivery(int indexOfObject) {
        if (indexOfObject < 0 || indexOfObject >= numberOfObjects || northDelivery.size() < 1) {
            return -1;
        }
        int indexOfNearestDelivery = pathways.getNumberOfShelves() + emptyContainers.size(); 
        int indexOfLastNorthDelivery = indexOfNearestDelivery + northDelivery.size() - 1;
        for(int i = indexOfNearestDelivery + 1; i <= indexOfLastNorthDelivery; i++) {
            if (distanceBetweenObjectsForHeuristic.get(indexOfObject).get(i) < distanceBetweenObjectsForHeuristic.get(indexOfObject).get(indexOfNearestDelivery)) {
                indexOfNearestDelivery = i;
            }
        }
        return indexOfNearestDelivery;
    }
    
    private int getNearestSouthDelivery(int indexOfObject) {
        if (indexOfObject < 0 || indexOfObject >= numberOfObjects || southDelivery.size() < 1) {
            return -1;
        }
        int indexOfNearestDelivery = pathways.getNumberOfShelves() + emptyContainers.size() + northDelivery.size(); 
        int indexOfLastSouthDelivery = indexOfNearestDelivery + southDelivery.size() - 1;
        for(int i = indexOfNearestDelivery + 1; i <= indexOfLastSouthDelivery; i++) {
            if (distanceBetweenObjectsForHeuristic.get(indexOfObject).get(i) < distanceBetweenObjectsForHeuristic.get(indexOfObject).get(indexOfNearestDelivery)) {
                indexOfNearestDelivery = i;
            }
        }
        return indexOfNearestDelivery;
    }
    
    private void recountNearestContainers() {
        nearestContainerToDelivery.clear();
        int sizeOfVector = northDelivery.size() + southDelivery.size();
        int firstDeliveryIndex = pathways.getNumberOfShelves() + emptyContainers.size();
        for (int i = 0; i < sizeOfVector; i++) {
            nearestContainerToDelivery.add(i, getNearestEmptyContainer(firstDeliveryIndex + i));
        }
    }
    
    private void recountNearestDeliveries() {
        nearestNorthDeliveryToShelf.clear();
        nearestSouthDeliveryToShelf.clear();
        int sizeOfVector = pathways.getNumberOfShelves();
        for (int i = 0; i < sizeOfVector; i++) {
            nearestNorthDeliveryToShelf.add(i, getNearestNorthDelivery(i));
            nearestSouthDeliveryToShelf.add(i, getNearestSouthDelivery(i));
        }
    }
    
    private void computeDistancesBetweenObjects() {
        int numberOfShelves = pathways.getNumberOfShelves(); 
        int emptyContSize = numberOfShelves + emptyContainers.size();
        
        for (int fromObject = 0; fromObject < numberOfObjects; fromObject++) {
            PointWithIndex from = getPointWithPathwayIndex(fromObject);
            for (int toObject = 0; toObject <= fromObject; toObject++) {
                if (toObject >= numberOfShelves  && toObject < emptyContSize) { continue; }
                PointWithIndex to = getPointWithPathwayIndex(fromObject);
                distanceBetweenObjects.get(fromObject).set(toObject, RouteDistance.shortestDistance(from, from.getIndex(), to, to.getIndex(), pathways));
            }
        }
    }
    
    private void computeDistancesBetweenObjectsForHeuristic() {
        int numberOfShelves = pathways.getNumberOfShelves(); 
        int emptyContSize = numberOfShelves + emptyContainers.size();
        
        for (int fromObject = 0; fromObject < numberOfShelves; fromObject++) {
            PointWithIndex from = getPointWithPathwayIndex(fromObject);
            Direction fromPathwayDirection = pathways.getDirectionOfPathway(from.getIndex()); 
            for (int toObject = 0; toObject < numberOfObjects; toObject++) {
                if (toObject >= numberOfShelves  && toObject < emptyContSize) { 
                    distanceBetweenObjectsForHeuristic.get(fromObject).set(toObject, Double.POSITIVE_INFINITY);
                    continue;
                }
                PointWithIndex to = getPointWithPathwayIndex(fromObject);
                if (toObject < pathways.getNumberOfShelves()) {
                    distanceBetweenObjectsForHeuristic.get(fromObject).set(toObject, RouteDistance.computeDistance(fromPathwayDirection, from, from.getIndex(), to, to.getIndex(), pathways));
                }
                else {
                    distanceBetweenObjectsForHeuristic.get(fromObject).set(toObject, RouteDistance.shortestDistance(from, from.getIndex(), to, to.getIndex(), pathways));
                }
            }
        }
    
        for (int fromObject = numberOfShelves; fromObject < numberOfObjects; fromObject++) {
            PointWithIndex from = getPointWithPathwayIndex(fromObject);
            for (int pathwayIndex = 0; pathwayIndex < pathways.getNumberOfPathways(); pathwayIndex++) {
                int nearestShelfIndex = pathways.getIndexOfNearestShelfBasedOnDirection(pathwayIndex, from);
                PointWithIndex to = getPointWithPathwayIndex(nearestShelfIndex);
                double minimumDistance = RouteDistance.shortestDistance(from, from.getIndex(), to, to.getIndex(), pathways);
                distanceBetweenObjectsForHeuristic.get(fromObject).set(nearestShelfIndex, minimumDistance);
                int lastIndex = pathways.getLastShelfIndexInPathway(pathwayIndex);
                for (int toObject = pathways.getFirstShelfIndexInPathway(pathwayIndex); toObject <= lastIndex; toObject++) {
                    if (toObject == nearestShelfIndex) { continue; }
                    distanceBetweenObjectsForHeuristic.get(fromObject).set(toObject, minimumDistance + distanceBetweenObjectsForHeuristic.get(toObject).get(fromObject));
                }
            }
        }
        
        for (int fromObject = emptyContSize; fromObject < numberOfObjects; fromObject++) {
            PointWithIndex from = getPointWithPathwayIndex(fromObject);
            for (int toObject = numberOfShelves; toObject < emptyContSize; toObject++) {
                PointWithIndex to = getPointWithPathwayIndex(fromObject);
                distanceBetweenObjectsForHeuristic.get(fromObject).set(toObject, RouteDistance.shortestDistance(from, from.getIndex(), to, to.getIndex(), pathways));
            }
        }
    }
    
    private PointWithIndex getPointWithPathwayIndex(int index) {
        if (index < 0 || index >= numberOfObjects) {
            return new PointWithIndex(-1);
        }
        if (index < pathways.getNumberOfShelves()) {
            Point p = pathways.getPickupPointOfShelf(index);
            int pathwayIndex = pathways.getPathwayIndexByShelfIndex(index);
            return new PointWithIndex(p.getX(), p.getY(), pathwayIndex);
        }
        index -= pathways.getNumberOfShelves();
        if (index < emptyContainers.size()) {
            return emptyContainers.get(index);
        }
        index -= emptyContainers.size();
        if (index < northDelivery.size()) {
            return northDelivery.get(index);
        }
        index -= northDelivery.size();
        return southDelivery.get(index);
    }
    
    public double getRealDistance(int from, int to) {
        if (from > to) {
            int tmp = from;
            from = to;
            to = tmp;
        }
        if (from < 0 || to < 0 || from >= distanceBetweenObjects.size() || to >= distanceBetweenObjects.get(from).size()) {
            return Double.POSITIVE_INFINITY;
        }
        return distanceBetweenObjects.get(from).get(to);
    }
    
    public double getDistanceForHeuristic(int from, int to) {
        if (from < 0 || to < 0 || from >= distanceBetweenObjectsForHeuristic.size() || to >= distanceBetweenObjectsForHeuristic.get(from).size()) {
            return Double.POSITIVE_INFINITY;
        }
        return distanceBetweenObjectsForHeuristic.get(from).get(to);
    }
    
    public int getIndexOfShelf(String name) {
        return pathways.getShelfIndex(name);
    }
    
    private void recountNumberOfShelvesAndObjects() {
        pathways.recountNumberOfShelves();
        numberOfObjects = pathways.getNumberOfShelves() + northDelivery.size() + southDelivery.size() + emptyContainers.size();
    }
    
    private void clearAll() {
        pathways.clear();
        northDelivery.clear();
        southDelivery.clear();
        emptyContainers.clear();
        for (int i = 0; i < distanceBetweenObjects.size(); i++) {
            distanceBetweenObjects.get(i).clear();
        }
        distanceBetweenObjects.clear();
    }
    
    public void readFromFile(String filename) {
        clearAll();
        //........
        
        recountNumberOfShelvesAndObjects();
        computeDistancesBetweenObjects();
        computeDistancesBetweenObjectsForHeuristic();
        recountNearestDeliveries();
        recountNearestContainers();
    }
    
    public void sortIndexesForHeuristic (Vector<OrderItem> orderItems) {
        pathways.sortIndexesForHeuristic(orderItems);
    }
}