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
        
        TreePrinter tp = new TreePrinter();
        tp.print(rTree.root); //Allows printout of tree - which looks fine
        
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
            // System.out.println("NULL");
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

        Node leftLeafNode, rightLeafNode = null;
        Node closestNode = null;
        BooleanObject bObj = new BooleanObject(false); // because our searches start with the left and right children of tree.root
        Node usedChildNode = null;
        
        // Get the KDTree matching the category of the point
        KDTree tree = getCatTree(searchTerm);
        sObj.addToSearchResults(tree.root.point);

        // RIGHT SIDE OF TREE      

        // Go down right side to closest leaf
        rightLeafNode = findClosestLeaf(tree.root.rightChild, bObj, sObj);
        sObj.addToSearchResults(rightLeafNode.point);

        System.out.println("RightLeaf: " + rightLeafNode.point.id + " " + bObj.xAxis);

        // Move back up the tree, checking for closer nodes
        unwindAndCheckIfCloser(rightLeafNode, bObj, sObj);

        // LEFT SIDE OF TREE
        // Go down left side to closest leaf
        bObj.xAxis = false;
        leftLeafNode = findClosestLeaf(tree.root.leftChild, bObj, sObj);
        sObj.addToSearchResults(leftLeafNode.point);
        System.out.println("LeftLeaf: " + leftLeafNode.point.id + " " + bObj.xAxis);

        // Move back up the tree, checking for closer nodes
        unwindAndCheckIfCloser(leftLeafNode, bObj, sObj);

        // sort searchResults into order of distance from searchTerm (also trims and updates kClosestPoint)
        sObj.sortResults();
        sObj.printResults();

        Collections.sort(storedPoints, new DistComparator(searchTerm));

        System.out.println();
        System.out.println("DISTANCES OF ALL POINTS");
        for (Point point:storedPoints){
            System.out.println(point.id + ": " + point.distTo(searchTerm));
        }
        
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
        
        //check if already on leaf
        if (currentNode.leftChild == null && currentNode.rightChild == null){
            return currentNode;
        }

        while (currentNode != null) {
            previousNode = currentNode;
            
            // Compare the correct point depending on the x/y split
            double currentPoint = (bObj.xAxis ? currentNode.point.lat : currentNode.point.lon);
            double searchPoint = (bObj.xAxis ? sObj.searchTerm.lat : sObj.searchTerm.lon);
            
            System.out.println("Descending: " + currentNode.point.id + ": " + bObj.xAxis + ", " + currentPoint + " " + searchPoint);
            
            if (searchPoint < currentPoint) {
                // Go left if the x/y value is less than that of the search term
                currentNode = currentNode.leftChild;
            }
            else {
                // Otherwise go right.
                currentNode = currentNode.rightChild;
            }
            // Flip our axis boolean so we compare the correct values next time
            bObj.xAxis = !bObj.xAxis;
        }
        
        // When we get to a null node, the previous node is our leaf! Also flip boolean as we are going up from null child to leaf.
        leafNode = previousNode;
        bObj.xAxis = !bObj.xAxis;
        
        System.out.println("LeafDistance: " + leafNode.point.distTo(sObj.searchTerm));

        return leafNode;
    }

    /**
     * unwindAndCheckIfCloser function - given starting leaf, retraces path up KDTree checking whether there
     * are any nodes closer than current closest node. 
     * @param startNode, Point searchTerm, Boolean xAxis.
     * @return Node which is the closest leaf in KDTree
     */

    public void unwindAndCheckIfCloser(Node leaf, BooleanObject bObj, SearchObject sObj) {

        KDTree tree = getCatTree(sObj.searchTerm);
        
        Node newLeafNode = null;
        Node usedChildNode = leaf;
        Node currentNode = leaf.parent; // one up from leaf
        bObj.xAxis = !bObj.xAxis; // reflip our axis bool as we have moved up one level.

        while (!currentNode.point.equals(tree.root.point)){

            System.out.println("Unwinding: " + currentNode.point.id + " " + bObj.xAxis);
            
            // If the current node is closer and hasn't already been visited, record it as a close node and explore other branch.
            if ((currentNode.point.distTo(sObj.searchTerm) < sObj.kDistance) && !sObj.searchResults.contains(currentNode.point)) {

                System.out.println("New closeNode: " + currentNode.point.id + ": " + currentNode.point.distTo(sObj.searchTerm) + "vs " + sObj.kDistance);
                sObj.addToSearchResults(currentNode.point);

                // If currentNode is a leaf, or only has one child, continue
                if (currentNode.leftChild == null || currentNode.rightChild == null){
                    break;
                }
                
                // Go down the unused child to explore the unexplored branch
                currentNode = getUnusedChild(currentNode, usedChildNode);
                bObj.xAxis = !bObj.xAxis; // flip boolean because we have gone down one level
                
                newLeafNode = findClosestLeaf(currentNode, bObj, sObj);
                sObj.addToSearchResults(newLeafNode.point);
                currentNode = newLeafNode;
                System.out.println("NewLeaf: " + newLeafNode.point.id + " " + bObj.xAxis);
                
                // Recursively continue down unexplored branches
                unwindAndCheckIfCloser(newLeafNode, bObj, sObj);

                // When recursion finishes, will be back at root
                // currentNode = tree.root;
                System.out.println("Finished recursion: Current Node: " + currentNode.point.id + " usedChildNode: " + currentNode.point.id + bObj.xAxis);
                break;
            }
            
            sObj.addToSearchResults(currentNode.point);
            usedChildNode = currentNode;
            currentNode = currentNode.parent;
            bObj.xAxis = !bObj.xAxis; // reflip our axis bool as we move back up the tree.
        }
        
        // Now back at currentNode is now tree.root 

        return;
    }


    /**
     * getUnusedChild function - returns the other Node which has not previously been explored. 
     * @param Current closest Node & the used ChildNode.
     * @return The other child node.
     */

    public Node getUnusedChild(Node closestNode, Node usedChildNode) {
        
        Node otherChildNode = null;
        
        if (closestNode.rightChild.point.equals(usedChildNode.point)){
            otherChildNode = closestNode.leftChild;
        } 
        else{
            otherChildNode = closestNode.rightChild;
        }
        return otherChildNode;
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
        // tree.printTree(tree.root, "");

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
        
        Node deletedNode = getNodeFromTree(point, tree.root, true);
        parent = deletedNode.parent;
        
        childList = treeToList(deletedNode); //Generate list from current child tree of deletedNode
        
        if (parent == null){ // deletedNode was root of KDTree
            tree.root = buildTree(childList, true);
        }
        else {
            if (parent.leftChild.point.equals(deletedNode.point)){
                // join new tree as leftChild of parent
                parent.leftChild = buildTree(childList, true);
                parent.leftChild.parent = parent;
            }
            else{
                // join new tree as rightChild of parent
                parent.rightChild = buildTree(childList, true);
                parent.rightChild.parent = parent;
            }
        }
        
        // System.out.println("");
        // System.out.println("TREE AFTER DELETION ");
        // tree.printTree(tree.root, "");

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
