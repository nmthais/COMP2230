

/** knapsack problem using dynamic programming
* @author Kieran Molner
* @author M A Hakim Newton
*/
public class knapsack3
{
    //static int inf = 999999;
    public static void main(String[] args)
    {
        int [] W = {1, 2, 3, 1, 1, 2, 3, 2}; // weight
        int [] S = {2, 3, 1, 4, 4, 2, 2, 1}; // size
        int [] v = {5, 6, 9, 3, 7, 3, 5, 6}; // value
        int s = 10;
        int w = 10; //weight capacity
        int n = 8; //number of items
        int [][][] f = new int[n+1][w+1][s+1]; // total value of the picked item
        boolean [][][] p = new boolean[n+1][w+1][s+1]; // item picked or not picked
        knapsackSolution(n, w, s, v, W, S, f, p);
        System.out.println(showResult(n,w,s,W,S,f,p));
    }

    /** Solve an instance of the knapsack problem using iterative forward dynamic
    programming
    * @param items The items to be included in the knapsack
    * @param knapsackCapacity The capacity of the knapsack
    * @return A string representation of the items included in the knapsack
    */
    public static void knapsackSolution(int n, int w, int s, int[] V, int[]W, int[]S, int [][][] f, boolean [][][] p)
    {
    // initialise the base cases
        int[] temp = V.clone();
        for (int l = 0; l <= w; l++)
        {
            f[0][l][0] = 0; // when no item, the value is 0
            p[0][l][0] = false; // when no item, no item picked
        }
        for (int k = 0; k <= n; k++)
        {
            f[k][0][0] = 0; // when capacity is 0, the value must be 0 regardless of the item
            p[k][0][0] = false; // when capacity is 0, no item can be picked regardless of the item
        }
        for(int m=0; m <= s; m++){
            f[0][0][m] = 0; //when size =0, value=0
            p[0][0][m] = false; // no item can be picked
        }
        // calculate the maximum value that can be obtained by using the first k items and a knapsack of capacity l
        for (int k = 1; k <= n; k++)
        {
            for(int m =1; m <= s; m++)
            {
                for (int l = 1; l <= w; l++)
                {
                    if (l < W[k-1] || m < S[k-1]) // if the new item is too heavy to be included in the knapsack
                    {
                        f[k][l][m] = f[k-1][l][m]; // item is not picked, just take solution from k-1 items
                        p[k][l][m] = false; // the item has not been picked
                    }
                    else
                    {
                        // int tempTotal = f[k-1][l-W[k-1]][m - S[k-1]] + temp[k-1];
                        // int num = f[k-1][l-W[k-1]][m - S[k-1]] + V[k-1];
                        // int bestTotal = Math.max(tempTotal, num);
                        int bestTotal = f[k-1][l-W[k-1]][m - S[k-1]] + V[k-1];
                        //System.out.println(num);
                        if(bestTotal > f[k-1][l][m]){
                            f[k][l][m] = bestTotal;
                            p[k][l][m] = true; 
                            // if(bestTotal == tempTotal){
                            //     temp = update(temp, k);
                            //     V = temp;
                            // }
                        }
                        else{
                            f[k][l][m] = Math.max(f[k-1][l][m], bestTotal);
                            p[k][l][m] = (bestTotal > f[k-1][l][m]);
                        }
                    }
                }
            }
            //update(V, k);
            //System.out.print(f[k][w][s] + " ");
            String output ="";
            for(int i: V){
                output+= i + " ";
            }
            output += "";
            System.out.println( "\n" +output + f[k][w][s]);            
        }
    }
    
    /** Output the best chain of matrix multiplication
    * @param i The start node
    * @param j The end node
    * @param R The best intermediate node
    * @return The shortest path
    */
    public static String showResult(int n, int w, int s, int [] W, int []S, int[][][] f, boolean[][][] p)
    {
    // determine which items are included in the knapsack
        String result = "";
        result += "total value: " + f[n][w][s] + "\n";
        int l = w;
        int m = s;
        for (int k = n; k >= 1; k--)
        {
            result += "item " + (k-1) + " included: " + p[k][l][m] + "\n";
            if (p[k][l][m]){
                l = l - W[k-1];
                m = m - S[k-1];
            }
                
        }
            return result;
    }

    public static int[] update(int[]v, int k){
        switch (k) {
            case 1 -> {
                v[1] -=3;
                v[2] -=2;
            }
            case 2->{
                //v[0] -=3;
                v[4] -=3;
            }
            case 3 -> {
                //v[0] -=2;
                v[3] -=2;
                v[5] -=1;
                v[6] -=4;
            }
            case 4 -> {
                //v[2] -=2;
                v[4] -=1;
            }
            case 5 -> {
                //v[1] -=3;
               // v[3] -=1;
                v[7] -=3;
            }
            case 6 -> {
                //v[2] -= 1;
                v[7] -=2;
            }
            case 7 -> {
                //v[2] -=4;
                v[7] -=1;
            }
            case 8 -> {
                //v[4] -=3;
                //v[5] -=2;
                //v[6] -=1;
            }
            default -> {
                
            }
        }
        for (int i = 0; i < v.length; i++) {
            System.out.print(v[i] + "   ");
        }
        int [] newV = v.clone();

        return newV;
    }
}