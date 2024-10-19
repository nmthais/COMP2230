/*
 * COMP2230 - Algorithms
 * Assignment 1 - Answers
 * @author  Studious Student - c1234567 (Replace with your name and student number)
 * @version 1.0
 * 
 * This is where you will write your code for the assignment. The required methods have been started for you, you may add additional helper methods and classes as required.
 */
import java.util.*;

 public class TrafficAnalyser {
    private final MapGenerator mapGen;
    public String cityMap = null;  // We make this public in order to access it for testing, you would not normally do this
    
    public TrafficAnalyser(int seed){
        mapGen = new MapGenerator(seed); // Pass a seed to the random number generator, allows for reproducibility
    }

    /*
     * This method must load the city map from the map generator and store it
     * You may assume that all marking scripts will call this method before any others.
     * Do not modify the code above the comment line in this method - this will be used to manually insert specific maps for marking purposes.
     * Write your code below the comment line where indicated.
     */
    public void loadMap(){
        if (cityMap == null) {
            cityMap = mapGen.generateMap();
        }

        // Write your code below this line
        //createGraph();
        Graph gr = new Graph(cityMap);
    }

    public DisjointSet createDisjointSet(HashMap<String, List<Integer>> graph) {
        // Get the graph data
        Graph gr = new Graph(cityMap);
        DisjointSet disjointSet = new DisjointSet();
        
        Initialize the disjoint set with all intersections
        for (String intersection : graph.keySet()) {
            disjointSet.makeSet(intersection);
        }
        
        // Union all intersections connected by a road
        for (Map.Entry<String, List<Integer>> entry : graph.entrySet()) {
            String intersection = entry.getKey();
            for (Integer road : entry.getValue()) {
                //disjointSet.union(intersection, road.g);
            }
        }
        for(int i=0;i<gr.getList().size();i++){
            System.out.println(gr.getList().get(i).roadName);
        }
        
        return disjointSet;
    }

    // public String getInnerCity(){
    //     // Graph gr = new Graph(cityMap);
    //     // HashMap<String, List<Integer>> graph = gr.getGraph();
    //     // DisjointSet disjointSet = createDisjointSet(graph);
        
    //     // // find the largest group
    //     // Map<String, Integer> groupSizes = new HashMap<>();
    //     // for (String intersection : graph.keySet()) {
    //     //     String root = disjointSet.find(intersection);
    //     //     groupSizes.put(root, groupSizes.getOrDefault(root, 0) + 1);
    //     // }
    //     // String largestGroupRoot = Collections.max(groupSizes.entrySet(), Map.Entry.comparingByValue()).getKey();
    //     // return largestGroupRoot;
    // }

    /*
     * @param intersectionName The name of the intersection to check
     * @return true if the intersection is in the inner city, false otherwise
     * 
     * The 'inner city' is defined as the largest connected component of intersections in the map.
     * 
     */
    public boolean isInInnerCity(String intersectionName) {
        // Your code here
        Graph gr = new Graph(cityMap);
        HashMap<String, List<Integer>> graph = gr.getMap();
        DisjointSet disjointSet = createDisjointSet(graph);
        //String largestGroupRoot = getInnerCity();
        
        // Check if the intersection is in the largest component
        //return disjointSet.find(intersectionName).equals(largestGroupRoot);
        throw new UnsupportedOperationException("Not implemented yet"); // Get rid of this line when you start implementing
    }


    /*
     * @param threshold The travel time threshold in minutes
     * @return The number of roads in the 'inner city' that have a travel time strictly greater (>) than the threshold
     * 
     * The 'inner city' is defined as the largest connected component of intersections in the map.
     * 
     */
    public int countInnerCitySlowRoads(double threshold) {
        // Your code here
        // Graph gr = new Graph(cityMap);
        // HashMap<String, List<Road>> graph = gr.getGraph();
        // DisjointSet disjointSet = createDisjointSet(graph);
        // String largestGroupRoot = getInnerCity();
        // int slowRoadCount = 0;

        // // Count the slow roads in the largest component
        // for(Road road : gr.getroadList()){
        //     if(disjointSet.find(road.destination).equals(largestGroupRoot) && road.travelTime > threshold){
        //         slowRoadCount++;
        //     }
        // }
        // for(int i=0; i< gr.getroadList().size();i++){
        //     System.out.println(gr.getroadList().get(i).roadName);
        // }
        
        // return slowRoadCount;
        throw new UnsupportedOperationException("Not implemented yet"); // Get rid of this line when you start implementing
    }    

    /* 
     * @return An array of road names that represent roads currently in the 'inner city' which, if any single one is closed, would result in an intersection no longer being reachable from the 'inner city'
     * 
     * The goal here is to identify roads that are critical to the connectivity of the city, that is, if any of these roads are closed, the inner city will be split into more disconnected components.
     * Remember that the 'inner city' is defined as the largest connected component of intersections in the map.
     *  
     */
    public String[] cityBottleneckRoads() {
        // Your code here
        String[] hehe = new String[0];
        return hehe;
        //throw new UnsupportedOperationException("Not implemented yet"); // Get rid of this line when you start implementing
    }

    /*
     * @param intersectionName The name of the intersection to check
     * @param hops The distance from the intersection to lockdown
     * @return An array of road names that have endpoints within 'hops' of the intersection
     * 
     * Which roads do we need to lockdown to secure the minister for transport's visit?
     * Note: You are required to use Breadth First Search (BFS) as part of your solution.
     * 
     */
    public String[] lockdownIntersection(String intersectionName, int hops) {
        // Your code here
        // Graph localGraph = new Graph(cityMap);
        // HashMap<String, List<Road>> graphData = localGraph.getGraph();

        // Set<String> visited = new HashSet<>();
        // Queue<String> queue = new LinkedList<>();
        // Map<String, Integer> hopCount = new HashMap<>();
        // List<String> lockdownRoads = new ArrayList<>();

        // queue.add(intersectionName);
        // visited.add(intersectionName);
        // hopCount.put(intersectionName, 0);

        // while (!queue.isEmpty()) {
        //     String currentIntersection = queue.poll();
        //     int currentHops = hopCount.get(currentIntersection);

        //     if (currentHops >= hops) continue; // Stop if we've reached the hop limit

        //     for (Road road : graphData.get(currentIntersection)) {
        //         if (!visited.contains(road.destination)) {
        //             visited.add(road.destination);
        //             queue.add(road.destination);
        //             hopCount.put(road.destination, currentHops + 1);
        //             lockdownRoads.add(road.roadName); // Lockdown road in hops range
        //         }
        //     }
        // }

        // return lockdownRoads.toArray(new String[0]);
        throw new UnsupportedOperationException("Not implemented yet"); // Get rid of this line when you start implementing
    }

    /*
     * @param intersectionNames An array of intersection names where protests are occurring
     * @return The number of roads that need to be closed to contain the protests, according to the rules below
     * 
     * Implement this as a simulation - every time step, you move first - then the protesters move.
     * On your move, you must identify a connected component of intersections blocked by protesters, and close the minimum number of roads to separate it from the rest of the city.
     * On the protesters move, they will spread to all connected intersections with a road open to the current intersection. They will only travel a single 'hop' per time step.
     * 
     * Specifically, on your move, you must identify the connected component of protesters that will spread to the *largest* number of intersections on the protesters' next move.
     * This is the one you must close off. If there is a tie, choose the one that requires the least number of roads to be closed in order to contain it. If there is still a tie, you may choose any of the tied components.
     *  
     */
    public int containProtests(String[] intersectionNames){
        // Your code here
        
        throw new UnsupportedOperationException("Not implemented yet"); // Get rid of this line when you start implementing
    }
}

class Road {
    String firstEndpoint, secondEndpoint, roadName;
    double travelTime;

    public Road(String roadname, String firstEndpoint, String secondEndpoint , double travelTime) {
        this.roadName = roadname;
        this.firstEndpoint = firstEndpoint;
        this.secondEndpoint = secondEndpoint;
        this.travelTime = travelTime;
    }
}

class Graph{
    static HashMap<String, List<Integer>> intersectionsMap = new HashMap<>();
    static ArrayList<Road> roadList = new ArrayList<>();
    public Graph(String cityString){
        cityString = cityString.substring(1, cityString.length()-1);
        String [] roadArray = cityString.split("}, \\{");
        for (String road : roadArray) {
            String[] city = road.replaceAll("[{}]", "").split(", "); // Remove remaining curly braces
            String roadName = city[0];
            String firstEndpoint = city[1];
            String secondEndpoint = city[2];
            Double avgTravelTime = Double.parseDouble(city[3]);
            addroad(roadName, firstEndpoint, secondEndpoint, avgTravelTime);
        }
    }
    public void addroad(String roadName, String firstEndpoint, String secondEndpoint, double travelTime) {
        Road road = new Road(roadName, firstEndpoint, secondEndpoint , travelTime);
        roadList.add(road);
        int roadIndex = roadList.size() - 1; // Get index of this road

        intersectionsMap.putIfAbsent(firstEndpoint, new ArrayList<>());
        intersectionsMap.get(firstEndpoint).add(roadIndex);
        intersectionsMap.putIfAbsent(secondEndpoint, new ArrayList<>());
        intersectionsMap.get(secondEndpoint).add(roadIndex);
    }
    public HashMap<String, List<Integer>> getMap(){
        return intersectionsMap;
    }
    public ArrayList<Road> getList(){
        return roadList;
    }
}

class DisjointSet{
   private Map<String, String> parent;
    private Map<String, Integer> rank;

    // Constructor initializes the parent and rank maps
    public DisjointSet() {
        parent = new HashMap<>();
        rank = new HashMap<>();
    }

    // Create a set for each intersection
    public void makeSet(String intersection) {
        parent.put(intersection, intersection); // Each intersection is its own parent initially
        rank.put(intersection, 0);              // Rank is initially 0
    }

    // Find with path compression
    public String find(String intersection) {
        // make the parent of the node point directly to the root
        if (!parent.get(intersection).equals(intersection)) {
            parent.put(intersection, find(parent.get(intersection)));
        }
        return parent.get(intersection);
    }

    // Union by rank, attach the smaller tree under the root of the larger tree
    public void union(String intersection1, String intersection2) {
        String root1 = find(intersection1);
        String root2 = find(intersection2);

        if (!root1.equals(root2)) {
            // Union by rank: attach the shorter tree under the taller tree
            if (rank.get(root1) > rank.get(root2)) {
                parent.put(root2, root1);
            } else if (rank.get(root1) < rank.get(root2)) {
                parent.put(root1, root2);
            } else {
                parent.put(root2, root1);
                rank.put(root1, rank.get(root1) + 1); // Increment the rank if both have the same rank
            }
        }
    }

}