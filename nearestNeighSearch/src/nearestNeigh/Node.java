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

    // parent
    public Node parent = null;

    // left child
    public Node leftChild = null;

    // right child
    public Node rightChild = null;

} // end of class Node
