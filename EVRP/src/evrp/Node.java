package evrp;

import java.util.ArrayList;

/**
 * The Node class represents a single node in the whole graph, and 
 * stores all the information needed about a single node
 */
public class Node{
    private int nodeId; //represents the node id
    private double x; //represents the x coordinate
    private double y; //represents the y coordinate
    private char nodeType; //represents the type of node
    private int stationType; //represents the type of station (if applies)
    private ArrayList<Successor> successors; //contains all the successor of this node

    //null values used in a constructor without enough parameters
    private final int NULLINT = 2000000000;
    private final char NULLCHAR = '\0';

    /**
     * Constructor that initializes all the values for this node
     *
     * @param nodeId represents the id of this node
     */
    public Node(int nodeId){
	this.nodeId = nodeId;
	this.x = 0.0;
	this.y = 0.0;
	this.nodeType = NULLCHAR;
	this.stationType = NULLINT;
	this.successors = new ArrayList<Successor>();
    }

    /**
     * Constructor that initializes all the values for this node
     *
     * @param nodeId represents the id of this node
     * @param x represents the x coordinate of this node
     * @param y represents the y coordinate of this node
     * @param nodeType represents the type of this node
     */
    public Node(int nodeId, double x, double y, char nodeType){
	this.nodeId = nodeId;
	this.x = x;
	this.y = y;
	this.nodeType = nodeType;
	this.stationType = NULLINT;
	this.successors = new ArrayList<Successor>();
    }

    /**
     * Constructor that initializes all the values for this node
     *
     * @param nodeId represents the id of this node
     * @param x represents the x coordinate of this node
     * @param y represents the y coordinate of this node
     * @param nodeType represents the type of this node
     * @param stationType representas the type of the station (fast, slow, medium slow)
     */
    public Node(int nodeId, double x, double y, char nodeType, int stationType){
	this.nodeId = nodeId;
	this.x = x;
	this.y = y;
	this.nodeType = nodeType;
	this.stationType = stationType;
	this.successors = new ArrayList<Successor>();
    }

    /**
     * Returns the id of this node
     * @return id of this node
     */    
    public int getId(){
	return nodeId;
    }

    /**
     * Returns the x coordinate of this node
     * @return x coordinate of this node
     */
    public double getX(){
	return x;
    }

    /**
     * Returns the y coordinate of this node
     * @return y coordinate of this node
     */
    public double getY(){
	return y;
    }

    /**
     * Returns the type of this node
     * @return type of this node
     */
    public char getType(){
	return nodeType;
    }

    /**
     * Returns the station type of this node, if this node is not
     * an stations, it returns NULLINT
     * @return station type of this node (if it is not an station returns NULLINT)
     */
    public int getStationType(){
	return stationType;
    }

    /**
     * Returns the list of successors of this node
     * @return list of successors of this node
     */
    public ArrayList<Successor> getSuccessors(){
	return successors;
    }

    /**
     * Adds a successor to the list of successors of this node
     * 
     * @param destination represents the succesor id
     * @param distance represents the euclidean distance from this node to the successor
     */
    public void addSuccessor(int destination, double distance){
	successors.add(new Successor(destination, distance));
    }
}
