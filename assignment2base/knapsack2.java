/** knapsack problem using dynamic programming
* @author Kieran Molner
* @author M A Hakim Newton
*/
import java.util.*;
public class knapsack2
{
    //static int inf = 999999;
    public static void main(String[] args)
    {
        HashMap<String,List<Integer>> H = new HashMap<>();
        int [] W = {1, 2, 3, 1, 1, 2, 3, 2}; // weight
        int [] S = {2, 3, 1, 4, 4, 2, 2, 1}; // size
        for(int i=1; i<=W.length;i++){
            H.putIfAbsent(String.valueOf(i), new ArrayList<>());
            H.get(String.valueOf(i)).add(S[i]);
            H.get(String.valueOf(i)).add(W[i]);
        }
        // {1, 2, 3, 1, 1, 2, 3, 2}; // weight
        // {2, 3, 1, 4, 4, 2, 2, 1}; // size
        int [] v = {5, 6, 9, 3, 7, 3, 5, 6}; // value
        int w = 10; //weight capacity
        int n = 8; //number of items
        int [][]f = new int[n+1][w+1]; // total value of the picked item
        boolean [][] p = new boolean[n+1][w+1]; // item picked or not picked
        knapsackSolution(n, w, v, H, f, p);
        System.out.println(showResult(n,w,H,f,p));
    }

    /** Solve an instance of the knapsack problem using iterative forward dynamic
    programming
    * @param items The items to be included in the knapsack
    * @param knapsackCapacity The capacity of the knapsack
    * @return A string representation of the items included in the knapsack
    */
    public static void knapsackSolution(int n, int w, int[] V, HashMap<String,List<Integer>> H, int [][] f, boolean [][] p)
    {
    // initialise the base cases
        for (int l = 0; l <= w; l++)
        {
            f[0][l] = 0; // when no item, the value is 0
            p[0][l] = false; // when no item, no item picked
        }
        for (int k = 0; k <= n; k++)
        {
            f[k][0]= 0; // when capacity is 0, the value must be 0 regardless of the item
            p[k][0] = false; // when capacity is 0, no item can be picked regardless of the item
        }
        // calculate the maximum value that can be obtained by using the first k items and a knapsack of capacity l
        for (int k = 1; k <= n; k++)
        {
            for (int l = 1; l <= w; l++)
            {
                if (l < H.get(String.valueOf(k)).get(0) || l < H.get(String.valueOf(k)).get(1)) // if the new item is too heavy to be included in the knapsack
                {
                    f[k][l] = f[k-1][l]; // item is not picked, just take solution from k-1 items
                    p[k][l] = false; // the item has not been picked
                }
                else
                {
                    int num = f[k-1][l-W[k-1]] + V[k-1];
                    //System.out.println(num);
                    f[k][l] = Math.max(f[k-1][l], num);
                    p[k][l] = (num > f[k-1][l]);
                }
            }
            
            
        }
    }
    
    /** Output the best chain of matrix multiplication
    * @param i The start node
    * @param j The end node
    * @param R The best intermediate node
    * @return The shortest path
    */
    public static String showResult(int n, int w, HashMap<String,List<Integer>> H, int[][] f, boolean[][] p)
    {
    // determine which items are included in the knapsack
        String result = "";
        result += "total value: " + f[n][w] + "\n";
        int l = w;
        for (int k = n; k >= 1; k--)
        {
            result += "item " + (k-1) + " included: " + p[k][l] + "\n";
            if (p[k][l]){
                l = l - W[k-1];
            }
                
        }
            return result;
    }
}