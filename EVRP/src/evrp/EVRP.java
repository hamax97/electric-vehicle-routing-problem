package evrp;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.lang.Exception;

/**
 * @author Hamilton Tobon M, Santiago Toro
 * The EVRP class represents the main class and is the one that makes all the process of finding the routes
 * Date: 06/June/2018
 */
public class EVRP {

    //flag that if setted to true, the program is going to print debug information
    private static boolean debug;
    //for this problem, this represents an infinite distance
    private static double INFINITE = 1000000000.0;
    
    private static int numberOfNodes; //holds the total number of nodes
    private static int numberOfClients; //holds the total number of clients
    private static int numberOfStations; //holds the total number of stations
    private static double consumptionRate; //holds the energy consumption rate of each car
    private static double speed; //holds the speed of each car
    private static double tMax; //holds the max travel time for each car
    private static double sMax; //holds the max amount of time that a station takes to charge a car
    private static double tInCustomer; //holds the time that each car takes in a customer
    private static double batteryCapacity; //holds the maximum battery capacity of each car
    private static double batteryCapacityTemp; //controls the car battery during the travel
    private static ArrayList<Integer> currentPath; //contains the current path done so far for an individual car
    private static Graph graph; //contains the representation of the graph
    private static int[] visited; //takes control of the already visited clients
    private static int numOfVisitedClients; //control the number of already visited clients
    private static double currentDistance; //control the total distance traveled so far
    //these variables represent the initial and ending points of each one of the station charge functions
    private static double l1x1, l1x2, g1y1, g1y2;
    private static double l2x1, l2x2, g2y1, g2y2;
    private static double l3x1, l3x2, g3y1, g3y2;
    //these variables hold the charging ratio of each one of the different type of stations
    private static double m1, m2, m3;
    private static double globalTime; //control the current time traveled by a car
    //hold the total time traveled by a single car
    private static double timeTakenByCar;

    /**
     * Main function that read the file and start the looking for a solution based on that input file
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

	//if there are no arguments when executing the program
	if(args.length == 0){
	    System.err.println("No file in arguments");
	    System.exit(1);
	}

	//Uncomment for debug information
	debug = false;
	//debug = true;
	
	try{
	    //READS THE INPUT FILE AND CREATES THE CORRESPONDING GRAPH
	    //FROM HERE-------------------------------------------
	    String fileName = args[0];
	    System.out.println("Reading file: " + fileName);

	    Scanner reader = new Scanner(new File(fileName));

	    readHeader(reader);

	    if(debug){
		System.out.println("File header");
		System.out.println("Nodes: " + numberOfNodes);
		System.out.println("Clients: " + numberOfClients);
		System.out.println("Stations: " + numberOfStations);
		System.out.println("Rate: " + consumptionRate);
		System.out.println("Speed: " + speed);
		System.out.println("Tmax: " + tMax);
		System.out.println("Smax: " + sMax);
		System.out.println("t_customer: " + tInCustomer);
		System.out.println("Q: " + batteryCapacity);
	    }

	    reader.next(); //ignore Coordinates tag

	    //Important variable initialization
	    graph = new GraphAL(numberOfNodes);
	    currentPath = new ArrayList<Integer>();
	    visited = new int[graph.size()];
	    numOfVisitedClients = 0;
	    currentDistance = 0.0;
	    batteryCapacityTemp = batteryCapacity;
	    globalTime = 0.0;

	    //Reads and creates each node
	    for(int i = 0; i < numberOfNodes; i++){
		int id = reader.nextInt();
		reader.next(); //ignores the name of the node
		double x = reader.nextDouble();
		double y = reader.nextDouble();
		char type = reader.next().charAt(0);
		if(type == 's'){
		    int stationType = reader.nextInt();
		    graph.addNode(new Node(id, x, y, type, stationType));
		}else{
		    reader.next(); //ignores station type
		    graph.addNode(new Node(id, x, y, type));
		}
	    }

	    //Creates each edge
	    for(int i = 0; i < numberOfNodes; i++){
		for(int j = i+1; j < numberOfNodes; j++)
		    graph.addEdge(i, j, graph.getDistance(i, j));
	    }

	    readFooter(reader);
	    //TO HERE-------------------------------------------
	    
	    //Setting rate of charge for each type of station
	    m1 = (g1y2 - g1y1) / (l1x2 - l1x1); //fast station
	    m2 = (g2y2 - g2y1) / (l2x2 - l2x1); //slow medium
	    m3 = (g3y2 - g3y1) / (l3x2 - l3x1); //slow

	    //main function
	    TSP();
	    
	}catch(Exception e){
	    System.err.println(e.getMessage());
	    System.exit(1);
	}
    }

    public static void TSP(){
	//a path that is going to contain all the routes
	ArrayList<Integer> superPath = new ArrayList<Integer>();
	//successors of the depot node
	ArrayList<Successor> currentNodeSuccessors;
	//holds the depot node id
	int depot;
	//hols the id of the current node
	int currentNode;
	//control the number of routes found so far
	int routes = 0;

	depot = 0; //graph[0] is equal to the depot
	currentPath.add(depot);
	visited[depot] = 1;
	
	currentNodeSuccessors = graph.getSuccessors(depot);
	
	if(debug){
	    System.out.println("Adding node: " + depot);
	    graph.printSuccessors(depot);
	}

	//for each successor of depot
	for(int i = 0; i < currentNodeSuccessors.size(); i++){

	    if(numOfVisitedClients == numberOfClients){
		System.out.println("Clients visited: " + numOfVisitedClients);
		break;
	    }

	    timeTakenByCar = 0.0;
	    currentNode = minInDistance(currentNodeSuccessors);

	    if(currentNode != -1 && (visited[currentNode] == 0 || visited[currentNode] == 2)){
		currentPath.add(currentNode);
		currentDistance += currentNodeSuccessors.get(currentNode-1).distance;

		//these two conditional check if the next node is a valid node
		if(inValidTime()){
		    if(isValidPath()){

			if(graph.getNode(currentNode).getType() == 'c'){
			    visited[currentNode] = 1;
			    numOfVisitedClients++;
			}
			else{ //it is an station
			    
			    visited[currentNode] = 2; //station type
			}
			
			if(debug){
			    System.out.println("Adding node: " + currentNode);
			    System.out.println("Path valid so far: " + currentPath.toString());
			    System.out.println("Num of visited so far: " + numOfVisitedClients);
			}
			testPath(currentNode);
		    }else{ //if the node is not valid
			if(debug)
			    System.out.println("Node: " + currentNode
					       + " IS NOT VALID fot the currentPath: " + currentPath.toString());
		    
			currentPath.remove(currentPath.size() - 1);		
			continue;
		    }
		}else{
		    System.out.println("Unreachable node: " + currentPath.get(currentPath.size() - 1));
		}
	    
		if(isHamiltonianPath()){
		    System.out.println("Route " + routes + ": " + currentPath.toString());
		    System.out.println("Time taken by car: " + routes + " is: " + timeTakenByCar);
		    System.out.println("");
		    routes++;
		}
	    }else
		continue;

	    //reset time, battery, and distance for the next car
	    currentDistance = 0.0;
	    batteryCapacityTemp = batteryCapacity;
	    globalTime = 0.0;

	    //adds this path to the superpath
	    superPath.addAll(0, currentPath);
	    int pathSize = currentPath.size() - 1;
	    //erases the currentPath
	    for(int x = pathSize; x > 0; x--)
		currentPath.remove(x);
	}

	if(trySolution(superPath))
	    System.out.println("Valid Solution");
	else
	    System.out.println("Invalid Solution :(");
    }

    /**
     * Iterate through the successors of the given node and adds to the path the one that is valid node, and calls it recursively
     * @param currentNode id of the node that is going to be analyzed
     */
    public static void testPath(int currentNode){
	if(currentPath.get(currentPath.size() - 1) == 0) return;

	ArrayList<Successor> currentNodeSuccessors = graph.getSuccessors(currentNode);

	if(debug)
	    graph.printSuccessors(currentNode);
	
	for(int i = 0; i < currentNodeSuccessors.size(); ++i){

	    if(currentPath.get(currentPath.size() - 1) == 0){
		break;
	    }
	    
	    int nextNode = minInDistance(currentNodeSuccessors);

	    if(nextNode == -1){//current node does not have more successors without a visit
		currentPath.add(0);
		break;
	    }else if(visited[nextNode] == 0 || visited[nextNode] == 2){
		currentPath.add(nextNode);

		if(nextNode > currentNode)
		    currentDistance += currentNodeSuccessors.get(nextNode-1).distance;
		else
		    currentDistance += currentNodeSuccessors.get(nextNode).distance;
		
		//these two conditional check if the next node is a valid node		
		if(inValidTime()){
		    if(isValidPath()){

			if(graph.getNode(nextNode).getType() == 'c'){
			    visited[nextNode] = 1;
			    numOfVisitedClients++;
			}
			else{//it is an station
			    
			    visited[nextNode] = 2; //station type
			}
			
			if(debug){
			    System.out.println("Adding node: " + nextNode);
			    System.out.println("Path valid so far: " + currentPath.toString());
			    System.out.println("Num of visited so far: " + numOfVisitedClients);
			}
		    
			testPath(nextNode);
		    }else{//if the node is not valid
      
			if(debug)
			    System.out.println("Node: " + nextNode
					       + " IS NOT VALID fot the currentPath: " + currentPath.toString());
		    
			currentPath.remove(currentPath.size() - 1);
		    }
		}else{ //if there are no more reachable nodes
		    currentPath.remove(currentPath.size() - 1);
		    currentPath.add(0);
		    currentDistance = 0.0;
		    break;
		}
	    }
	}
    }

    /**
     * Verify if there is enough time to go to the last added node to the path and go back to depot
     * taking in count the total distance traveled so far and the battery level that the car has
     *
     * @return true if there is enough time, false in other case
     */
    public static boolean inValidTime(){
	
	int lastNodeAdded = currentPath.get(currentPath.size() - 1);

	double totalDistance = graph.getSuccessors(0).get(lastNodeAdded - 1).distance + currentDistance;
	globalTime = totalDistance / speed;

	//if it is a client adds TimeInCustomer to the global time
	if(graph.getNode(lastNodeAdded).getType() == 'c'){
	    globalTime += tInCustomer;
	    
	}
	else{//it is an station
	    
	    //battery level in wich the car is
	    double batteryToCharge = batteryCapacity - batteryCapacityTemp;
	    
	    //type of station
	    int stationType = graph.getNode(lastNodeAdded).getStationType();
	    
	    //y - 0 = m*(x - 0)
	    //x = y / m -> y: batteryToCharge, m: rate of charge of the station
	    //then what i need is the max time minus the time that it takes to charge the battery at the current level
	    if(stationType == 0){//fast
		double timeSpent = batteryToCharge / m1;
		timeSpent = l1x2 - timeSpent;
		globalTime += timeSpent;
		batteryCapacityTemp = batteryCapacity; //battery reload
		
	    }else if(stationType == 1){//mid
		double timeSpent = batteryToCharge / m2;
		timeSpent = l2x2 - timeSpent;
		globalTime += timeSpent;
		batteryCapacityTemp = batteryCapacity; //battery reload
		
	    }else if(stationType == 2){//slow
		double timeSpent = batteryToCharge / m3;
		timeSpent = l3x2 - timeSpent;
		globalTime += timeSpent;
		batteryCapacityTemp = batteryCapacity; //battery reload
		
	    }else{
		System.out.println("Unrecognized type of station: " + stationType);
	    }
	}	    
	if(globalTime <= tMax){
	    //update time taken by car
	    timeTakenByCar = globalTime;
	    return true;
	}
	return false;
    }

    /**
     * Verify if the last added node to the path is the depot
     *
     * @return true if the last added node is the depot, false in other case
     */
    public static boolean isHamiltonianPath(){
	if(currentPath.get(currentPath.size() - 1) == 0 && currentPath.size() != 1)
	    return true;
	return false;
    }

    /**
     * Iterate through the successors of the node that is currently being analized,
     * and selects the closest node, but giving priority to clients without a visit.
     * After selecting the closest node it calculates if the car has enough battery to visit the client
     * and go back depot, if that is not true, try if the car has enough battert to visit the client a go
     * to an station, if that is not true, that is an invalid node and it will return -1
     *
     * @param currentNodeSuccessors list of successors of the node that is currently being analyzed
     * @return the id of the closest node to the current node
     */
    public static int minInDistance(ArrayList<Successor> currentNodeSuccessors){

	double minDistance = INFINITE;
	double minDistance2 = INFINITE;
	int nodeId = -1;
	int nodeId2 = -1;

	for(int i = 0; i < currentNodeSuccessors.size(); i++){

	    int currentSuccessor = currentNodeSuccessors.get(i).destination;

	    //if visited[id] == 2 is because it is an station
	    if(visited[currentSuccessor] == 0 || visited[currentSuccessor] == 2){
		double currentDistance = currentNodeSuccessors.get(i).distance;

		//if not visited yet
		if(graph.getNode(currentSuccessor).getType() == 'c'){
		    
		    if(currentDistance < minDistance){
			minDistance = currentDistance;
			nodeId = currentSuccessor;
		    }
		    
		}else if(graph.getNode(currentSuccessor).getType() == 's'){
		    if(currentDistance < minDistance2){
			minDistance2 = currentDistance;
			nodeId2 = currentSuccessor;
		    }
		}
	    }
	}

	if(nodeId == -1){ //no more clients to visit
	    return nodeId;
	}else{
	    int lastNodeAdded = currentPath.get(currentPath.size() - 1);
	    double auxDistance;

	    //--------Battery spent visitting client
	    if(nodeId > lastNodeAdded)
		auxDistance = currentNodeSuccessors.get(nodeId-1).distance;
	    else
		auxDistance = currentNodeSuccessors.get(nodeId).distance;
	    
	    double timeSpent = auxDistance / speed;
	    
	    double batterySpent = timeSpent * consumptionRate;

	    //---------Battery spent going back depot
	    timeSpent = graph.getSuccessors(0).get(nodeId-1).distance / speed;

	    double batterySpent2 = timeSpent * consumptionRate;

	    //---------Battery spent going station
	    if(nodeId2 > lastNodeAdded)
		auxDistance = currentNodeSuccessors.get(nodeId2-1).distance;
	    else
		auxDistance = currentNodeSuccessors.get(nodeId2).distance;

	    timeSpent = auxDistance / speed;

	    double batterySpent3 = timeSpent * consumptionRate;

	    if((batteryCapacityTemp - batterySpent) < 0){ //can not go to client

		if((batteryCapacityTemp - batterySpent3) > 0){ //can go station
		    batteryCapacityTemp -= batterySpent3;
		    return nodeId2;
		}
		
		return -1;
		
	    }else if((batteryCapacityTemp - batterySpent - batterySpent2) > 0){//can go client and go back depot
		batteryCapacityTemp -= (batterySpent + batterySpent2);
		return nodeId;
		
	    }else if((batteryCapacityTemp - batterySpent - batterySpent3) > 0){ //can go client and go station
		batteryCapacityTemp -= (batterySpent + batterySpent3);		
		return nodeId;
	    }
	    return -1;
	}
    }

    /**
     * Verify if the path so far has the same node two times, excluding stations
     * because stations can be visited many times
     *
     * @return false if there is a client visited two times, true in other case
     */
    public static boolean isValidPath(){
	for(int i = 0; i < currentPath.size(); i++){
	    for(int j = 0; j < currentPath.size(); j++){
		if(i != j && currentPath.get(i) == currentPath.get(j))
		    if(graph.getNode(i).getType() == 'c')
			return false;
	    }
	}
	return true;
    }

    /**
     * Check if the path with all routes found has a client visited two times, ignoring stations and depot node
     *
     * @param path Super path that contain all the paths found
     * @return false if there is a client visited two times, true in other case
     */
    public static boolean trySolution(ArrayList<Integer> path){
	for(int i = 0; i < path.size(); i++){
	    for(int j = 0; j < path.size(); j++){
		if(path.get(i) != 0 && path.get(j) != 0)
		    if(i != j && path.get(i) == path.get(j))
			if(graph.getNode(i).getType() == 'c')
			    return false;
	    }
	}
	return true;
    }

    /**
     * Reads the header of the input file
     * @param reader scanner already initialized with the input file
     */    
    public static void readHeader(Scanner reader){
	//ignore n =
	reader.next();
	reader.next();
	numberOfNodes = reader.nextInt();
	//ignore m =
	reader.next();
	reader.next();
	numberOfClients = reader.nextInt();
	//ignore u =
	reader.next();
	reader.next();
	numberOfStations = reader.nextInt();
	//ignores breaks = #
	reader.next();
	reader.next();
	reader.next();
	//ignore r =
	reader.next();
	reader.next();
	consumptionRate = reader.nextDouble();
	//ignore speed = 
	reader.next();
	reader.next();
	speed = reader.nextDouble();
	//ignore Tmax =
	reader.next();
	reader.next();
	tMax = reader.nextDouble();
	//ignore Smax =
	reader.next();
	reader.next();
	sMax = reader.nextDouble();
	//ignore st_customer = 
	reader.next();
	reader.next();
	tInCustomer = reader.nextDouble();
	//ignore Q =
	reader.next();
	reader.next();
	batteryCapacity = reader.nextDouble();

	//ignore Coordinates
	reader.nextLine();
    }

    /**
     * Reads the footer of the input file
     * @param reader scanner already initialized with the input file
     */
    public static void readFooter(Scanner reader){
	
	reader.next(); //ignore l tag
	//line 1 time
	l1x1 = reader.nextDouble();
	reader.next();
	reader.next();
	l1x2 = reader.nextDouble();
	
	//line 2 time
	l2x1 = reader.nextDouble();
	reader.next();
	reader.next();
	l2x2 = reader.nextDouble();
	
	//line 3 time
	l3x1 = reader.nextDouble();
	reader.next();
	reader.next();
	l3x2 = reader.nextDouble();

	reader.next(); //ignore g tag
	//line 1 battery
	g1y1 = reader.nextDouble();
	reader.next();
	reader.next();
	g1y2 = reader.nextDouble();
	
	//line 2 battery
	g2y1 = reader.nextDouble();
	reader.next();
	reader.next();
	g2y2 = reader.nextDouble();
	
	//line 3 battery
	g3y1 = reader.nextDouble();
	reader.next();
	reader.next();
	g3y2 = reader.nextDouble();	
    }
}
