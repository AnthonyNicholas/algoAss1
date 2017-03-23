package nearestNeigh;

import java.util.Objects;
import static nearestNeigh.Category.*;

/**
 * Class representing a node in the KD tree.
 *
 * @author Jeffrey, Youhan
 */
public class Node {

    // Each node stores one point
    public Point point = null;

    // parent - because I think in our search we need to be able to go up and down the tree
    public Node parent = null;

    // left child
    public Node leftChild = null;

    // right child
    public Node rightChild = null;

} // end of class Node
