package Warehouse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Warehouse {
    private static Warehouse instance;
    
    private PathwayStorage pathways;
    private Vector<Point> emptyContainers;
    private Vector<Point> northDelivery;
    private Vector<Point> southDelivery;
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
//    private Vector<Vector<Double>> distanceBetweenObjectsForHeuristic; // size is equal to numberOfObjects * numberOfObjects
    
    private Vector<Integer> nearestNorthDeliveryToShelf; // size of Vector is equal to numberOfShelves
    private Vector<Integer> nearestSouthDeliveryToShelf; // size of Vector is equal to numberOfShelves
    private Vector<Integer> nearestContainerToDelivery; // size of Vector is equal to northDelivery.size() + southDelivery.size()
    
    private Warehouse() {
        pathways = new PathwayStorage(Direction.Down);
        emptyContainers = new Vector<Point>();
        northDelivery = new Vector<Point>();
        southDelivery = new Vector<Point>();
        numberOfObjects = 0;
        distanceBetweenObjects = new Vector<Vector<Double>>();
       // distanceBetweenObjectsForHeuristic = new Vector<Vector<Double>>();
        nearestNorthDeliveryToShelf = new Vector<Integer>();
        nearestSouthDeliveryToShelf = new Vector<Integer>();
        nearestContainerToDelivery = new Vector<Integer>();
    }
    
    public static Warehouse getInstance() {
        if (instance == null) {
            instance = new Warehouse();
        }
        return instance;
    }
    
    private int findNearestEmptyContainer(int indexOfObject) {
        if (indexOfObject < 0 || indexOfObject >= numberOfObjects || emptyContainers.size() < 1) {
            return -1;
        }
        int indexOfNearestContainer = pathways.getNumberOfShelves(); 
        int indexOfLastContainer = indexOfNearestContainer + emptyContainers.size() - 1;
        for(int i = indexOfNearestContainer + 1; i <= indexOfLastContainer; i++) {
            if (getRealDistance(indexOfObject, i) < getRealDistance(indexOfObject, indexOfNearestContainer)) {
                indexOfNearestContainer = i; 
            }
        }
        return indexOfNearestContainer;
    }
    
    private int findNearestNorthDelivery(int indexOfObject) {
        if (indexOfObject < 0 || indexOfObject >= numberOfObjects || northDelivery.size() < 1) {
            return -1;
        }
        int indexOfNearestDelivery = pathways.getNumberOfShelves() + emptyContainers.size(); 
        int indexOfLastNorthDelivery = indexOfNearestDelivery + northDelivery.size() - 1;
        for(int i = indexOfNearestDelivery + 1; i <= indexOfLastNorthDelivery; i++) {
            if (getRealDistance(i, indexOfObject) < getRealDistance(indexOfNearestDelivery, indexOfObject)) {
                indexOfNearestDelivery = i;
            }
        }
        return indexOfNearestDelivery;
    }
    
    private int findNearestSouthDelivery(int indexOfObject) {
        if (indexOfObject < 0 || indexOfObject >= numberOfObjects || southDelivery.size() < 1) {
            return -1;
        }
        int indexOfNearestDelivery = pathways.getNumberOfShelves() + emptyContainers.size() + northDelivery.size(); 
        int indexOfLastSouthDelivery = indexOfNearestDelivery + southDelivery.size() - 1;
        for(int i = indexOfNearestDelivery + 1; i <= indexOfLastSouthDelivery; i++) {
            if (getRealDistance(i, indexOfObject) < getRealDistance(indexOfNearestDelivery, indexOfObject)) {
                indexOfNearestDelivery = i;
            }
        }
        return indexOfNearestDelivery;
    }
    
    public int getNearestEmptyContainer(int indexOfObject) {
        int deliveryFirstIndex = pathways.getNumberOfShelves() + emptyContainers.size();
        if (indexOfObject < numberOfObjects && indexOfObject >= deliveryFirstIndex) {
            return nearestContainerToDelivery.get(indexOfObject - deliveryFirstIndex);
        }
        return findNearestEmptyContainer(indexOfObject);
    }
    
    public int getNearestNorthDelivery(int indexOfObject) {
        if (indexOfObject >= 0 && indexOfObject < pathways.getNumberOfShelves()) {
            return nearestNorthDeliveryToShelf.get(indexOfObject);
        }
        return findNearestNorthDelivery(indexOfObject);
    }
    
    public int getNearestSouthDelivery(int indexOfObject) {
        if (indexOfObject >= 0 && indexOfObject < pathways.getNumberOfShelves()) {
            return nearestSouthDeliveryToShelf.get(indexOfObject);
        }
        return findNearestSouthDelivery(indexOfObject);
    }
    
    private void recountNearestContainers() {
        int sizeOfVector = northDelivery.size() + southDelivery.size();
        int firstDeliveryIndex = pathways.getNumberOfShelves() + emptyContainers.size();
        for (int i = 0; i < sizeOfVector; i++) {
            nearestContainerToDelivery.add(findNearestEmptyContainer(firstDeliveryIndex + i));
        }
    }
    
    private void recountNearestDeliveries() {
        int sizeOfVector = pathways.getNumberOfShelves();
        for (int i = 0; i < sizeOfVector; i++) {
            nearestNorthDeliveryToShelf.add(findNearestNorthDelivery(i));
            nearestSouthDeliveryToShelf.add(findNearestSouthDelivery(i));
        }
    }
    
    private void computeDistancesBetweenObjects() {
        while (distanceBetweenObjects.size() < numberOfObjects) {
            distanceBetweenObjects.add(new Vector<Double>());
        }
        
        for (int fromObject = 0; fromObject < numberOfObjects; fromObject++) {
            PointWithIndex from = getPointWithPathwayIndex(fromObject);
            for (int toObject = 0; toObject <= fromObject; toObject++) {
                PointWithIndex to = getPointWithPathwayIndex(toObject);
                distanceBetweenObjects.get(fromObject).add(RouteDistance.shortestDistance(from, from.getIndex(), to, to.getIndex(), pathways));
            }
        }
    }
/*    
    private void computeDistancesBetweenObjectsForHeuristic() {
        int numberOfShelves = pathways.getNumberOfShelves(); 
        int emptyContSize = numberOfShelves + emptyContainers.size();
        
        while (distanceBetweenObjectsForHeuristic.size() < numberOfObjects) {
            distanceBetweenObjectsForHeuristic.add(new Vector<Double>());
        }
        
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
 */   
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
            return new PointWithIndex(emptyContainers.get(index), pathways.getPathwayIndexByPoint(emptyContainers.get(index)));
        }
        index -= emptyContainers.size();
        if (index < northDelivery.size()) {
            return new PointWithIndex(northDelivery.get(index), pathways.getPathwayIndexByPoint(northDelivery.get(index)));
        }
        index -= northDelivery.size();
        return new PointWithIndex(southDelivery.get(index), pathways.getPathwayIndexByPoint(southDelivery.get(index)));
    }
    
    public double getRealDistance(int from, int to) {
        if (from < to) {
            int tmp = from;
            from = to;
            to = tmp;
        }
        if (from < 0 || to < 0 || from >= distanceBetweenObjects.size() || to >= distanceBetweenObjects.get(from).size()) {
            return Double.POSITIVE_INFINITY;
        }
        return distanceBetweenObjects.get(from).get(to);
    }
    
/*    public double getDistanceForHeuristic(int from, int to) {
        if (from < 0 || to < 0 || from >= distanceBetweenObjectsForHeuristic.size() || to >= distanceBetweenObjectsForHeuristic.get(from).size()) {
            return Double.POSITIVE_INFINITY;
        }
        return distanceBetweenObjectsForHeuristic.get(from).get(to);
    }
*/    
    public int getIndexOfShelf(String name) {
        return pathways.getShelfIndex(name);
    }
    
    public int getIndexOfFirstDelivery() {
        return pathways.getNumberOfShelves() + emptyContainers.size();
    }
    
    public int getIndexOfLastDelivery() {
        return getIndexOfFirstDelivery() + northDelivery.size() + southDelivery.size() - 1; 
    }
    
    private void recountNumberOfShelvesAndObjects() {
        pathways.recountNumberOfShelves();
        numberOfObjects = pathways.getNumberOfShelves() + northDelivery.size() + southDelivery.size() + emptyContainers.size();
    }
    
    private void clearAll() {
        try {
            pathways.clear();
            northDelivery.clear();
            southDelivery.clear();
            emptyContainers.clear();
            for (int i = 0; i < distanceBetweenObjects.size(); i++) {
                distanceBetweenObjects.get(i).clear();
            }
            distanceBetweenObjects.clear();
            nearestContainerToDelivery.clear();
            nearestNorthDeliveryToShelf.clear();
            nearestSouthDeliveryToShelf.clear();
        }
        catch (NullPointerException npe) {
            
        }
    }
    
    private void readExpedition(BufferedReader br, Expedition exp) throws IOException {
        String line = br.readLine();
        String [] elements = line.split("[\";]");
        while (elements.length <= 1 || !(elements[0].startsWith("(") && elements[1].endsWith(")"))) {
            line = br.readLine();
            if (line == null) { throw new IOException("Wrong format of Warehouse file");}
            elements = line.replace("\"","").split(";");
        }
        for (int i = 0; i < (elements.length - 1) && elements[i].startsWith("(") && elements[i + 1].endsWith(")"); i+=2) {
            String x = elements[i].replace(",", ".").substring(1);
            String y = elements[i+1].replace(",", ".");
            y = y.substring(0, y.length() - 1);
            Point tmp = new Point(x, y);
            if (exp == Expedition.North) {
                northDelivery.add(tmp);
            }
            else  {
                southDelivery.add(tmp);
            }
        }
    }
    
    private void readShelfs(BufferedReader br, int numberOfRows) throws IOException {
        String line = br.readLine();
        if (line == null) {throw new IOException("Wrong format of Warehouse file");}
        String [] elements = line.split(";");
        if (elements.length < numberOfRows ) {throw new IOException("Wrong format of Warehouse file");}
        
        line = br.readLine();
        if (line == null) {throw new IOException("Wrong format of Warehouse file");}
        elements = line.split(";");
        if (elements.length < (numberOfRows + 1)) {throw new IOException("Wrong format of Warehouse file");}
        int [] numberOfShelfsInRow = new int [numberOfRows];
        int i = 0;
        for (i = 0; i < numberOfRows; i++) {
            numberOfShelfsInRow[i] = Integer.parseInt(elements[i+1]);
        }
        
        line = br.readLine();
        if (line == null) {throw new IOException("Wrong format of Warehouse file");}
        elements = line.replace("\"","").split(";");
        if (elements.length < (2*numberOfRows + 1)) {throw new IOException("Wrong format of Warehouse file");}
        Point [] topLeft = new Point [numberOfRows];
        for (i = 0; i < numberOfRows && elements[2*i+1].startsWith("(") && elements[2*(i + 1)].endsWith(")"); i++) {
            String x = elements[2*i+1].replace(",", ".").substring(1);
            String y = elements[2*(i + 1)].replace(",", ".");
            y = y.substring(0, y.length() - 1);
            topLeft[i] = new Point(x, y);
        }
        if (i != numberOfRows) {throw new IOException("Wrong format of Warehouse file");}
        
        line = br.readLine();
        if (line == null) {throw new IOException("Wrong format of Warehouse file");}
        elements = line.replace("\"","").split(";");
        if (elements.length < (2*numberOfRows + 1)) {throw new IOException("Wrong format of Warehouse file");}
        Point [] bottomRight = new Point [numberOfRows];
        for (i = 0; i < numberOfRows && elements[2*i+1].startsWith("(") && elements[2*(i + 1)].endsWith(")"); i++) {
            String x = elements[2*i+1].replace(',', '.').substring(1);
            String y = elements[2*(i + 1)].replace(',', '.');
            y = y.substring(0, y.length() - 1);
            bottomRight[i] = new Point(x, y);
        }
        if (i != numberOfRows) {throw new IOException("Wrong format of Warehouse file");}
        double [] height = new double [numberOfRows];
        double [] width = new double [numberOfRows];
        for (i = 0; i < numberOfRows; i++) {
            if (topLeft[i].getY() < bottomRight[i].getY() || topLeft[i].getX() > bottomRight[i].getX()) {
                throw new IOException("Check top-left and bottom-right points");
            }
            height[i] = (topLeft[i].getY() - bottomRight[i].getY()) / numberOfShelfsInRow[i];
            width[i] = (bottomRight[i].getX() - topLeft[i].getX());
        }
        line = br.readLine();
        if (line == null) {throw new IOException("Wrong format of Warehouse file");}
        int maxNumberOfShelfsInRow = numberOfShelfsInRow[0];
        for (i = 1; i < numberOfRows; i++) {
            if (maxNumberOfShelfsInRow < numberOfShelfsInRow[i]) {
                maxNumberOfShelfsInRow = numberOfShelfsInRow[i];
            }
        }
        
        while (maxNumberOfShelfsInRow > 0) {
            line = br.readLine();
            if (line == null) {throw new IOException("Wrong format of Warehouse file");}
            elements = line.split(";");
            i = 1;
            for (int j = 0; j < numberOfRows; j++) {
                if (numberOfShelfsInRow[j] > 0) {
                    if (i >= elements.length) {throw new IOException("Wrong format of Warehouse file");}
                    Shelf sh = new Shelf(elements[i], topLeft[j].getX(), bottomRight[j].getY() + (height[j] * numberOfShelfsInRow[j]), width[j], height[j]);
                    pathways.add(j, sh);
                    if (sh.getName().toUpperCase().contains(EmptyContainer.NAME)) {
                        Point p = new Point((sh.getBottomRightX() - sh.getTopLeftX())/2, (sh.getTopLeftY() - sh.getBottomRightY())/2);
                        emptyContainers.add(p);
                    }
                    numberOfShelfsInRow[j]--;
                }
                i++;
            }
            
            maxNumberOfShelfsInRow--;
        }
    }
    
    public void readFromFile(String filename) {
        clearAll();
        String line = "";
        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            readExpedition(br, Expedition.North);
            readExpedition(br, Expedition.South);
            line = br.readLine();
            if (line == null) { throw new IOException("Wrong format of Warehouse file"); }
            String [] elements = line.split(";");
            if (elements.length < 2) { throw new IOException("Wrong format of Warehouse file"); }
            int numberOfRows = Integer.parseInt(elements[1]); 
            readShelfs(br, numberOfRows);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
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
        
        recountNumberOfShelvesAndObjects();
        computeDistancesBetweenObjects();
        recountNearestDeliveries();
        recountNearestContainers();
    }
       
    public void sortIndexesForHeuristic (Vector<OrderItem> orderItems) {
        pathways.sortIndexesForHeuristic(orderItems);
    }
}