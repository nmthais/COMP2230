/*
 * COMP2230 - Algorithms
 * Assignment 1 - Answers
 * @author  Studious Student - c1234567 (Replace with your name and student number)
 * @version 1.0
 * 
 * This is where you will write your code for the assignment. The required methods have been started for you, you may add additional helper methods and classes as required.
 */
import java.lang.reflect.Array;
import java.util.*;

 public class TrafficAnalyser {
    private final MapGenerator mapGen;
    public String cityMap = null;  // We make this public in order to access it for testing, you would not normally do this
    private Graph graph = null;
    private DisjointSet disjointSet = null;
    
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
        graph = new Graph(cityMap);
    }
    
    

    /*
     * @param intersectionName The name of the intersection to check
     * @return true if the intersection is in the inner city, false otherwise
     * 
     * The 'inner city' is defined as the largest connected component of intersections in the map.
     * 
     */
    public boolean isInInnerCity(String intersectionName) {
        // Your code here
        HashMap<String, List<Road>> graphData = graph.getGraph();
        DisjointSet disjointSet = createDisjointSet();
        String largestGroupRoot = getInnerCityRootNode();

        return disjointSet.find(intersectionName).equals(largestGroupRoot);
    }

    public DisjointSet createDisjointSet() {
        // Get the graph data
        DisjointSet disjointSet = new DisjointSet();
        HashMap<String, List<Road>> graphData = graph.getGraph();
        //Initialize the disjoint set with all intersections
        for (String intersection : graphData.keySet()) {
            disjointSet.makeSet(intersection);
        }
        
        for (Road road : graph.getList()) {
            disjointSet.union(road.firstIntersection, road.secondIntersection);
        }
        
        return disjointSet;
    }

    public String getInnerCityRootNode(){
        HashMap<String, List<Road>> graphData = graph.getGraph();
        DisjointSet disjointSet = createDisjointSet();
    
        // find the largest group
        Map<String, Integer> groupSizes = new HashMap<>();
        for (String intersection : graphData.keySet()) {
            String root = disjointSet.find(intersection);
            groupSizes.put(root, groupSizes.getOrDefault(root, 0) + 1);
        }
        String largestGroupRoot = Collections.max(groupSizes.entrySet(), Map.Entry.comparingByValue()).getKey();

        return largestGroupRoot;
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
        DisjointSet disjointSet = createDisjointSet();
        String largestGroupRoot = getInnerCityRootNode();

        int slowRoadCount = 0;
    
        // Iterate through all roads in the graph and check if both endpoints are in the inner city
        for (Road road : graph.getList()) {
    
            if (disjointSet.find(road.firstIntersection).equals(largestGroupRoot) && 
            disjointSet.find(road.secondIntersection).equals(largestGroupRoot)) {
                if (road.travelTime > threshold) {
                    slowRoadCount++;
                }
            }
        }

        return slowRoadCount;
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
        ArrayList<String> bottleneckRoads = new ArrayList<>();
        HashMap<String, String> parentMap = new HashMap<>();
        HashMap<String, Integer> discoveryTime = new HashMap<>();
        HashMap<String, Integer> lowTime = new HashMap<>();
        Stack<String> openStack = new Stack<>();
        Set<String> closeStack = new HashSet<>();
        ArrayList<String> innerinterList = getInnerIntersection();
        int time=0;
        for(String intersection : innerinterList){
            if(!closeStack.contains(intersection)){
                helperCityBottleneck(intersection ,parentMap, discoveryTime, lowTime, openStack, closeStack, time, innerinterList, bottleneckRoads);
            }
        }
        return bottleneckRoads.toArray(new String[0]); 
    //     throw new UnsupportedOperationException("Not implemented yet"); // Get rid of this line when you start implementing
    }

    public List<String> helperCityBottleneck(String startInter, HashMap<String, String> parentMap, HashMap<String, Integer> discoveryTime, 
    HashMap<String, Integer> lowTime, Stack<String> openStack, Set<String> closeStack, int time, ArrayList<String> innerinterList, List<String> bottleneckRoads){
        HashMap<String, List<Road>> graphData = graph.getGraph();


        openStack.push(startInter);
        parentMap.put(startInter, null);            //start node dont have parent
        
        discoveryTime.put(startInter, time);
        lowTime.put(startInter, time);
        time++;


        while(!openStack.empty()){                              //iterative dfs + tarjan algorithm
            String current = openStack.peek();
            
            boolean allNeighborsProcessed = true;
            for(Road road : graphData.get(current)){
                String neighbor = road.firstIntersection.equals(current) ? road.secondIntersection : road.firstIntersection;

                if(!discoveryTime.containsKey(neighbor)){
                    openStack.push(neighbor);
                    parentMap.put(neighbor, current);
                    discoveryTime.put(neighbor, time);
                    lowTime.put(neighbor, time);
                    time++;
                    allNeighborsProcessed = false;
                    break;
                }
                else if(!neighbor.equals(parentMap.get(current))){
                    lowTime.put(current, Math.min(lowTime.get(current), discoveryTime.get(neighbor)));
                }
            }
            if(allNeighborsProcessed){
                openStack.pop();
                String parent = parentMap.get(current);
                if(parent !=null && lowTime.get(current) > discoveryTime.get(parent)){
                    for(Road road : graphData.get(current)){
                        if((road.firstIntersection.equals(current) && road.secondIntersection.equals(parent)) ||
                        (road.secondIntersection.equals(current) && road.firstIntersection.equals(parent))){
                            bottleneckRoads.add(road.roadName);
                        }
                    }
                }
                if(parent !=null){
                    lowTime.put(parent, Math.min(lowTime.get(parent), lowTime.get(current)));
                }
            }
            closeStack.add(current);
            
        }
        return bottleneckRoads;
    }

    public ArrayList<String> getInnerIntersection(){
        DisjointSet disjointSet = createDisjointSet();
        ArrayList<String> innerInterectionList = new ArrayList<>();

        for(String intersection : graph.getGraph().keySet()){
            if(disjointSet.find(intersection).equals(getInnerCityRootNode())){
                innerInterectionList.add(intersection);
            }
        }
        return innerInterectionList;
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
        HashMap<String, List<Road>> graphData = graph.getGraph();

        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> hopCount = new HashMap<>();
        Set<String> lockdownRoads = new HashSet<>();

        queue.add(intersectionName);
        visited.add(intersectionName);
        hopCount.put(intersectionName, 0);

        while (!queue.isEmpty()) {
            String currentIntersection = queue.poll();
            int currentHops = hopCount.get(currentIntersection);

            if (currentHops > hops) continue; // Stop if we've reached the hop limit

            for (Road road : graphData.get(currentIntersection)) {
                String neighbor = road.firstIntersection.equals(currentIntersection) ? road.secondIntersection : road.firstIntersection;
                lockdownRoads.add(road.roadName); // Lockdown road in hops range
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    hopCount.put(neighbor, currentHops+1);
                }
            }
        }
        return lockdownRoads.toArray(new String[0]);
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
        HashMap<String, List<Road>> graphData = graph.getGraph();
        Set<String> protesters = new HashSet<>(Arrays.asList(intersectionNames));  //Current locations of protesters
        Set<String> blockedRoads = new HashSet<>();  //Roads that have already been blocked
        int totalRoadsBlocked = 0;

        //Continue until all protests are contained
        while (!protesters.isEmpty()) {
            Set<String> visited = new HashSet<>();
            Set<String> largestComponent = null;
            int maxSpread = -1;
            Set<String> roadsToBlockForBestComponent = null;

            //Find all protester connected components and calculate spread potential
            for (String protester : protesters) {
                if (!visited.contains(protester)) {
                    Set<String> component = new HashSet<>();
                    Set<String> roadsToBlock = new HashSet<>();
                    findConnectedComponent(protester, protesters, graphData, visited, component);
                    int spreadCount = calculateSpread(component, graphData, protesters, roadsToBlock, blockedRoads);
                    //Choose the component with the highest spread potential or the least roads to block
                    if (spreadCount > maxSpread || (spreadCount == maxSpread && (roadsToBlockForBestComponent == null || 
                    roadsToBlock.size() < roadsToBlockForBestComponent.size()))) {
                        maxSpread = spreadCount;
                        largestComponent = component;
                        roadsToBlockForBestComponent = roadsToBlock;
                    }
                }
            }
            
            //Block roads for the largest component
            totalRoadsBlocked += roadsToBlockForBestComponent.size();
            blockedRoads.addAll(roadsToBlockForBestComponent);

            //Remove the protesters from the contained component
            protesters.removeAll(largestComponent);

            //Update protesters' positions based on unblocked roads (spread)
            protesters.addAll(updateProtesters(protesters, graphData, blockedRoads));
        }
        return totalRoadsBlocked;
    }

    //Helper method to find all intersections in the same connected component as a protester
    public void findConnectedComponent(String intersection, Set<String> protesters, HashMap<String, List<Road>> graphData, Set<String> visited, Set<String> component) {
        Queue<String> queue = new LinkedList<>();
        queue.add(intersection);
        visited.add(intersection);
        component.add(intersection);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (Road road : graphData.get(current)) {
                String neighbor = road.firstIntersection.equals(current) ? road.secondIntersection : road.firstIntersection;
                if (protesters.contains(neighbor) && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    component.add(neighbor);
                }
            }
        }
    }

    // Helper method to calculate the number of intersections a component will spread to on the next step
    public int calculateSpread(Set<String> component, HashMap<String, List<Road>> graphData, Set<String> protesters, Set<String> roadsToBlock, Set<String> blockedRoads) {
        Set<String> spreadTargets = new HashSet<>();
        for (String intersection : component) {
            for (Road road : graphData.get(intersection)) {
                String neighbor = road.firstIntersection.equals(intersection) ? road.secondIntersection : road.firstIntersection;
                
                //If the neighbor is not part of the protester set and the road is not blocked, it is a target to spread to
                if (!protesters.contains(neighbor) && !blockedRoads.contains(road.roadName)) {
                    spreadTargets.add(neighbor);
                    roadsToBlock.add(road.roadName);  // Add this road to the list of roads to block
                }
            }
        }

        return spreadTargets.size();  //Return the number of intersections the component will spread to
    }

    // Helper method to update the protester positions after they spread
    public Set<String> updateProtesters(Set<String> protesters, HashMap<String, List<Road>> graphData, Set<String> blockedRoads) {
        Set<String> newProtesters = new HashSet<>();

        for (String intersection : protesters) {
            for (Road road : graphData.get(intersection)) {
                if (!blockedRoads.contains(road.roadName)) {
                    String neighbor = road.firstIntersection.equals(intersection) ? road.secondIntersection : road.firstIntersection;
                    newProtesters.add(neighbor);  //Protesters spread to this intersection
                }
            }
        }

        return newProtesters;
    }

}

class Road {
    String roadName;
    String firstIntersection;
    String secondIntersection;
    double travelTime;

    public Road(String roadName, String firstIntersection, String secondIntersection, double travelTime) {
        this.roadName = roadName;
        this.firstIntersection = firstIntersection;
        this.secondIntersection = secondIntersection;
        this.travelTime = travelTime;
    }
}

class Graph{
    private HashMap<String, List<Road>> graph = new HashMap<>();
    private ArrayList<Road> roadList = new ArrayList<>();

    public Graph(String cityString) {
        cityString = cityString.substring(1, cityString.length() - 1);
        String[] roadArray = cityString.split("}, \\{");

        for (String road : roadArray) {
            String[] city = road.replaceAll("[{}]", "").split(", "); // Remove remaining curly braces
            String roadName = city[0];
            String firstEndpoint = city[1];
            String secondEndpoint = city[2];
            Double avgTravelTime = Double.parseDouble(city[3]);

            // Add the road to the roadList
            roadList.add(new Road(roadName, firstEndpoint, secondEndpoint, avgTravelTime));

            // Add the road to the adjacency list (graph)
            addRoad(roadName, firstEndpoint, secondEndpoint, avgTravelTime);
        }
    }

    public void addRoad(String roadName, String firstEndpoint, String secondEndpoint, double travelTime) {
        graph.putIfAbsent(firstEndpoint, new ArrayList<>());
        graph.get(firstEndpoint).add(new Road(roadName, firstEndpoint, secondEndpoint, travelTime));

        graph.putIfAbsent(secondEndpoint, new ArrayList<>());
        graph.get(secondEndpoint).add(new Road(roadName, secondEndpoint, firstEndpoint, travelTime));
    }

    public HashMap<String, List<Road>> getGraph() {
        return graph;
    }

    public ArrayList<Road> getList() {
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
        // Path compression, make the parent of the node point directly to the root
        if (!parent.get(intersection).equals(intersection)) {
            parent.put(intersection, find(parent.get(intersection)));
        }
        return parent.get(intersection);
    }

    // Union by rank
    public void union(String intersection1, String intersection2) {
        String root1 = find(intersection1);
        String root2 = find(intersection2);

        if (!root1.equals(root2)) {
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