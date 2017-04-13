package nearestNeigh;
import java.util.*;// have imported to assist with comparators

import java.util.Objects;
import static nearestNeigh.Category.*;

/**
 * Class holding parameters for a search.
 *
 * @author Jeffrey, Youhan
 */
public class SearchObject {

    // Number of points sought
    public int k;
    // Point around which search is conducted
    public Point searchTerm;
    // List of search results
    public List<Point> searchResults = new ArrayList<Point>();
    // The kth closest point on list of search results
    public Point closestK;
    // The distance the kth closest point is from the searchTerm
    public double kDistance;
    // Number of search results
    public int rNum; 
    // Points already searched
    public List<Point> searchedPoints = new ArrayList<Point>();

    /**
     * constructor with given point information
     */
    public SearchObject(int k, Point searchTerm) {
        this.k = k;
        this.searchTerm = searchTerm;
        this.searchResults = new ArrayList<Point>();
        this.closestK = null;
        this.kDistance = 99999999;
        this.rNum = 0;
    }

     /**
     * addToSearchResults function - adds closePoint, sorts results and updates kClosestNode & kDistance. 
     * @param Candidate close Node.
     * @return kClosestNode.
     */

    public void addToSearchResults(Point closePoint) {
        
        if (!searchResults.contains(closePoint)){
            searchResults.add(closePoint);
            Collections.sort(searchResults, new DistComparator(searchTerm));
            if (searchResults.size() > k){
                searchResults = searchResults.subList(0,k);
            }

            rNum = searchResults.size();

            // System.out.println("Adding Search Result: " + closePoint.id + "rNum: " + rNum);
            // System.out.print("Current searchResults: ");
            // for (Point point : searchResults){
            //     System.out.print(point.id + " ");
            // }
            // System.out.println();

            closestK = rNum >= k ? searchResults.get(k-1) : searchResults.get(rNum-1);
            kDistance = closestK.distTo(searchTerm);

        }

        return;
    }
    
    /**
     * sortResults function - sorts & trims searchResults and updates closestK & kDistance. 
     * @param 
     * @return 
     */

    public void sortResults() {
        
        Collections.sort(searchResults, new DistComparator(searchTerm)); 

        if (rNum > k){
            searchResults = searchResults.subList(0, k); // if greater than k, reduce to k
            rNum = searchResults.size();
        }
        
        closestK = rNum >= k ? searchResults.get(k-1) : searchResults.get(rNum-1);
        kDistance = closestK.distTo(searchTerm);
        return;
    }

/**
     * printResults function - prints searchResults. 
     * @param 
     * @return 
     */

    public void printResults(int i) {

        Collections.sort(searchResults, new DistComparator(searchTerm));
        System.out.print("searchResults " + i + ": ");
        for (Point point : searchResults){
                System.out.print(point.id+ " ");
        }
        System.out.println();

        return;
    }

} 
