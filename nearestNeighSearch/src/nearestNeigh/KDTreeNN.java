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
    private List<Point> storedPoints = new ArrayList<Point>();

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

        rTree.root = buildTree(rPoints, true);
        eTree.root = buildTree(ePoints, true);
        hTree.root = buildTree(hPoints, true);

        // rTree.printTree(rTree.root, "");
        // rTree.print(); //Allows printout of tree - which looks fine

        storedPoints = points;
        
        return;
    }
    
    
    /**
     * recursive function that builds KDTree.  
     * @param List of points
     * @return Node that is the root of the tree
     */

    public Node buildTree(List<Point> points, boolean bXDim) { 
        int median = 0;
        List<Point> sortedPoints = new ArrayList<Point>();
        Node currNode = new Node(); 
        Node leftChild = null; 
        Node rightChild = null;

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
            return null; 
        }

        median = findMedian(sortedPoints); 
        
        // construct a node for the median point 
        currNode = buildNode(sortedPoints.get(median)); 
        
        // Check if there is a left partition (indexing starts at 0).  If so, recursively partition it
        if (median > 0) {

            // Invert boolean value (effectively changing the dimension we split on next) 
            leftChild = buildTree(sortedPoints.subList(0,median), !bXDim);
            currNode.setLeft(leftChild);
        }
        // check if there is a right partition 
        if (median < (sortedPoints.size()-1)){
            
            // Invert the boolean value (effectively changing the dimension we split on next) 
            rightChild = buildTree(sortedPoints.subList(median+1,sortedPoints.size()), !bXDim); 
            currNode.setRight(rightChild);
        }        
         
        if (leftChild != null){
            leftChild.setParent(currNode); // Each node keeps track of its parent node.  
        }
        if (rightChild != null){
            rightChild.setParent(currNode);
        }
        
        return currNode;
    }
 
     /**
     * search function - searches for k points closest to a given point - searchTerm.
     * @param searchTerm - point around which we are searching.
     * @return list of k nearest points
     */

    @Override
    public List<Point> search(Point searchTerm, int k) {

        System.out.println("SEARCHING FOR " + k + " POINTS AROUND " + searchTerm.toString());

        SearchObject sObj = new SearchObject(k, searchTerm); // allows us to pass around k, searchTerm and searchResults
        sObj.searchResults.clear(); // empties any previous results stored in searchResults 

        // Get the KDTree matching the category of the point
        KDTree tree = getCatTree(sObj.searchTerm);

        sObj.searchResults.clear(); // empties any previous results stored in searchResults 
        sObj.searchedPoints.clear();

        sObj.printResults(0); // empties any previous results stored in searchResults 

        Node firstLeafNode = null;
        Node closestNode = null;
        Node currentNode = tree.root;
        BooleanObject bObj = new BooleanObject(true); // creating Boolean Obj so we can pass boolean by reference

        // Find the closest leaf
        
        firstLeafNode = findClosestLeaf(currentNode, bObj, sObj);
        System.out.println("Firstleaf: " + firstLeafNode.point.id);
        closestNode = firstLeafNode;

        sObj.addToSearchResults(closestNode.point); // add the initial closest leaf to our results

        sObj.printResults(1); // empties any previous results stored in searchResults 

        // Move back up the tree, checking for closer nodes

        unwindAndCheckIfCloser(firstLeafNode, bObj, sObj);

        // Add other closestNode to searchResults.
        sObj.addToSearchResults(closestNode.point);
        sObj.printResults(2);

        return sObj.searchResults;
    }

    /**
     * findClosestLeaf function - given starting node, finds closest leaf to point searchTerm. 
     * @param startNode, Point searchTerm, Boolean xAxis.
     * @return Node which is the closest leaf in KDTree
     */

    public Node findClosestLeaf(Node startNode, BooleanObject bObj, SearchObject sObj) {
        
        Node leafNode = null;
        Node currentNode = startNode;
        Node previousNode = null;

        while (currentNode != null) {
            previousNode = currentNode;

            sObj.addToSearchResults(currentNode.point);//Want to add all points as we traverse the tree to check if wrong results are due to simple missing points

            // Compare the correct point depending on the x/y split
            double currentPoint = (bObj.xAxis ? currentNode.point.lat : currentNode.point.lon);
            double searchPoint = (bObj.xAxis ? sObj.searchTerm.lat : sObj.searchTerm.lon);
            
            // System.out.println(currentNode.point.id + ": " + bObj.xAxis + ", " + currentPoint + " " + searchPoint);
            
            if (searchPoint < currentPoint) {
                // Go left if the x/y value is less than that of the search term (unless left is null, then go right);
                currentNode = (currentNode.leftChild == null ? currentNode.rightChild : currentNode.leftChild);
            }
            else {
                // Otherwise go right.
                currentNode = currentNode.rightChild;
            }
            // Flip our axis boolean so we compare the correct values next time
            bObj.xAxis = !bObj.xAxis;
        }

        // When we get to a null node, the previous node is our leaf!
        leafNode = previousNode;

        return leafNode;
    }

    /**
     * unwindAndCheckIfCloser function - given starting leaf, retraces path up KDTree checking whether there
     * are any nodes closer than current closest node. 
     * @param startNode, Point searchTerm, Boolean xAxis.
     * @return Node which is the closest leaf in KDTree
     */

    public void unwindAndCheckIfCloser(Node leaf, BooleanObject bObj, SearchObject sObj) {

        bObj.xAxis = !bObj.xAxis; // reflip our axis bool as we move back up the tree.
        Node currentNode = leaf.parent;
        Node usedChildNode = leaf;
        Node newLeafNode;
        
        //System.out.println("+++++++++ Current closest point distance: " + searchResults.get(searchResults.size() - 1).distTo(searchTerm) + " +++++++++++++++");

        while (currentNode != null) {

            if (!sObj.searchedPoints.contains(currentNode.point)) {
                sObj.searchedPoints.add(currentNode.point); // Marking this leaf as 'searched'.
            }

            // If the current node is closer than the least close node in the search results, replace
            if (sObj.kDistance > currentNode.point.distTo(sObj.searchTerm)) {
                //System.out.println("New closest node: " + currentNode.point.id + ". Distance: " + currentNode.point.distTo(searchTerm));
                sObj.addToSearchResults(currentNode.point);
            }

            // Calculate the closest possible point along the line of separation from our search term point
            Point closestPossiblePointOnLine = new Point();
            closestPossiblePointOnLine.lat = (bObj.xAxis ? currentNode.point.lat : sObj.searchTerm.lat);
            closestPossiblePointOnLine.lon = (!bObj.xAxis ? currentNode.point.lon : sObj.searchTerm.lon);
            //System.out.println("Calculated closest point on line: " + closestPossiblePointOnLine.lat + ", " + closestPossiblePointOnLine.lon + ". Distance: " + closestPossiblePointOnLine.distTo(searchTerm));

            // Then compare the distance from that point to our closest point to see if there are possibly closer points
            if (closestPossiblePointOnLine.distTo(sObj.searchTerm) < sObj.kDistance) {
                //System.out.println("======= Possible closest point in unexplored branch. Exploring! =======");
                // If so, go down the unexplored branch
                // If either of the child nodes is null, don't bother (the null is obviously unexplored)
                if (currentNode.rightChild != null && currentNode.leftChild != null) {

                    //System.out.println("Children aren't null");

                    currentNode = getUnusedChild(currentNode, usedChildNode);

                    //System.out.println("Child: " + currentNode.point.id);
                    // If the unusedChild hasn't already been searched
                    if (!sObj.searchedPoints.contains(currentNode.point)) {
                        //System.out.println("Unused child hasn't been searched");
                        newLeafNode = findClosestLeaf(currentNode, bObj, sObj);
                        //System.out.println("===== Adding new leaf " + newLeafNode.point.id + " to searched points. =====");
                        sObj.searchedPoints.add(newLeafNode.point); // This caused some infinite looping.
                        //System.out.println(searchedPoints);

                        //System.out.println("unwinding newLeaf: " + newLeafNode.point.id);
                        // Recursively unwind from new leaf
                        unwindAndCheckIfCloser(newLeafNode, bObj, sObj);
                    }
                    // else
                        //System.out.println("Child has already been searched. Continuing.");

                    currentNode = currentNode.parent;
                }
            }else
                //System.out.println("Skipping as line point isn't closer than current closest point.");
             // Update usedChildNode to point at the current node rather than its used child.
            usedChildNode = currentNode;
            bObj.xAxis = !bObj.xAxis; // reflip our axis bool as we move back up the tree.
            currentNode = currentNode.parent;
            // Collections.sort(searchResults, new DistComparator(searchTerm));
            // sObj.searchResults = searchResults.subList(0,k);
            //System.out.println("Search results in sorted order: " + searchResults);
            return;
        }
    }

    /**
     * getUnusedChild function - returns the other Node which has not previously been explored. 
     * @param Current closest Node & the used ChildNode.
     * @return The other child node.
     */

    public Node getUnusedChild(Node closestNode, Node usedChildNode) {
        //System.out.println("Getting child from: " + closestNode.point.id);
        //System.out.println("Used child: " + usedChildNode.point.id);
        if (closestNode.rightChild.point.equals(usedChildNode.point)) {
            return closestNode.leftChild;
        }
        return closestNode.rightChild;
    }

     /**
     * addPoint function - adds a point to KDTree of the category of the point.
     * @param point to be added.
     * @return boolean - true if point successfully added.
     */

    @Override
    public boolean addPoint(Point point) {

        System.out.println("ADDING POINT: " + point.toString());

        if (isPointIn(point)){
            return false;
        }
        
        // Get the KDTree for the category which point is in
        KDTree tree = getCatTree(point);
        
        Node addNode = new Node(point); // CHECK - MAKE CONSTRUCTOR ACCEPT POINT

        // Add node to tree in right position
        addNode(addNode, tree.root, null, true);
        
        // System.out.println("");
        // System.out.println("TREE AFTER ADDITION ");
        // tree.print();

        return true;
    }

     /**
     * deletePoint function - deletes a point - note how to delete - JChan answer - A: First, find node that holds 
     * the point being deleted.  Call this node D.  Get all children of node D. Delete node D, and construct new 
     * subtree from the children of node D.  Get the root of this new subtree, and this takes the (former) position of node D. 
     * @param point to be deleted.
     * @return boolean - true if point successfully deleted.
     */

    @Override
    public boolean deletePoint(Point point) {
        
        if (!isPointIn(point)){
            return false;
        }
        
        System.out.println("DELETING POINT: " + point.toString());
        List<Point> childList = new ArrayList<Point>(); //When we delete, need to build child tree again suign this list
        Node parent = null;
        
        KDTree tree = getCatTree(point);
        // System.out.println("ROOT: " + tree.root.point.toString());
        
        
        Node deletedNode = getNodeFromTree(point, tree.root, true);
        parent = deletedNode.parent;

        // System.out.println("deletedNode: " + deletedNode.point.toString());
        
        childList = treeToList(deletedNode); //Generate list from current child tree of deletedNode
        
        if (parent == null){ // deletedNode was root of KDTree
            tree.root = buildTree(childList, true);
            return true;
        }
        
        if (deletedNode.leftChild == null && deletedNode.rightChild == null){ // deletedNode was leaf of KDTree
            if (parent.leftChild.point.equals(point)){
                parent.leftChild = null;
            }
            else{
                parent.rightChild = null;
            }
            //  TreePrinter tp = new TreePrinter();
            // tp.print(tree.root); //Allows printout of tree - which looks fine
            return true;
        }
        else {
            if (parent.leftChild.point.equals(deletedNode.point)){
                // join new tree as leftChild of parent
                parent.leftChild = buildTree(childList, true);
                parent.leftChild.parent = parent;
                System.out.println("parentNode: " + parent.leftChild.parent.point.id);
            }
            else{
                // join new tree as rightChild of parent
                parent.rightChild = buildTree(childList, true);
                parent.rightChild.parent = parent;
                System.out.println("parentNode: " + parent.leftChild.parent.point.id);
            }
        }
        
        // System.out.println("");
        // System.out.println("TREE AFTER DELETION ");
        // tree.print();

        return true;

    }

    /**
     * treeToList function - helper for deletePoint function.  Makes a list containing all points in subtree
     * below a given point. 
     * @param point that is root of subtree.
     * @return list containing all points in subtree below given point.
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
     * isPointIn function - tests whether given point is in KDTree. 
     * @param point to be checked.
     * @return true if point is in KDTree, false otherwise.
     */

    @Override
    public boolean isPointIn(Point point) {
        
        System.out.println("CHECKING POINT: " + point.toString());
        
        KDTree tree = getCatTree(point);

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
     * adds node to KDTree of the same category of the point.
     * @param addNode to be added, currNode (= rootNode of KDTree initially), currParent (=null initially) and bXDim - (set to true initially)
     * @return true if successfully added, false otherwise
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

   /**
     * findMedian - finds index of the median point in list of points.
     * @param list of points
     * @return index of the median point
     */

    private int findMedian(List<Point> sortedPoints) {
        
        if (sortedPoints.size() == 0){
            return 0;
        }
    
        return (int)((sortedPoints.size() - 1)/2); //if even number, will choose lower value
        
    }

   /**
     * buildNode - given a point, returns a new node containing that point.
     * @param list of points
     * @return index of the median point
     */

    private Node buildNode(Point medianPoint) {
    
        Node newNode = new Node(medianPoint);
        return newNode;

    }
    
      /**
     * getCatTree - retrieves KDTree of the category of given point.
     * @param point
     * @return KDTree with same category
     */
    
    public KDTree getCatTree(Point point) {
        // Check whether point already in tree

        KDTree tree = new KDTree();
        
        if (point.cat.equals(Category.RESTAURANT)){
            tree = rTree;
        }
        if (point.cat.equals(Category.EDUCATION)){
            tree = eTree;
        }
        if (point.cat.equals(Category.HOSPITAL)){
            tree = hTree;
        }

        return tree;
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
