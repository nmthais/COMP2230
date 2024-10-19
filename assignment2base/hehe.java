public class hehe{
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
        // for(String str: answer){
        //     System.out.println(str);
        // }

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

