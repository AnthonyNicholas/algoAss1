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
     * Recursively prints KDTree
     */
    
    public void printTree(Node currNode, int level) {
    level = level + 1;
    
    if (currNode == null){
        return;
    }
    System.out.print("*");
    System.out.println("Level =" + level + currNode.point.toString());
    System.out.print("---");
    printTree(currNode.leftChild, level);
    printTree(currNode.rightChild, level);    
    
    return;
    }

} // end of class KdTree
