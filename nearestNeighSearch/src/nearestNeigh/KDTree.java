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

} // end of class KdTree
