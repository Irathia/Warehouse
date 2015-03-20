package Warehouse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Warehouse extends InputParameters{
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
    private Vector<Integer> nearestContainerToShelf; // size of Vector is equal to numberOfShelves
    
    private Warehouse() {
        super();
        pathways = new PathwayStorage();
        emptyContainers = new Vector<Point>();
        northDelivery = new Vector<Point>();
        southDelivery = new Vector<Point>();
        numberOfObjects = 0;
        distanceBetweenObjects = new Vector<Vector<Double>>();
        nearestNorthDeliveryToShelf = new Vector<Integer>();
        nearestSouthDeliveryToShelf = new Vector<Integer>();
        nearestContainerToShelf = new Vector<Integer>();
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
        if (indexOfObject >= 0 && indexOfObject < pathways.getNumberOfShelves()) {
            return nearestContainerToShelf.get(indexOfObject);
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
        for (int i = 0; i < pathways.getNumberOfShelves(); i++) {
            nearestContainerToShelf.add(findNearestEmptyContainer(i));
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
    
    public String getNameByIndex(int index) {
        if (index < 0 || index >= numberOfObjects) {
            return null;
        }
        if (index < pathways.getNumberOfShelves()) {
            return pathways.getPickupPointOfShelf(index).getName();
        }
        index -= pathways.getNumberOfShelves();
        if (index < emptyContainers.size()) {
            return "(" + emptyContainers.get(index).getX() + ";" + emptyContainers.get(index).getY() + ")";
        }
        index -= emptyContainers.size();
        if (index < northDelivery.size()) {
            return "(" + northDelivery.get(index).getX() + ";" + northDelivery.get(index).getY() + ")";
        }
        index -= northDelivery.size();
        return "(" + southDelivery.get(index).getX() + ";" + southDelivery.get(index).getY() + ")";
    }
    
    public int getIndexOfShelf(String name) {
        return pathways.getShelfIndexByFullName(name);
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
            nearestContainerToShelf.clear();
            nearestNorthDeliveryToShelf.clear();
            nearestSouthDeliveryToShelf.clear();
        }
        catch (NullPointerException npe) {
            
        }
    }
    
    private int readExpedition(BufferedReader br, Expedition exp, String filename, int lineCounter) throws Exception {
        String line = br.readLine();
        lineCounter++;
        String [] elements = line.split("[\";]");
        while (elements.length <= 1 || !(elements[0].startsWith("(") && elements[1].endsWith(")"))) {
            line = br.readLine();
            lineCounter++;
            if (line == null) { throw new Exception(I18n.wrongFormatOfFile(filename));}
            elements = line.replace("\"","").split(";");
        }
        for (int i = 0; i < (elements.length - 1) && elements[i].startsWith("(") && elements[i + 1].endsWith(")"); i+=2) {
            String x = elements[i].replace(",", ".").substring(1);
            String y = elements[i+1].replace(",", ".");
            y = y.substring(0, y.length() - 1);
            Point tmp; 
            try {
                tmp = new Point(x, y);
            }
            catch (Exception e) {
                throw new Exception(I18n.wrongFormatOfFile(filename) + "\n" + I18n.WRONG_COORDINATES + I18n.wrongCell(lineCounter, i+1, filename));
            }
            if (exp == Expedition.North) {
                northDelivery.add(tmp);
            }
            else  {
                southDelivery.add(tmp);
            }
        }
        return lineCounter;
    }
    
    private void readShelfs(BufferedReader br, int numberOfRows, String filename, int lineCounter) throws Exception {
        String line = br.readLine();
        lineCounter++;
        if (line == null) {throw new Exception(I18n.wrongFormatOfFile(filename));}
        String [] elements = line.split(";");
        if (elements.length < numberOfRows ) {throw new Exception(I18n.wrongFormatOfFile(filename));}
        
        line = br.readLine();
        lineCounter++;
        if (line == null) {throw new Exception(I18n.wrongFormatOfFile(filename));}
        elements = line.split(";");
        if (elements.length < (numberOfRows + 1)) {throw new Exception(I18n.wrongFormatOfFile(filename));}
        int [] numberOfShelfsInRow = new int [numberOfRows];
        int i = 0;
        for (i = 0; i < numberOfRows; i++) {
            try {
                numberOfShelfsInRow[i] = Integer.parseInt(elements[i+1]);
            }
            catch (Exception ex) {
                throw new Exception(I18n.wrongFormatOfFile(filename) + "\n" + I18n.wrongNumberOfShelfs(i));
            }
        }
        
        line = br.readLine();
        lineCounter++;
        if (line == null) {throw new Exception(I18n.wrongFormatOfFile(filename));}
        elements = line.replace("\"","").split(";");
        if (elements.length < (2*numberOfRows + 1)) {throw new Exception(I18n.wrongFormatOfFile(filename));}
        Point [] topLeft = new Point [numberOfRows];
        for (i = 0; i < numberOfRows && elements[2*i+1].startsWith("(") && elements[2*(i + 1)].endsWith(")"); i++) {
            String x = elements[2*i+1].replace(",", ".").substring(1);
            String y = elements[2*(i + 1)].replace(",", ".");
            y = y.substring(0, y.length() - 1);
            try {
                topLeft[i] = new Point(x, y);
            }
            catch (Exception ex) {
                throw new Exception(I18n.WRONG_COORDINATES + ". " + I18n.wrongCell(lineCounter, i+1, filename));
            }
        }
        if (i != numberOfRows) {throw new Exception(I18n.wrongFormatOfFile(filename));}
        
        line = br.readLine();
        lineCounter++;
        if (line == null) {throw new Exception(I18n.wrongFormatOfFile(filename));}
        elements = line.replace("\"","").split(";");
        if (elements.length < (2*numberOfRows + 1)) {throw new Exception(I18n.wrongFormatOfFile(filename));}
        Point [] bottomRight = new Point [numberOfRows];
        for (i = 0; i < numberOfRows && elements[2*i+1].startsWith("(") && elements[2*(i + 1)].endsWith(")"); i++) {
            String x = elements[2*i+1].replace(',', '.').substring(1);
            String y = elements[2*(i + 1)].replace(',', '.');
            y = y.substring(0, y.length() - 1);
            try {
                bottomRight[i] = new Point(x, y);
            }
            catch (Exception ex) {
                throw new Exception(I18n.WRONG_COORDINATES + ". " + I18n.wrongCell(lineCounter, i+1, filename));
            }
        }
        if (i != numberOfRows) {throw new Exception(I18n.wrongFormatOfFile(filename));}
        double [] height = new double [numberOfRows];
        double [] width = new double [numberOfRows];
        for (i = 0; i < numberOfRows; i++) {
            if (topLeft[i].getY() < bottomRight[i].getY() || topLeft[i].getX() > bottomRight[i].getX()) {
                throw new Exception(I18n.wrongTop_LeftAndBottom_RightCoordinates(i+1));
            }
            height[i] = (topLeft[i].getY() - bottomRight[i].getY()) / numberOfShelfsInRow[i];
            width[i] = (bottomRight[i].getX() - topLeft[i].getX());
        }
        for (i = 0; i < (numberOfRows - 1); i++) {
            if (bottomRight[i].getX() > topLeft[i+1].getX()) {
                throw new Exception(I18n.wrongCoordinatesOfNeighborRows(i+1));
            }
        }
        int rowShift = 0;
        if (numberOfRows > 1 && bottomRight[0].getX() == topLeft[1].getX()) {
            rowShift = 1;
        }
        line = br.readLine();
        lineCounter++;
        if (line == null) {throw new Exception(I18n.wrongFormatOfFile(filename));}
        int maxNumberOfShelfsInRow = numberOfShelfsInRow[0];
        for (i = 1; i < numberOfRows; i++) {
            if (maxNumberOfShelfsInRow < numberOfShelfsInRow[i]) {
                maxNumberOfShelfsInRow = numberOfShelfsInRow[i];
            }
        }
        
        while (maxNumberOfShelfsInRow > 0) {
            line = br.readLine();
            lineCounter++;
            if (line == null) {
                throw new Exception(I18n.wrongFormatOfFile(filename));
            }
            elements = line.split(";");
            i = 1;
            for (int j = 0; j < numberOfRows; j++) {
                if (numberOfShelfsInRow[j] > 0) {
                    if (i >= elements.length) {
                        throw new Exception(I18n.wrongNumberOfShelfs(i, filename));
                    }
                    if (elements[i].equals("")) {
                        throw new Exception(I18n.wrongNumberOfShelfs(i, filename));
                    }
                    PointWithIndex tmpPoint = pathways.getPickupPointOfShelfWithRowIndexByPartOfName(elements[i]);
                    if (tmpPoint != null) {
                        if (tmpPoint.getIndex() != j + rowShift) {
                            throw new Exception(I18n.wrongPartsOfCell(tmpPoint.getIndex(), j + rowShift, elements[i], filename));
                        }
                        int copy = pathways.getShelfIndexByFullName(elements[i]);
                        if (copy != -1) {
                            throw new Exception(I18n.repeatCell(elements[i], filename) + I18n.wrongCell(lineCounter, i+1, filename));
                        }
                        pathways.duplicatePickupPointOfShelf(tmpPoint, tmpPoint.getIndex(), elements[i]);
                        i++;
                        continue;
                    }
                    Shelf sh = new Shelf(elements[i], topLeft[j].getX(), bottomRight[j].getY() + (height[j] * numberOfShelfsInRow[j]), width[j], height[j]);
                    pathways.add(j + rowShift, sh);
                    if (sh.getName().toUpperCase().contains(I18n.EMPTY_CONTAINER)) {
                        Point p = new Point(sh.getTopLeftX() + (sh.getBottomRightX() - sh.getTopLeftX())/2, sh.getBottomRightY() + (sh.getTopLeftY() - sh.getBottomRightY())/2);
                        emptyContainers.add(p);
                    }
                    numberOfShelfsInRow[j]--;
                }
                else if (elements.length > i && numberOfShelfsInRow[j] <= 0 && !elements[i].equals("")) {
                    PointWithIndex tmpPoint = pathways.getPickupPointOfShelfWithRowIndexByPartOfName(elements[i]);
                    if (tmpPoint == null) {
                        throw new Exception(I18n.wrongNumberOfShelfs(i, filename));
                    }
                    else {
                        if (tmpPoint.getIndex() != j + rowShift) {
                            throw new Exception(I18n.wrongPartsOfCell(tmpPoint.getIndex(), j + rowShift, elements[i], filename));
                        }
                        int copy = pathways.getShelfIndexByFullName(elements[i]);
                        if (copy != -1) {
                            throw new Exception(I18n.repeatCell(elements[i], filename) + I18n.wrongCell(lineCounter, i+1, filename));
                        }
                        pathways.duplicatePickupPointOfShelf(tmpPoint, tmpPoint.getIndex(), elements[i]);
                    }
                }
                i++;
            }
            
            maxNumberOfShelfsInRow = numberOfShelfsInRow[0];
            for (i = 1; i < numberOfRows; i++) {
                if (maxNumberOfShelfsInRow < numberOfShelfsInRow[i]) {
                    maxNumberOfShelfsInRow = numberOfShelfsInRow[i];
                }
            }
        }
    }
    
    public void readTopology(String filename) throws Exception {
        clearAll();
        String line = "";
        int lineCounter = 0;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            line = br.readLine();
            lineCounter++;
            if (line == null) { throw new Exception(I18n.wrongFormatOfFile(filename)); }
            String [] elements = line.split(";");
            if (elements.length < 2) { throw new Exception(I18n.wrongFormatOfFile(filename)); }
            int direction;
            try {
                direction = Integer.parseInt(elements[1]); 
            }
            catch (Exception e) {
                throw new Exception(I18n.WRONG_WAREHOUSE_DIRECTION + I18n.errorLine(lineCounter, filename));
            }
            switch (direction) {
                case 0: 
                    pathways.setDirection(Direction.Down);
                    break;
                case 1:
                    pathways.setDirection(Direction.Up);
                    break;
                default:
                    throw new Exception(I18n.wrongFormatOfFile(filename) + I18n.WRONG_WAREHOUSE_DIRECTION);
            }
            lineCounter = readExpedition(br, Expedition.North, filename, lineCounter);
            lineCounter = readExpedition(br, Expedition.South, filename, lineCounter);
            line = br.readLine();
            lineCounter++;
            if (line == null) { throw new Exception(I18n.wrongFormatOfFile(filename)); }
            elements = line.split(";");
            if (elements.length < 2) { throw new Exception(I18n.wrongFormatOfFile(filename)); }
            int numberOfRows; 
            try {
                numberOfRows = Integer.parseInt(elements[1]); 
            }
            catch (Exception e) {
                throw new Exception(I18n.wrongFormatOfFile(filename) + I18n.errorLine(lineCounter, filename));
            }
            readShelfs(br, numberOfRows, filename, lineCounter);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
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
    
    public void readFromFile(String warehouseFilename,String parametersFilename) throws Exception {
        readParameters(parametersFilename);
        readTopology(warehouseFilename);
    }
    
    public void sortIndexesForHeuristic (Vector<OrderItem> orderItems) {
        pathways.sortIndexesForHeuristic(orderItems);
    }
}