package packageone;

import java.util.Scanner;

public class LabFourDijkstraAlgorithm {
	
	//vertices with no connection will be given arbitrary 
	//max length for convenience.
	 static final int MAX = 1000;
	 static final int NOEDGE = 0;
	
	public static int[] finddijkstrapath(int[][] adj, int base) {
		
		int[] dist = new int[adj.length]; // array for shortest distances.

			for (int i=0; i<dist.length; i++){
				dist[i] = MAX; //filled with max value
			}
		
		dist[base] = 0; //base vertex weight to be zero.

		
		boolean[] check = new boolean[dist.length];//keep record for selected vertices
		for (int i=0; i<check.length; i++) 
			check[i] = false;
		
		// iterate through every vertex
		for (int i=0; i<dist.length-1; i++) {
			
			int vertex = 0;
			int sPath = MAX;
			// select the shortest path that is added yet.
			for (int j=0; j<dist.length; j++) {
				if (check[j] == false && dist[j] < sPath) {
					sPath = dist[j];
					vertex = j;
				}
			}
			// Choose this vertex to be added.
			check[vertex] = true;
			
			// Update all shortest path based on this particular vertex.
			for (int k = 0; k<dist.length; k++) {
				if (adj[vertex][k] == NOEDGE) 
					continue;
				
				if (dist[vertex]+adj[vertex][k] < dist[k])
					dist[k] = dist[vertex] + adj[vertex][k];
			}

		}
		return dist;
	}
	

	public static void main(String[] args) {
		// getting the adjacency matrix
		 int numVertices;
	     Scanner scanner = null;
	        
	           System.out.println("Enter the number of vertices in the graph");
	           scanner = new Scanner(System.in);
	           numVertices = scanner.nextInt();
	 
	           int adjacency_matrix[][] = new int[numVertices][numVertices];
	           System.out.println("Enter the adjacency matrix:");
	           //take the input matrix;
	           for ( int i = 0 ; i < numVertices ; i++ ){  
	  	         for (int j = 0 ; j < numVertices ; j++ ){
	  	        	 
	  	        	 System.out.print("Element [" + (i) +"][" +( j)+ "]:");
	  	        	adjacency_matrix[i][j] = scanner.nextInt();
	  	       	
	  	         }
	           }
	        
	       int[] sPath = finddijkstrapath(adjacency_matrix, 0);
			System.out.println("Shortest path length b/w V1 to V"+numVertices+" is:" + sPath[numVertices-1]);//ref is zero instead of vertex 1.
			
	}
}
