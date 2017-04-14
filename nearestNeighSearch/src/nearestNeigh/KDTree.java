package nearestNeigh;

import java.util.*;
import java.util.Objects;
import static nearestNeigh.Category.*;

/**
 * Class representing the KD tree.
 *
 */
public class KDTree {

    public Node root; // Each node stores one point
    public int size; // Do we need?
    
    /**
     * Constructor
     */
    public KDTree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Prints KDTree 
     */
    
    public void print() {

        TreePrinter tp = new TreePrinter();
        tp.print(this.root); //Allows printout of tree - which looks fine
        return;
    }

    /**
     * Recursively prints KDTree
     */
    
    public void printTreeRecursion(Node currNode, String path) {

        if (currNode == null){
            return;
        }

        System.out.println("PATH: " + path + " " + currNode.point.toString());
        printTreeRecursion(currNode.leftChild, path + 'l');
        printTreeRecursion(currNode.rightChild, path + 'r');    
        
        return;
    }

   /**
     * retrieves node with given point from KDTree
     * @param searchPoint to be found and Boolean bXDim (true if sorting by X first)
     * @return if successful, node with given point.  Null if unsuccessful.
     */

    public Node getNode(Point searchPoint) {

        Node foundNode = new Node();
        
        foundNode = getNodeHelper(searchPoint, root, true);

        return foundNode;
    }

    public Node getNodeHelper(Point searchPoint, Node currNode, Boolean bXDim) {

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
        currNode = getNodeHelper(searchPoint, currNode, bXDim);
        return currNode;
    }

    /**
     * treeToList function - helper for deletePoint function.  Makes a list containing all points in subtree
     * below a given point. 
     * @param point that is root of subtree.
     * @return list containing all points in subtree below given point.
     */

    public List<Point> toList(Node targetNode) {
        
        List<Point> list = new ArrayList<Point>();
        Node currNode = targetNode;
        
        if (currNode.leftChild != null){
            list.add(currNode.leftChild.point);
            list.addAll(this.toList(currNode.leftChild));
        }
        if (currNode.rightChild != null){
            list.add(currNode.rightChild.point);
            list.addAll(this.toList(currNode.rightChild));
        }
        return list;
    }


} // end of class KdTree
