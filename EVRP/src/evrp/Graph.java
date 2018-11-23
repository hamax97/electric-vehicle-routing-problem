package evrp;

import java.util.ArrayList;

/**
 * The Graph abstract class contains the main functions that are used in a graph
 */

public abstract class Graph{
    private int size; //stores the total number of nodes in the graph

    /**
     * Constructor of the class that also sets the size of the graph
     *
     * @param numOfVertices represents the number of nodes that the graph is going to contain
     */
    public Graph(int numOfVertices){
	size = numOfVertices;
    }

    public abstract void addNode(Node node);

    public abstract Node getNode(int nodeId);
    
    public abstract void addEdge(int source, int destination, double distance);

    public abstract ArrayList<Successor> getSuccessors(int nodeId);

    public abstract void printSuccessors(int nodeId);

    public abstract double getDistance(int source, int destination);

    public int size(){
	return size;
    }
}
