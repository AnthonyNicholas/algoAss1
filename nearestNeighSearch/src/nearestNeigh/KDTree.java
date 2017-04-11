package nearestNeigh;

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
    
    public void printTree(Node currNode, String path) {

    System.out.println();
    printTreeRecursion(currNode, path);
    System.out.println();

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

} // end of class KdTree
