package packageone;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Stack;

public class LabThreeBibartite {
 
	 static final int blank = 0;
	 static final int red = 1;
	 static final int blue = 2;
    static private Stack<Integer> mystack;
   
    public static boolean isBipartite(int adjacencyMatrix[][], int vertices)
    {
    	  int[] colorset = new int[vertices];
          for (int i = 1; i <= vertices; i++)
          {
              colorset[i] = blank;
          }
          int temp=1;
          mystack.push(temp);
          colorset[temp] = red;
          int current = temp;
          int adjacent = temp;
          
          while (!mystack.empty())
	        {
	            current = mystack.peek();
	            adjacent = current;
	            while (adjacent < vertices)
	            {
	                if (adjacencyMatrix[current][adjacent] == 1&& colorset[adjacent] == colorset[current])
	                {
	                    return false;
	                }
	                if (adjacencyMatrix[current][adjacent] == 1 && colorset[adjacent] == blank)
	                {
	                	if(colorset[current] == red) {	
	                		colorset[adjacent] = blue;
	                	}
	                	else{ 
	                		colorset[adjacent] = red;
	                	}
	                  mystack.push(adjacent);
	                  current = adjacent;
	                  adjacent = 1;
	                  continue;
	                }
	                adjacent++;
	            }
	            mystack.pop();
	        }
        return true;
    }
 
    public static void main(String[]arg)
    {
        int numVertices;
        int source=1;
        Scanner scanner = null;
        try 
        {
           System.out.println("Enter the number of vertices in the graph");
           scanner = new Scanner(System.in);
           numVertices = scanner.nextInt();
 
           int adjacency_matrix[][] = new int[numVertices+1][numVertices+1];
           System.out.println("Enter the adjacency matrix:");
           
           for ( int i = 1 ; i <= numVertices ; i++ ){  
  	         for (int j = 1 ; j <= numVertices ; j++ ){
  	        	 
  	        	 System.out.print("Element [" + (i-1) +"][" +( j-1)+ "]:");
  	        	adjacency_matrix[i][j] = scanner.nextInt();
  	       	
  	         }
           }

           if (isBipartite(adjacency_matrix,numVertices)) 
           {
               
        	   System.out.println("The given graph is bipartite and has partition:{}");
           } else
           {
               System.out.println("The given graph is not bipartite");
           }
       } catch (InputMismatchException inputMismatch) 
       {
           System.out.println("Input format is not good");
       }  
    }
}
