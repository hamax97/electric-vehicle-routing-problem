package evrp;

import java.lang.Math;
import java.util.ArrayList;

/**
 * The GraphAL class represents an implementation of Graph using adjacency lists
 *
 */
public class GraphAL extends Graph{
    private ArrayList<Node> graph; //list of nodes of the graph

    /**
     * Constructor of the class that calls its parent class and initializes the list of nodes
     *
     * @param size represents the number of nodes that the graph going to contain
     */
    public GraphAL(int size){
	super(size);
	this.graph = new ArrayList<Node>(size);
    }

    /**
     * Adds an object of type Node to the list
     *
     * @param node contains all the information of the node that is going to be added
     */
    public void addNode(Node node){
	graph.add(node);
    }

    /**
     * Returns an object of type Node but finding it based on its node id
     *
     * @param id represents the id of the needed node
     */
    public Node getNode(int id){
	return graph.get(id);
    }

    /**
     * Adds an edge between a source node and a destination node
     *
     * @param source represents the source node id
     * @param destination represents the destination node id
     * @param distance represents the euclidean distance between the source node and the destination node
     */
    public void addEdge(int source, int destination, double distance){
	graph.get(source).addSuccessor(destination, distance);
	graph.get(destination).addSuccessor(source, distance);
    }

    /**
     * Returns a list of successors that represents the successors of the required node
     *
     * @param nodeId represents the id of the needed node
     */    
    public ArrayList<Successor> getSuccessors(int nodeId){
	return graph.get(nodeId).getSuccessors();
    }

    /**
     * Prints to standard output all the successors of the required node
     *
     * @param nodeId represents the id of the needed node
     */
    public void printSuccessors(int nodeId){
	ArrayList<Successor> nodeSuccessors;
	nodeSuccessors = this.getSuccessors(nodeId);
	
	System.out.print("- Successors of: " + nodeId + ": ");
	for(int i = 0; i < nodeSuccessors.size(); i++)
	    System.out.print(nodeSuccessors.get(i).destination + " ");
	System.out.println("");
    }

    /**
     * Calculates the euclidean distance between two nodes
     *
     * @param source represents the source node id
     * @param destination represents the destination node id
     */
    public double getDistance(int source, int destination){
	double x1, x2, y1, y2;
	double distance;

	x1 = graph.get(source).getX();
	y1 = graph.get(source).getY();

	x2 = graph.get(destination).getX();
	y2 = graph.get(destination).getY();

	double a, b;
	a = Math.pow(Math.abs(y2 - y1), 2);
	b = Math.pow(Math.abs(x2 - x1), 2);

	//c = sqrt(( x2 - x1 )^2 + ( y2 - y1 )^2);
	distance = Math.sqrt(a + b);
	return distance;
    }

}
