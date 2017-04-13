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
    public PrintableNode getLeft(){
        return this.leftChild;   
    }


    /** Get right child */
    public PrintableNode getRight(){
        return this.rightChild; 
    }


    /** Get text to be printed */
    public String getText(){
        String label = this.point.id + ":" + (int)this.point.lat + "," + (int)this.point.lon;
        return label;
    }

    /**
     * Equality for a node.
     * @param obj Object (Node) to compare.
     * @return True if equal, otherwise false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (!this.point.equals(other.point)){
           return false;
        }
        return true;
    }



} // end of class Node
