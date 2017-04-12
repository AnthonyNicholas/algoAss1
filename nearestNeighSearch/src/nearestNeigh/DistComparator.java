package nearestNeigh;
import java.util.*;// have imported to assist with comparators
import static nearestNeigh.Category.*;

/**
 * Class implementing comparator for sorting list according to distance from a point searchTerm.
 *
 * @author Jeffrey, Youhan
 */

public class DistComparator implements Comparator<Point>
{

    private Point searchTerm;

    public DistComparator(Point searchTerm)
    {
         this.searchTerm = searchTerm;
    }

    public int compare(Point p1, Point p2) {
        double dist1 = p1.distTo(searchTerm);
        double dist2 = p2.distTo(searchTerm);
        if (dist1 < dist2){
            return -1;
        }
        if (dist1 > dist2){
            return 1;
        }
        else{
            return 0;
        }
    }
 }

