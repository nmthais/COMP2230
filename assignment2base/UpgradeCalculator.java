/*
 * COMP2230 - Algorithms
 * Assignment 2 - Main
 * @author  Minh Thai Nguyen - c3440776 
 * @version 1.0
 * 
 * This is where you will write your code for the assignment. The required methods have been started for you, you may add additional helper methods and classes as required.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class UpgradeCalculator {
    private final MapGenerator mapGen;
    public String [] upgradeData;
    public UpgradeCheck checker;
    public String cityMap = null;
    private Graph graph = null;
    
    public UpgradeCalculator(int seed){
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
            cityMap = mapGen.generateMap(10); // You can optionally pass in an integer here to set the size of the largest connected component in the map
        }

        // Write your code below this line - just copy from your assignment 1 solution, it's the same map generator
        graph = new Graph(cityMap);
    }

    /*
     * @param budgetLimit the maximum amount of money that can be spent on road upgrades
     * @param timeLimit the maximum amount of time that can be spent on road upgrades
     * 
     * @return an array of strings, each string is the name of an intersection that should be upgraded
     * 
     * This method must use a dynamic programming approach to solve the problem
     */
    public String [] dynamicProgrammingSolver(int budgetLimit, int timeLimit){  //maybe try 2D array with budget and time, find the biggest upgrade possible
        if (upgradeData == null) {
            upgradeData = checker.upgradeAnalyser();
        }
        // Write your code below this line
        //System.out.println(budgetLimit +" " + timeLimit);

        // timeLimit =9;
        // budgetLimit=9;
        UpgradeCity upgrade = new UpgradeCity(upgradeData);
        int totalItem = graph.getGraph().size();
        //int budgetLimit
        //int timeLimit
        int [] budgetArray = upgrade.getBudgetArray();
        int [] timeArray = upgrade.getTimeArray();
        int [] value = graph.getImprovementArray();                      //try the sum of values list, change the values accordingly when an intersection is picked
        int [][][] totalV = new int[totalItem+1][budgetLimit+1][timeLimit+1]; //total value of picked items
        boolean [][][] pickedItems = new boolean[totalItem+1][budgetLimit+1][timeLimit+1];
          
        return dpSub(totalItem, budgetLimit, timeLimit, budgetArray, timeArray, value, totalV, pickedItems);
        //throw new UnsupportedOperationException("Not implemented yet."); // Remove this line when you implement this method
    }

    public String [] dpSub(int totalItem, int budgetLimit, int timeLimit, int [] budgetArray, 
                        int [] timeArray, int [] value, int [][][] totalV, boolean [][][] pickedItems){
        ArrayList<String> answer = new ArrayList<>();
        for(int l=0; l<= budgetLimit;l++){          //base case when budget ==0
            totalV[0][l][0] =0;
            pickedItems[0][l][0]=false;
        }
        for(int k=0; k<= totalItem;k++){          //base case when no items 
            totalV[k][0][0] =0;
            pickedItems[k][0][0]=false;
        }
        for(int m=0; m<= timeLimit;m++){          //base case when time ==0
            totalV[0][0][m] =0;
            pickedItems[0][0][m]=false;
        }
        for(int k=1; k<=totalItem;k++){            // looping through the intersection list
            for(int m=1 ;m<=timeLimit;m++){
                for(int l=1; l<=budgetLimit;l++){
                    if(l < budgetArray[k-1] || m < timeArray[k-1]){
                        totalV[k][l][m] = totalV[k - 1][l][m];
                        pickedItems[k][l][m] = false;
                    }
                    else {
                        int [] tempV = value.clone();
                        int num;
                        int budget = l - budgetArray[k-1];
                        int time = m - timeArray[k-1];
                        
                        tempV = updateValueArrayDP(k-1, budget, time, budgetArray, timeArray, pickedItems, value);
                        num = totalV[k - 1][budget][time] + tempV[k-1];

                        if (num > totalV[k - 1][l][m]) {
                            totalV[k][l][m] = num;
                            pickedItems[k][l][m] = true;
                        } else {
                            totalV[k][l][m] = totalV[k - 1][l][m];
                            pickedItems[k][l][m] = false;
                        }

                    }
                }
            }
        }
        answer = showResult(totalItem, budgetLimit, timeLimit, budgetArray, timeArray, totalV, pickedItems, answer);
        //printMatrices(totalV, totalItem, budgetLimit, timeLimit);
        System.out.println("total value: " + totalV[totalItem][budgetLimit][timeLimit]);
        // for(String str: answer){
        //    System.out.println(str + " time " + ); 
        // }
        
        return answer.toArray(new String[0]);
    }

    public int[] updateValueArrayDP(int k, int l, int m, int[] L, int []M, boolean [][][] p, int [] value){
        String [] Intersections = graph.getIntersectionsArray();
        HashMap<String, List<Road>> graphData = graph.getGraph();
        int [] modifiedV = value.clone();
        ArrayList<Integer> testing = new ArrayList<>();
        int newl= l;
        int newm = m;
        boolean check = false;
        
        for (int i = k; i >= 1; i--)
        {
            if (p[i][newl][newm]){
                check = true;
                testing.add(i-1);
                newl = newl- L[i-1];
                newm = newm - M[i-1];
            }
            
        }
        if(check){
            for(int i : testing){
                for (Road road : graphData.get(Intersections[i])) {
                    String otherIntersection = road.getFirstIntersection().equals(Intersections[i]) ? road.getSecondIntersection() : road.getFirstIntersection();
                    int o = Arrays.binarySearch(Intersections, otherIntersection);
                    modifiedV[o] -= road.getTravelTime(); // Deduct travel time from neighboring intersection
                }
            }  
        }
                
        return modifiedV;
    }

    public ArrayList<String> showResult(int n, int w, int s, int [] W, int []S, int[][][] f, boolean[][][] p, ArrayList<String> answer)
    {
        int l = w;
        int m = s;
        for (int k = n; k >= 1; k--)
        {
            if (p[k][l][m]){
                answer.add(graph.getIntersectionsArray()[k-1]);
                l = l - W[k-1];
                m = m - S[k-1];
                System.out.println("Picked item " + (k - 1) + " Budget remaining: " + l + " Time remaining: " + m);       
            }
        }
            return answer;
    }

    public void printMatrices(int[][][] totalV, int totalItem, int budgetLimit, int timeLimit) {
        // We assume the totalV has dimensions at least 10x10x10
        int matricesToPrint = totalItem; // Number of matrices you want to print (maximum of 8 in this case)
        int maxtrixRows = budgetLimit;     // Size of each matrix (10x10)
        int matrixCols = timeLimit;
    
        // Print matrices for different slices of the totalItem dimension
        for (int k = 1; k <= matricesToPrint; k++) {
            //System.out.println("Matrix for totalItem = " + k);
            for (int b = 0; b <= maxtrixRows; b++) {
                for (int t = 0; t <= matrixCols; t++) {
                    System.out.print(totalV[k][b][t] + "\t");  // Print the value in totalV
                    //System.out.print(totalV[totalItem][b][t] + "\t");
                }
                System.out.println(); // Move to the next line after each row
            }
            System.out.println(); // Separate matrices by an empty line
        }
    }

    /*
     * @param budgetLimit the maximum amount of money that can be spent on road upgrades
     * @param timeLimit the maximum amount of time that can be spent on road upgrades
     * 
     * @return an array of strings, each string is the name of an intersection that should be upgraded
     * 
     * This method must use a heuristic algorithm to solve the problem
     */
    public String [] heuristicSolver(int budgetLimit, int timeLimit){// value / (money + time)
        if (upgradeData == null) {
            upgradeData = checker.upgradeAnalyser();
        }
        // Write your code below this line
        UpgradeCity upgrade = new UpgradeCity(upgradeData);
        int totalItem = graph.getGraph().size();
        //int budgetLimit
        //int timeLimit
        int [] budgetArray = upgrade.getBudgetArray();
        int [] timeArray = upgrade.getTimeArray();
        int [] value = graph.getImprovementArray();
        String [] intersections = graph.getIntersectionsArray();
        boolean [] pickedIntersections = new boolean[intersections.length];

        return heuristicSub(totalItem, budgetLimit, timeLimit, budgetArray, timeArray, value, intersections, pickedIntersections);
        //throw new UnsupportedOperationException("Not implemented yet."); // Remove this line when you implement this method
    }

    public String [] heuristicSub(int totalItem, int budgetLimit, int timeLimit, int[]budgetArray, int[] timeArray, int [] value, String [] intersections, boolean [] pickedItems){
        ArrayList<String> answer = new ArrayList<>();
        int indexMax;

        while(budgetLimit >0 && timeLimit >0){
            indexMax = getIndexMax(totalItem, budgetLimit, timeLimit, budgetArray, timeArray, value, pickedItems);
            pickedItems[indexMax] = true;
            budgetLimit -= budgetArray[indexMax];
            timeLimit -= timeArray[indexMax];
            updateValueArrayH(indexMax, value);
            answer.add(intersections[indexMax]);
            //System.out.println(budgetLimit + timeLimit);
        }
        // for(String str: answer){
        //     System.out.println(str);
        // }
        
        return answer.toArray(new String [0]);
    }

    public int getIndexMax(int totalItem, int budgetLimit, int timeLimit ,int [] budgetArray, int [] timeArray, int [] value, boolean [] pickedItems){
        int maxIndex=0; 
        int comparator=0;
        boolean [] check = new boolean[totalItem];

        for(int i=0; i<pickedItems.length;i++){                                         // check budget and time need to fix this intersection is in bound
            check[i] = !(budgetLimit < budgetArray[i] || timeLimit < timeArray[i]);
        }
        for(int i=0;i<value.length;i++){
            if((value[i] / budgetArray[i]) + (value[i] + timeArray[i]) > comparator && check[i] && !pickedItems[i]){    // if its in bound, item is not picked and its has largest value budget time ratio then pick
                comparator = value[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public int[] updateValueArrayH(int k, int [] value){
        String [] Intersections = graph.getIntersectionsArray();
        HashMap<String, List<Road>> graphData = graph.getGraph();
        
        for (Road road : graphData.get(Intersections[k])) {
            String otherIntersection = road.getFirstIntersection().equals(Intersections[k]) ? road.getSecondIntersection() : road.getFirstIntersection();
            int i = Arrays.binarySearch(Intersections, otherIntersection);
            value[i] -= road.getTravelTime(); // Deduct travel time from neighboring intersection
        }
        // for (int i = 0; i < value.length; i++) {
        //     System.out.print(value[i] + " ");
        // }
        return value;
    }


    /*
     * @param budgetLimit the maximum amount of money that can be spent on road upgrades
     * @param timeLimit the maximum amount of time that can be spent on road upgrades
     * 
     * @return an array of strings, each string is the name of an intersection that should be upgraded
     * 
     * This method must use a metaheuristic hill climbing algorithm to solve the problem
     */
    public String [] hillClimbingSolver(int budgetLimit, int timeLimit){
        if (upgradeData == null) {
            upgradeData = checker.upgradeAnalyser();
        }
        // Write your code below this line
        UpgradeCity upgrade = new UpgradeCity(upgradeData);
        int totalItem = graph.getGraph().size();
        //int budgetLimit
        //int timeLimit
        int [] budgetArray = upgrade.getBudgetArray();
        int [] timeArray = upgrade.getTimeArray();
        int [] value = graph.getImprovementArray();
        String [] intersections = graph.getIntersectionsArray();
        Random rng = new Random();

        return subMH(rng, totalItem, budgetLimit, timeLimit, budgetArray, timeArray, value, intersections);
    }

    public String[] subMH(Random rng, int totalItem, int budgetLimit, int timeLimit, int [] budgetArray, int [] timeArray, int [] value, String [] intersections){
        ArrayList<String> answer = new ArrayList<>();
        int iteration = 100;
        int constraintV = 99;
        boolean [] solution = randomSol(totalItem, rng);
        //boolean [] solution = {false,false,true,false,false,false,false,false,false};
        int [] score = evaluateNew(rng, constraintV, totalItem, budgetLimit, timeLimit, solution, budgetArray, timeArray, value);

        boolean[] bestSolution = solution.clone();
        int [] bestScore = score.clone();

        for(int i=0; i< iteration; i++){
            int itemFlip = rng.nextInt(totalItem);

            int [] scoreFlipped = evaluateFlip(constraintV, budgetLimit, timeLimit, budgetArray, timeArray, solution, score, itemFlip);

            if(scoreFlipped[0] < score[0] && scoreFlipped[1] < score[1]){
                //System.out.println("Yes");
                score = flip(constraintV, budgetLimit, timeLimit, budgetArray, timeArray, solution, score, itemFlip);

                if(score[0] < bestScore[0] && score[1] < bestScore[1]){
                    //System.out.println("New best");
                    bestSolution = solution.clone();
                    bestScore = score.clone();
                }
            }
            else{
                //System.out.println("NO");
            }
        }

        for(int i=0; i< bestSolution.length; i++){
            if(bestSolution[i]){
                answer.add(intersections[i]);
            }
        }

        return answer.toArray(new String[0]);
    }

    public int[] updateValueArrayMH(int k, int [] value, boolean flip){
        String [] Intersections = graph.getIntersectionsArray();
        HashMap<String, List<Road>> graphData = graph.getGraph();
        
        for (Road road : graphData.get(Intersections[k])) {
            String otherIntersection = road.getFirstIntersection().equals(Intersections[k]) ? road.getSecondIntersection() : road.getFirstIntersection();
            int i = Arrays.binarySearch(Intersections, otherIntersection);
            if (flip) {
                value[i] += road.getTravelTime();
            }
            else{
                value[i] -= road.getTravelTime(); // Deduct travel time from neighboring intersection
            }
        }
        
        return value;   
    }

    public boolean [] randomSol(int totalItem, Random rng){
        boolean [] sol = new boolean[totalItem];
        for(int i=0; i< sol.length; i++){
            sol[i] = rng.nextInt(100) % 2 ==0;
        }
        return sol;
    }

    public int [] evaluateNew(Random rng, int constraintV, int totalItem, int budgetLimit, int timeLimit, boolean [] sol, int [] budgetArray, int [] timeArray, int [] value){
        int [] score = new int[5];

        for(int i=0; i< totalItem; i++){
            if(sol[i]){
                score[2] += budgetArray[i];
                score[3] += timeArray[i];
                score[4] += value[i];
                updateValueArrayMH(i, value, false);
            }
            
        }
        score[0] = Math.max(0, score[2] - budgetLimit) * constraintV - score[4];
        score[1] = Math.max(0, score[3] - timeLimit) * constraintV - score[4];

        while (score[0] > 0 || score[1] > 0) {
            // Randomly pick an item to flip off (set to false) to reduce constraints
            int itemToFlip = rng.nextInt(totalItem);
    
            if (sol[itemToFlip]) {
                // Flip the item off
                sol[itemToFlip] = false;
                // Subtract the item's budget, time, and value
                score[2] -= budgetArray[itemToFlip];
                score[3] -= timeArray[itemToFlip];
                score[4] -= value[itemToFlip];
            }
    
            // Recalculate penalties
            score[0] = Math.max(0, score[2] - budgetLimit) * constraintV - score[4];
            score[1] = Math.max(0, score[3] - timeLimit) * constraintV - score[4];
    
            // Stop if all items are flipped off
            boolean allFalse = true;
            for (boolean b : sol) {
                if (b) {
                    allFalse = false;
                    break;
                }
            }
            if (allFalse) {
                break;
            }
        }    

        return score;
    }

    public int[] flip(int constraintV, int budgetLimit, int timeLimit, int[] budgetArray, int[] timeArray, boolean[] sol, int[] score, int itemFlip) {
        int newBudget = score[2];
        int newTime = score[3];
    
        if (sol[itemFlip]) {
            // If the item was selected, we are flipping it off
            newBudget -= budgetArray[itemFlip];
            newTime -= timeArray[itemFlip];
        } else {
            // If the item was not selected, we are flipping it on
            newBudget += budgetArray[itemFlip];
            newTime += timeArray[itemFlip];
        }
    
        // Check if the new budget and time exceed the limit too much
        if (newBudget > budgetLimit  || newTime > timeLimit) {
            // Discard the flip if it exceeds the threshold limits
            return score;
        }
    
        // Perform the flip
        score[2] = newBudget;
        score[3] = newTime;
    
        // Recalculate the score based on the new budget and time
        score[0] = Math.max(0, score[2] - budgetLimit) + constraintV - score[4];
        score[1] = Math.max(0, score[3] - timeLimit) + constraintV - score[4];
    
        // Flip the solution
        sol[itemFlip] = !sol[itemFlip];
    
        return score;
    }
    

    public int[] evaluateFlip(int constraintV, int budgetLimit, int timeLimit, int[] budgetArray, int[] timeArray, boolean[] sol, int[] score, int itemFlip) {
        int[] scoreFlip = score.clone();
        int newBudget = scoreFlip[2];
        int newTime = scoreFlip[3];
        
    
        if (sol[itemFlip]) {
            // Flipping off the current item
            newBudget -= budgetArray[itemFlip];
            newTime -= timeArray[itemFlip];
        } else {
            // Flipping on the current item
            newBudget += budgetArray[itemFlip];
            newTime += timeArray[itemFlip];
        }
    
        // Check if the new budget and time exceed the limit too much
        if (newBudget > budgetLimit || newTime > timeLimit) {
            
            return score;
        }
    
        // Calculate the flipped score
        scoreFlip[2] = newBudget;
        scoreFlip[3] = newTime;
    
        // Recalculate score considering the constraints
        scoreFlip[0] = Math.max(0, scoreFlip[2] - budgetLimit) + constraintV - scoreFlip[4];
        scoreFlip[1] = Math.max(0, scoreFlip[3] - timeLimit) + constraintV - scoreFlip[4];
    
        return scoreFlip;
    }
}

class Road {
    private String roadName;
    private String firstIntersection;
    private String secondIntersection;
    private int travelTime;

    public Road(String roadName, String firstIntersection, String secondIntersection, int travelTime) {
        this.roadName = roadName;
        this.firstIntersection = firstIntersection;
        this.secondIntersection = secondIntersection;
        this.travelTime = travelTime;
    }
    public int getTravelTime() {
        return travelTime;
    }
    public String getFirstIntersection() {
        return firstIntersection;
    }
    public String getSecondIntersection() {
        return secondIntersection;
    }
    public String getRoadName() {
        return roadName;
    }
}


class Graph{
    private final HashMap<String, List<Road>> graph = new HashMap<>();
    private ArrayList<Road> roadList = new ArrayList<>();

    public Graph(String cityString) {
        cityString = cityString.substring(1, cityString.length() - 1);          // get 
        String[] roadArray = cityString.split("}, \\{");

        for (String road : roadArray) {
            String[] city = road.replaceAll("[{}]", "").split(", "); // Remove remaining curly braces
            String roadName = city[0];
            String firstEndpoint = city[1];
            String secondEndpoint = city[2];
            int avgTravelTime = (int)(Double.parseDouble(city[3]));

            // Add the road to the roadList
            roadList.add(new Road(roadName, firstEndpoint, secondEndpoint, avgTravelTime));

            // Add the road to the adjacency list (graph)
            addRoad(roadName, firstEndpoint, secondEndpoint, avgTravelTime);
        }
    }

    public void addRoad(String roadName, String firstEndpoint, String secondEndpoint, int travelTime) {
        graph.putIfAbsent(firstEndpoint, new ArrayList<>());
        graph.get(firstEndpoint).add(new Road(roadName, firstEndpoint, secondEndpoint, travelTime));

        graph.putIfAbsent(secondEndpoint, new ArrayList<>());
        graph.get(secondEndpoint).add(new Road(roadName, secondEndpoint, firstEndpoint, travelTime));
    }

    public HashMap<String, List<Road>> getGraph() {
        return graph;
    }

    public ArrayList<Road> getRoadList() {
        return roadList;
    }

    public String[] getIntersectionsArray(){
        int o=0;
        String[] Intersections = new String[graph.keySet().size()];
        for(String str : graph.keySet()){
            Intersections[o] = str;
            o++;
        }
        Arrays.sort(Intersections);

        return Intersections;
    }

    public int[] getImprovementArray(){
        String [] Intersections = getIntersectionsArray();
        int[] value = new int[Intersections.length];
        for(int i=0;i<value.length;i++){
            for(Road road : getRoadList()){
                if(Intersections[i].equals(road.getFirstIntersection()) || Intersections[i].equals(road.getSecondIntersection())){
                    value[i] += road.getTravelTime();
                }
            }
        }

        return value;
    }
}

class UpgradeCity{
    int [] budgetArray;
    int [] timeArray;

    public UpgradeCity(String[] upgradeList) {
        this.budgetArray = new int[upgradeList.length];
        this.timeArray = new int[upgradeList.length];

        for(int i=0; i<upgradeList.length; i++){
            String[] parts = upgradeList[i].split(", ");
            
            budgetArray[i] = Integer.parseInt(parts[1]);
            timeArray[i] = Integer.parseInt(parts[2]);
        }
    }

    public int[] getBudgetArray() {
        return budgetArray;
    }

    public int[] getTimeArray() {
        return timeArray;
    }
    
}