import java.util.*;
/**
 * backup2
 */
public class backup2 {
    public String[] cityBottleneckRoads() {
        // Your code here
        ArrayList<Road> innerCityList = getListInner();  // Get all roads in the inner city

        // Maps to keep track of the discovery time and low time of intersections
        HashMap<String, Integer> discoveryTime = new HashMap<>();
        HashMap<String, Integer> lowTime = new HashMap<>();
        HashMap<String, String> parentMap = new HashMap<>();  // To track the parent of each node during DFS
        
        Set<String> criticalRoads = new HashSet<>();  // Set to store the names of critical roads (bridges)
        Set<String> visited = new HashSet<>();  // To keep track of visited intersections

        int time = 0;  // Global timer to assign discovery times

        // Perform iterative DFS from each intersection that hasn't been visited yet
        for (Road road : innerCityList) {
            if (!visited.contains(road.firstIntersection)) {
                iterativeDFS(road.firstIntersection, discoveryTime, lowTime, parentMap, visited, time, criticalRoads, innerCityList);
            }
        }

        // Return the array of critical road names
        return criticalRoads.toArray(new String[0]);
    }

    private void iterativeDFS(String startIntersection, 
                                HashMap<String, Integer> discoveryTime, HashMap<String, Integer> lowTime, 
                                HashMap<String, String> parentMap, Set<String> visited, int time, 
                                Set<String> criticalRoads, ArrayList<Road> innerCityList) {

        Stack<String> nodeStack = new Stack<>();       // Stack to simulate DFS
        Stack<String> childStack = new Stack<>();      // Stack to track children of current node
        Stack<Iterator<Road>> roadStack = new Stack<>(); // Stack to track roads to process
        
        nodeStack.push(startIntersection);             // Push the starting intersection
        parentMap.put(startIntersection, null);        // Start node has no parent

        // Initialize discovery and low time for the start intersection
        discoveryTime.put(startIntersection, time);
        lowTime.put(startIntersection, time);
        time++;
        
        visited.add(startIntersection);                // Mark the starting node as visited

        // Iterate over all the roads connected to the starting node
        roadStack.push(getRoadIterator(startIntersection, innerCityList));  // Get the iterator for roads starting from the current node

        while (!nodeStack.isEmpty()) {
            String current = nodeStack.peek();         // Peek the current node from the stack
            Iterator<Road> roads = roadStack.peek();   // Get the current road iterator

            if (roads.hasNext()) {
                Road road = roads.next();              // Get the next road to process
                String neighbor = (road.firstIntersection.equals(current)) ? road.secondIntersection : road.firstIntersection;

                if (!visited.contains(neighbor)) {
                    // If the neighbor is not visited, process it
                    nodeStack.push(neighbor);          // Push the neighbor to the stack
                    parentMap.put(neighbor, current);  // Set the parent of the neighbor

                    // Initialize discovery and low time for the neighbor
                    discoveryTime.put(neighbor, time);
                    lowTime.put(neighbor, time);
                    time++;

                    visited.add(neighbor);             // Mark neighbor as visited

                    // Add neighbor's roads to the stack for future exploration
                    roadStack.push(getRoadIterator(neighbor, innerCityList));
                } else if (!neighbor.equals(parentMap.get(current))) {
                    // Update low time of current node based on back edge
                    lowTime.put(current, Math.min(lowTime.get(current), discoveryTime.get(neighbor)));
                }
            } else {
                // All roads from this node have been processed, backtrack
                roadStack.pop();      // Finished processing roads from this node
                nodeStack.pop();      // Pop the current node from the stack

                // Update the low time of the parent based on the current node's low time
                if (!nodeStack.isEmpty()) {
                    String parent = nodeStack.peek();  // Get the parent node (next in stack)
                    
                    lowTime.put(parent, Math.min(lowTime.get(parent), lowTime.get(current)));

                    // Check if the current node forms a bridge (bottleneck road)
                    if (lowTime.get(current) > discoveryTime.get(parent)) {
                        for (Road road : innerCityList) {
                            if ((road.firstIntersection.equals(parent) && road.secondIntersection.equals(current)) ||
                                (road.secondIntersection.equals(parent) && road.firstIntersection.equals(current))) {
                                criticalRoads.add(road.roadName);  // Add this road as a bottleneck
                            }
                        }
                    }
                }
            }
        }
    }

    // Helper method to get an iterator for roads connected to a given intersection
    private Iterator<Road> getRoadIterator(String intersection, ArrayList<Road> innerCityList) {
        List<Road> connectedRoads = new ArrayList<>();
        for (Road road : innerCityList) {
            if (road.firstIntersection.equals(intersection) || road.secondIntersection.equals(intersection)) {
                connectedRoads.add(road);
            }
        }
        return connectedRoads.iterator();
    }
    //     ArrayList<Road> innerCityList = getListInner();
    //     int innerCityRoad =0;
    //     //bottleneckRoad only consider inner city
    //     //a stack of vertices, cross out node if traversed, backtrack if visited the node
    //     Stack<Road> roadStack = new Stack<>();
    //     for(Road road : innerCityList){
    //         roadStack.add(road);
    //         innerCityRoad++;
    //     }
    //     Vector<Boolean> visited = new Vector<Boolean>(innerCityRoad);
    //     for (int i = 0; i < innerCityRoad; i++){
    //         visited.add(false);
    //     }
    //     roadStack.push(innerCityList.get(0));
    //     while(roadStack.empty() ==false){
    //         //roadStack.pop();
    //         Road r = roadStack.peek();
    //         System.out.println(roadStack.pop());
    //     }


    //     throw new UnsupportedOperationException("Not implemented yet"); // Get rid of this line when you start implementing
    // }

    public ArrayList<Road> getListInner(){
        DisjointSet disjointSet = createDisjointSet();
        String largestGroupRoot = getInnerCity();
        ArrayList<Road> innerCityList = new ArrayList<>();

        for(Road road : graph.getList()){
            if(disjointSet.find(road.firstIntersection).equals(largestGroupRoot) && disjointSet.find(road.secondIntersection).equals(largestGroupRoot)){
                innerCityList.add(road);
            }
        }
        return innerCityList;
    }
    
}
