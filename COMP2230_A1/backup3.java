public int containProtests(String[] intersectionNames){
HashMap<String, List<Road>> graphData = graph.getGraph();
        Set<String> protesters = new HashSet<>(Arrays.asList(intersectionNames));  // Current locations of protesters
        Set<String> blockedRoads = new HashSet<>();  // Roads that have already been blocked
        int totalRoadsBlocked = 0;

        // Continue until all protests are contained
        while (!protesters.isEmpty()) {
            Set<String> visited = new HashSet<>();
            Set<String> largestComponent = null;
            int maxSpread = -1;
            Set<String> roadsToBlockForBestComponent = null;

            // Step 1: Find all protester connected components and calculate spread potential
            for (String protester : protesters) {
                if (!visited.contains(protester)) {
                    Set<String> component = new HashSet<>();
                    Set<String> roadsToBlock = new HashSet<>();
                    findConnectedComponent(protester, protesters, graphData, visited, component);
                    int spreadCount = calculateSpread(component, graphData, protesters, roadsToBlock, blockedRoads);

                    // Step 2: Choose the component with the highest spread potential or the least roads to block
                    if (spreadCount > maxSpread || (spreadCount == maxSpread && (roadsToBlockForBestComponent == null || roadsToBlock.size() < roadsToBlockForBestComponent.size()))) {
                        maxSpread = spreadCount;
                        largestComponent = component;
                        roadsToBlockForBestComponent = roadsToBlock;
                    }
                }
            }

            // Step 3: Block roads for the largest component
            totalRoadsBlocked += roadsToBlockForBestComponent.size();
            blockedRoads.addAll(roadsToBlockForBestComponent);

            // Step 4: Remove the protesters from the contained component
            protesters.removeAll(largestComponent);

            // Step 5: Update protesters' positions based on unblocked roads (spread)
            protesters = updateProtesters(protesters, graphData, blockedRoads);
        }

        return totalRoadsBlocked;
    }

    // Helper method to find all intersections in the same connected component as a protester
    private void findConnectedComponent(String intersection, Set<String> protesters, HashMap<String, List<Road>> graphData, Set<String> visited, Set<String> component) {
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
    private int calculateSpread(Set<String> component, HashMap<String, List<Road>> graphData, Set<String> protesters, Set<String> roadsToBlock, Set<String> blockedRoads) {
        Set<String> spreadTargets = new HashSet<>();

        for (String intersection : component) {
            for (Road road : graphData.get(intersection)) {
                String neighbor = road.firstIntersection.equals(intersection) ? road.secondIntersection : road.firstIntersection;

                // If the neighbor is not part of the protester set and the road is not blocked, it is a target to spread to
                if (!protesters.contains(neighbor) && !blockedRoads.contains(road.roadName)) {
                    spreadTargets.add(neighbor);
                    roadsToBlock.add(road.roadName);  // Add this road to the list of roads to block
                }
            }
        }

        return spreadTargets.size();  // Return the number of intersections the component will spread to
    }

    // Helper method to update the protester positions after they spread
    private Set<String> updateProtesters(Set<String> protesters, HashMap<String, List<Road>> graphData, Set<String> blockedRoads) {
        Set<String> newProtesters = new HashSet<>();

        for (String intersection : protesters) {
            for (Road road : graphData.get(intersection)) {
                if (!blockedRoads.contains(road.roadName)) {
                    String neighbor = road.firstIntersection.equals(intersection) ? road.secondIntersection : road.firstIntersection;
                    newProtesters.add(neighbor);  // Protesters spread to this intersection
                }
            }
        }

        return newProtesters;
    }
