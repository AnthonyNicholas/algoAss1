package nearestNeigh;

import java.util.*;// have imported to assist with comparators
import java.util.ArrayList;
import java.util.List;

/**
 * This class is required to be implemented.  Kd-tree implementation.
 *
 * @author Jeffrey, Youhan
 */
public class KDTreeNN implements NearestNeigh{

    private KDTree rTree = new KDTree(); // Stores root node for restaurant KDTree & is accessible to all methods
    private KDTree eTree = new KDTree(); // Stores root node for eduction KDTree & is accessible to all methods
    private KDTree hTree = new KDTree(); // Stores root node for hospital KDTree & is accessible to all methods

   /**
     * builds kd Trees.  Node we should have a seperate KDTree for each category - RESTAURANT, EDUCATION, HOSPITAL
     * @param List of points
     * @return
     */

    @Override
    public void buildIndex(List<Point> points) {
        
        List<Point> rPoints = new ArrayList<Point>(); // stores all points in restaurant category
        List<Point> ePoints = new ArrayList<Point>(); // stores all points in education category
        List<Point> hPoints = new ArrayList<Point>(); // stores all points in hospital category


        for (int i = 0; i < points.size(); i++){
            if (points[i].cat == RESTAURANT){
                rPoints.add(points[i]);
            }
            if (points[i].cat == EDUCATION){
                ePoints.add(points[i]);                
            }
            else {
                hPoints.add(points[i]);
            }
        }

        rTree.root = buildTree(rPoints, true);
        eTree.root = buildTree(ePoints, true);
        hTree.root = buildTree(hPoints, true);
        
    }
    
    // Recursive function
   
    // Recursive function
   
    public Node buildTree(List<Point> points, boolean bXDim) { 
        int median;
        List<Point> sortedPoints = new ArrayList<Point>();
        Node currParent, leftChild, rightChild;

        // First sort the list by Dimension and find median
        if (bXDim == true){
            Collections.sort(points, BXDIM);
            sortedPoints.addAll(points);
        }
        else {
            Collections.sort(points, BYDIM);
            sortedPoints.addAll(points);
        }
        if (sortedPoints.isEmpty()){
            return null;
        }
        median = findMedian(sortedPoints); 
        
        // construct a node for the median point 
        currParent = buildNode(sortedPoints.get(median)); 
        leftChild = null; 
        rightChild = null; 
        
        // Check if there is a left partition (indexing starts at 0).  If so, recursively partition it
        if (median > 0) {
            
            // Invert boolean value (effectively changing the dimension we split on next) 
            leftChild = buildTree(sortedPoints.subList(0,median-1), !bXDim); 
            
        }
        // check if there is a right partition 
        if (median < (points.size()-1)){
            // Invert the boolean value (effectively changing the dimension we split on next) 
            rightChild = buildTree(sortedPoints.subList(median+1,(points.size()-1)), !bXDim); 
        } 
        currParent.setLeft(leftChild); 
        currParent.setRight(rightChild); 

        leftChild.setParent(currParent); // Each node keeps track of its parent node.  
        rightChild.setParent(currParent);
        
        return currParent;
    }
 

    @Override
    public List<Point> search(Point searchTerm, int k) {
 
        List<Point> searchResults = new ArrayList<Point>();
        
        Node closestNode = getClosestNode(searchTerm, tree.root, null, true);
        // TODO
        return searchResults;
    }

    @Override
    public boolean addPoint(Point point) {
        // Check whether point already in tree
        if (isPointIn(point)){
            return false;
        }

        Node addNode = new Node(point); // CHECK - MAKE CONSTRUCTOR ACCEPT POINT

        // Add node to tree in right position
        addNode(addNode, tree.root, null, true); 

        return true;
    }

   //Tricky - need to work out how to handle deletion

    @Override
    public boolean deletePoint(Point point) {
        
        if (isPointIn(point) == false){
            return false;
        }

        Node deletedNode = getNodeFromTree(point, tree.root, true);

        Node currParent = deletedNode.parent;
        Node leftChild = deletedNode.leftChild;
        Node rightChild = deletedNode.rightChild;

        if (currParent.leftChild.point.equals(deletedNode.point)){ //We are deleting leftChild of parent

        } 

        else{ //We are deleting rightChild of parent

        } 

        return true; // To be implemented.

    }

   /**
     * determines whether point is in KDTree
     * @param searchPoint to be found
     * @return true if successful, false if not.
     */

    @Override
    public boolean isPointIn(Point point) {

        Node foundNode = getNodeFromTree(point, tree.root, true);
        if (foundNode == null){
            return false;
        }
        
        return true;
    }

   /**
     * retrieves node with given point from KDTree
     * @param searchPoint to be found, rootNode of KDTree, and bXDim - set to true if split on x-dimension first
     * @return if successful, node with given point.  Null if unsuccessful.
     */

    public Node getNodeFromTree(Point searchPoint, Node currNode, boolean bXDim) {

        if (searchPoint.equals(currNode.point)){
            return currNode;
        }

        //Otherwise go down either right or left branch (depending on lat/lon values)
        if (bXDim == true){
            if (searchPoint.lat > currNode.point.lat){
                currNode = currNode.rightChild;
            }
            else{
                currNode = currNode.leftChild;
            }            
        }    

        if (bXDim == false){
            if (searchPoint.lon > currNode.point.lon){
                currNode = currNode.rightChild;
            }
            else{
                currNode = currNode.leftChild;
            }            
        }    
        
        // If reach end of tree, return null

        if (currNode == null){
            return null;
        }

        // Else repeat process
        bXDim = !bXDim;
        currNode = getNodeFromTree(searchPoint, currNode, bXDim);
        return currNode;
    }

    // private List<Point> sort(List<Point> points, String bWhichDim) {

    //     //TODO
        
    //     //sort by X dimension
    //     if (bWhichDim == 'bXDim'){
    //         Collections.sort(points, BXDIM);
    //         return points;
    //     }        

    //     //sort by Y dimension
    //     else {
    //         Collections.sort(points, BYDIM);
    //         return points;
    //     }        
            
    // }

    private int findMedian(List<Point> sortedPoints) {
    
        return (int)(sortedPoints.size()/2); //if even number, will choose lower value

    }

    private Node buildNode(Point medianPoint) {
    
        Node newNode = new Node(medianPoint);
        return newNode;

    }
    
    // Comparator for Points by X dimension.

    static final Comparator<Point> BXDIM = new Comparator<Point>() {
        public int compare(Point p1, Point p2) {
            return Double.compare(p1.lat, p2.lat);
        }
    };

    // Comparator for Points by Y dimension.

    static final Comparator<Point> BYDIM = new Comparator<Point>() {
        public int compare(Point p1, Point p2) {
            return Double.compare(p1.lon, p2.lon);
        }
    };



}
