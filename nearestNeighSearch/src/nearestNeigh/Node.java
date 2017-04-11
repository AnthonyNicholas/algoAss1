package nearestNeigh;

import java.util.Objects;
import static nearestNeigh.Category.*;

/**
 * Class representing a node in the KD tree.
 *
 * @author Jeffrey, Youhan
 */
public class Node implements PrintableNode {

    public Point point = null; // Each node stores one point
    public Node parent = null; // parent - because I think in our search we need to be able to go up and down the tree
    public Node leftChild = null; // left child
    public Node rightChild = null;     // right child
    
    /**
     * Constructors
     */
    public Node() {
        this.point = null;
    }

    public Node(Point point) {
        this.point = point;
    }

    
    public void setLeft(Node leftChild) {
        
        this.leftChild = leftChild;
        return;

    }

    public void setRight(Node rightChild) {
    
        this.rightChild = rightChild;
        return;
        
    }

    public void setParent(Node parentNode) {
    
        this.parent = parentNode;
        return;
    }

   /** Get left child */
    PrintableNode getLeft(){
        return this.leftChild;   
    }


    /** Get right child */
    PrintableNode getRight(){
        return this.rightChild; 
    }


    /** Get text to be printed */
    String getText(){
        return this.point.id;
    }



} // end of class Node
