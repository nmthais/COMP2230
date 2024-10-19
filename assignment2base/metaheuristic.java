import java.util.Random;

/** hill climbing for knapsack problem
 * @author M A Hakim Newton
 */
  
class metaheuristic 
{
	static int bigM = 99;	// to put weight on constraint violation than on value

	static int capacity = 10;		// knapsack capacity
	static int itemCount = 5;		// the number of items

	static int [] weight = {1, 1, 2, 4, 8};		// item weights
	static int [] value = {1, 2, 3, 10, 4};		// item values

	// print solution
	static void printSolution(boolean [] solution)
	{
		System.out.print("solution: ");
		for(int i = 0; i < itemCount; i++)
			System.out.print(" " + solution[i]);
		System.out.println(); 
	}

	// print the score
	static void printScore(int [] score)
	{
		System.out.print("total: " + score[0]);
		System.out.print(" weight: " + score[1]);
		System.out.println(" value: " + score[2]);
	}

	// initialise the solution randomly
	static boolean[] initialiseRandom(Random rng)
	{
		boolean [] solution = new boolean[itemCount];
		for(int i = 0; i < itemCount; i++)
			solution[i] = rng.nextInt(100) % 2 == 0;
		return solution;
	}

	// compute the score of a solution from scratch
	// score computed assuming a minimisation problem
	static int [] evaluateAnew(boolean [] solution)
	{
		int [] score = new int[3];
		for(int i = 0; i < itemCount; i++)
			if (solution[i] == true)
			{
				score[1] += weight[i];
				score[2] += value[i]; 
			}
		// violation gets higher priority as gets multiplied by bigM
		// value is subtracted in a minimisation score because value to maximised
		score[0] = Math.max(0, score[1] - capacity) * bigM - score[2];
		return score;
	}

	// generate a flip move randomly
	static int randomFlipMove(Random rng)
	{
		return rng.nextInt(itemCount);	
	}

	// apply a flip move to a solution and compute score incrementally
	static void applyFlipMove(boolean [] solution, int [] score, int itemFlipped)
	{
		if (solution[itemFlipped] == true)
		{
			score[1] -= weight[itemFlipped];
			score[2] -= value[itemFlipped];
			score[0] = Math.max(0, score[1] - capacity) * bigM - score[2];
			solution[itemFlipped] = false;	
		}
		else
		{
			score[1] += weight[itemFlipped];
			score[2] += value[itemFlipped];
			score[0] = Math.max(0, score[1] - capacity) * bigM - score[2];
			solution[itemFlipped] = true;	
		}
	}
	
	// just evaluate a flip move and compute the total score, do not apply the move
	static int[] evaluateIncrFlip(boolean [] solution, int [] score, int itemFlipped)
	{
		int [] scoreFlipped = score.clone();

		if (solution[itemFlipped] == true)
		{
			scoreFlipped[1] -= weight[itemFlipped];
			scoreFlipped[2] -= value[itemFlipped];
			scoreFlipped[0] = Math.max(0, scoreFlipped[1] - capacity) * bigM - scoreFlipped[2];
		}
		else
		{
			scoreFlipped[1] += weight[itemFlipped];
			scoreFlipped[2] += value[itemFlipped];
			scoreFlipped[0] = Math.max(0, scoreFlipped[1] - capacity) * bigM - scoreFlipped[2];
		}
		return scoreFlipped;
	}

	// perform hill climbing search
	static void search(Random rng, int maxIter)
	{
		// initialise and compute the score
		boolean [] solution = initialiseRandom(rng);
		int [] score = evaluateAnew(solution);

		// save as the best solution and the best score
		boolean [] bestSolution = solution.clone();
		int [] bestScore = score.clone();

		// display the best solution and the best score
		printSolution(bestSolution);
		printScore(bestScore);

		// run the search algorithm for maxIter iterations
		for(int iter = 0; iter < maxIter; iter++)
		{
			// generate a flip move randomly
			int itemToBeFlipped = randomFlipMove(rng);
			System.out.print("flip " + itemToBeFlipped);

			// calculate the score temporarily for the flip
			int [] scoreAfterFlip = evaluateIncrFlip(solution, score, itemToBeFlipped);
			System.out.print(" old score: " + score[0] + " new score: " + scoreAfterFlip[0]);

			// if the flip score is better
			if (scoreAfterFlip[0] < score[0])
			{
				System.out.print(" move accepted");
				// accept the move
				applyFlipMove(solution, score, itemToBeFlipped);

				// check if a new best score is found
				if (score[0] < bestScore[0])
				{
					// save and display the new best solution and score
					System.out.println(" new best");
					bestSolution = solution.clone();
					bestScore = score.clone();
					printSolution(bestSolution);
					printScore(bestScore);
				}
				else
					System.out.println();
			}
			else
				System.out.println(" move rejected");
		}
		printSolution(bestSolution);
		printScore(bestScore);
	}

  /** main function
	 * @param args array of string arguments
	 */ 
  public static void main(String[] args) 
	{
		Random rng = new Random();
		search(rng, 10);
	} 
}
