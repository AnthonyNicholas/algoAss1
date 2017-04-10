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
            if (points.get(i).cat == Category.RESTAURANT){
                rPoints.add(points.get(i));
            }
            if (points.get(i).cat == Category.EDUCATION){
                ePoints.add(points.get(i));                
            }
            if (points.get(i).cat == Category.HOSPITAL){
                hPoints.add(points.get(i));
            }
        }

        rTree.root = buildTree(rPoints, true, 0);
        // rTree.printTree(rTree.root, 0);
        // System.out.println(rTree.root.rightChild.point);
        // eTree.root = buildTree(ePoints, true, 0);
        // hTree.root = buildTree(hPoints, true, 0);

    }
    
    
    /**
     * recursive function that builds KDTree.  
     * @param List of points
     * @return Node that is the root of the tree
     */

    public Node buildTree(List<Point> points, boolean bXDim, int level) { 
        int median = 0;
        List<Point> sortedPoints = new ArrayList<Point>();
        Node currNode = new Node(); 
        Node leftChild = null; 
        Node rightChild = null;
        level += 1;

        // First sort the list by Dimension and find median
        if (bXDim == true){
            Collections.sort(points, BXDIM);
            sortedPoints.addAll(points);
        }
        else {
            Collections.sort(points, BYDIM);
            sortedPoints.addAll(points);
        }

        // Check number of remaining points. If none or one, stop recursing.
        if (sortedPoints.isEmpty()){
            System.out.println("NULL");
            return null; 
        }
        // if (sortedPoints.size() == 1){
        //     currNode.point = sortedPoints.get(0);
        //     System.out.println("LEVEL:" + level + " POINT: " + currNode.point);
        //     return currNode;
        // }

        median = findMedian(sortedPoints); 
        
        // construct a node for the median point 
        currNode = buildNode(sortedPoints.get(median)); 
        
        if (currNode != null){
            System.out.println("LEVEL:" + level + " POINT: " + currNode.point + " sortedPoints.size(): " + sortedPoints.size());
        }

        // Check if there is a left partition (indexing starts at 0).  If so, recursively partition it
        if (median > 0) {

            System.out.print("LEFTCHILD: ");

            // Invert boolean value (effectively changing the dimension we split on next) 
            leftChild = buildTree(sortedPoints.subList(0,median), !bXDim, level);
            currNode.setLeft(leftChild);
            System.out.println("Point " + currNode.point.id + " leftchild is " + currNode.leftChild.point.id);
        }
        // check if there is a right partition 
        if (median < (sortedPoints.size()-1)){
            
            System.out.print("RIGHTCHILD: ");

            // Invert the boolean value (effectively changing the dimension we split on next) 
            rightChild = buildTree(sortedPoints.subList(median+1,sortedPoints.size()), !bXDim, level); 
            currNode.setRight(rightChild);
            System.out.println("Point " + currNode.point.id + " rightchild is " + currNode.rightChild.point.id);

        }        
         
        if (leftChild != null){
            leftChild.setParent(currNode); // Each node keeps track of its parent node.  
        }
        if (rightChild != null){
            rightChild.setParent(currNode);
        }
        
        // if (currNode.parent != null){
        //     System.out.println("PARENT: " + currNode.parent.point.id);
        // }

        return currNode;
    }
 

    @Override
    public List<Point> search(Point searchTerm, int k) {
 
        List<Point> searchResults = new ArrayList<Point>();
        
        // Node closestNode = getClosestNode(searchTerm, tree.root, null, true);

        // return searchResults;
        
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
        addNode(addNode, rTree.root, null, true); 

        return true;
    }


    //How to delete - JChan answer - A: First, find node that 
    //holds the point being deleted.  Call this node D.  Get all children of node D.
    // Delete node D, and construct new subtree from the children of node D.  Get 
    // the root of this new subtree, and this takes the (former) position of node D.  

    @Override
    public boolean deletePoint(Point point) {
        
        List<Point> childList = new ArrayList<Point>(); //When we delete, need to build child tree again suign this list
        Node parent = null;
        KDTree tree = null;
        
        if (point.cat.equals(Category.RESTAURANT)){
            tree = rTree;
            System.out.print(tree.root.point.lat);
        }
        if (point.cat.equals(Category.EDUCATION)){
            tree = eTree;
        }
        if (point.cat.equals(Category.HOSPITAL)){
            tree = hTree;
        }
        
        // if (isPointIn(point) == false){
        //     return false;
        // }
        
        System.out.println(getNodeFromTree(point, tree.root, true).point);
        
        Node deletedNode = getNodeFromTree(point, tree.root, true);
        parent = deletedNode.parent;
        
        childList = treeToList(deletedNode); //Generate list from current child tree of deletedNode
        if (parent == null){
            tree.root = buildTree(childList, true, 0);
        }
        
        if (parent.leftChild.point.equals(deletedNode.point)){
            // join new tree as leftChild of parent
            parent.leftChild = buildTree(childList, true,0);
            parent.leftChild.parent = parent;
        }
        else{
            // join new tree as rightChild of parent
            parent.rightChild = buildTree(childList, true,0);
            parent.rightChild.parent = parent;
        }
        
        return true; // To be implemented.

    }

   /**
     * given a root, traverses all nodes under root and returns as an arrayList
     * @param searchPoint to be found
     * @return true if successful, false if not.
     */

    public List<Point> treeToList(Node root) {
        
        List<Point> list = new ArrayList<Point>();
        Node currNode = root;
        
        if (currNode.leftChild != null){
            list.add(currNode.leftChild.point);
            list.addAll(treeToList(currNode.leftChild));
        }
        if (currNode.rightChild != null){
            list.add(currNode.rightChild.point);
            list.addAll(treeToList(currNode.rightChild));
        }
        return list;
    }

   /**
     * determines whether point is in KDTree
     * @param searchPoint to be found
     * @return true if successful, false if not.
     */

    @Override
    public boolean isPointIn(Point point) {

        Node foundNode = getNodeFromTree(point, rTree.root, true);
        
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
                currNode = currNode.rightChild; // am getting rightChild = null
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
        
        // If reach end of tree without finding point, return null

        if (currNode == null){
            return null;
        }

        // Else repeat process
        bXDim = !bXDim;
        currNode = getNodeFromTree(searchPoint, currNode, bXDim);
        return currNode;
    }

   /**
     * retrieves node below which the new point (addPoint) should be added
     * @param addPoint to be added, currNode (= rootNode of KDTree initially), currParent (=null initially) and bXDim - (set to true initially)
     * @return 
     */


    public boolean addNode(Node addNode, Node currNode, Node currParent, boolean bXDim) {

        currParent = currNode;

        //Go down either right or left branch (depending on lat/lon values)

        if (bXDim == true){ // split on X dimension
            if (addNode.point.lat > currNode.point.lat){ // branch right
                currNode = currNode.rightChild;
                if (currNode == null){
                    currParent.setRight(addNode); // have found correct position - add the node
                    return true;
                }
            }
            else{
                currNode = currNode.leftChild; // branch left
                if (currNode == null){
                    currParent.setLeft(addNode);
                    return true;
                }
            }            
        }    

        if (bXDim == false){ // split on Y dimension
            if (addNode.point.lon > currNode.point.lon){ // branch right
                currNode = currNode.rightChild;
                if (currNode == null){
                    currParent.setRight(addNode);
                    return true;                  
                }
            }
            else{
                currNode = currNode.leftChild; // branch left
                if (currNode == null){
                    currParent.setLeft(addNode);
                    return true;                   
                }
            }            
        }    
        
        // Repeat process
        bXDim = !bXDim;
        return addNode(addNode, currNode, currParent, bXDim);
    }

     /**
     * retrieves closest node to Point searchTerm INCORRECT - closest node will not nec be a leaf
     * @param Point searchTerm, currNode (= rootNode of KDTree initially), and bXDim - (set to true initially)
     * @return 
     */

    public Node getClosestNode(Point searchTerm, Node currNode, Node currParent, boolean bXDim) {

        currParent = currNode;

        //Go down either right or left branch (depending on lat/lon values)

        if (bXDim == true){ // split on X dimension
            if (searchTerm.lat > currNode.point.lat){ // branch right
                currNode = currNode.rightChild;
            }
            else{
                currNode = currNode.leftChild; // branch left
            }            
        }    

        if (bXDim == false){ // split on Y dimension
            if (searchTerm.lon > currNode.point.lon){ // branch right
                currNode = currNode.rightChild;
            }
            else{
                currNode = currNode.leftChild; // branch left
            }            
        }    
        if (currNode == null){
           return currParent; // have found closest node
        }

        // Repeat process
        bXDim = !bXDim;
        return getClosestNode(searchTerm, currNode, currParent, bXDim);
    }

    private int findMedian(List<Point> sortedPoints) {
        
        if (sortedPoints.size() == 0){
            return 0;
        }
    
        return (int)((sortedPoints.size() - 1)/2); //if even number, will choose lower value
        
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
