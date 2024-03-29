package nearestNeigh;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is required to be implemented.  Naive approach implementation.
 *
 * @author Jeffrey, Youhan
 */
public class NaiveNN implements NearestNeigh{

    private List<Point> index; // Stores the arrayList of points & is accessible to all methods

    @Override
    public void buildIndex(List<Point> points) {
    
        this.index = new ArrayList<Point>();
        index.addAll(points); //  Put all points into index.  

    }

    @Override
    public List<Point> search(Point searchTerm, int k) {

        // ANT IMPLEMENTATION

        List<Point> searchResults = new ArrayList<Point>(); // arraylist to store search results.
        double kPointDist = 1000000; // Distance of last poitn in searchResults list from searchTerm

        // Search naive arraylist points and find the k points with minimum distance from searchTerm.
        // NOTE: need to access our arrayList points.  How do i do that from here?

        for (Point point : index) {

            // If point is not the right category, ignore.
            if (point.cat != searchTerm.cat){
                continue;
            } 
            
            // if searchResults is fully populated and point is further away than furthest current result, ignore.
            if ((searchResults.size() == k) && (point.distTo(searchTerm) >= kPointDist)){
                continue;
            } 

            // if searchResults empty, add point.
            if (searchResults.isEmpty()){
                searchResults.add(point);
                continue;
            } 

            // Close point - add to searchResults positioning it in ascending order of distance
            
            for (int i = 0; i < searchResults.size(); i++){
                if (point.distTo(searchTerm) > searchResults.get(i).distTo(searchTerm)){
                    continue;
                }
                else {
                    searchResults.add(i,point); // add point at position i in results list
                    break;
                }
            }

            // If size of searchResults is now greater than k, drop last point

            if (searchResults.size() > k){
                searchResults.remove(k);
            }

            // Update kPointDist
            kPointDist = searchResults.get(searchResults.size()-1).distTo(searchTerm);            
        }
        
        return searchResults;
    }

    @Override
    public boolean addPoint(Point point) {

        if(this.index.contains(point)){
            return false;
        }
        else{
            this.index.add(point);
        }
        return true;
    }

    @Override
    public boolean deletePoint(Point point) {

        if (!this.index.contains(point)){
            return false;
        }
        else{
            int i = this.index.indexOf(point);
            index.remove(i);        
        }     
        return true;
    }

    @Override
    public boolean isPointIn(Point point) {
        // To be implemented.
        if (!this.index.contains(point)){
            return false;
        }

        return true;
    }

}
