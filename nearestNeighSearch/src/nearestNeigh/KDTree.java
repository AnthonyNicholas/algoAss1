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
    
    public void printTree(Node currNode, String path) {

    if (currNode == null){
        return;
    }

    System.out.println("PATH: " + path + " " + currNode.point.toString());
    printTree(currNode.leftChild, path + 'l');
    printTree(currNode.rightChild, path + 'r');    
    
    return;
    }
    
    /**
     * Print out tree in ascii text.
     */
    public void asciiPrint() {
        AsciiPrinter.printNode(root);
        return;
    } // end of printAscii()


} // end of class KdTree
