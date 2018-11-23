package evrp;

/**
 * The Successor class stores the information that a parent node need
 */
public class Successor{
    public int destination; //represents the id of this successor
    public double distance; //represents the distance from the parent to this successor

    /**
     * Constructor of the class that assigns an id and a distance
     * to this successor
     *
     * @param destination represents the successor id
     * @param distanceToTheNode represents the euclidean distance between the parent node and this successor
     */
    public Successor(int destination, double distanceToTheNode){
	this.destination = destination;
	this.distance = distanceToTheNode;
    }
}
