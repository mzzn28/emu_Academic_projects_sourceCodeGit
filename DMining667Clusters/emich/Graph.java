package dMinCluster.emich;

import java.io.*; 
import java.util.*; 


public class Graph 
{
	//data record class 
	private class Record {
		private double[] attributes; 	//attributes of record 
		private Record(double[] attributes) //constructor of record 
		{ 
		this.attributes = attributes; 	//assign attributes 
		}
	}
	
/******************************************************************/
	
	private int numberRecords;	//number of records
	private int numberAttributes; 	 //number of attributes
	private double delta; 			 //neighbor threshold 
	private ArrayList<Record> records; //list of records
	private int[][] matrix; 		  //adjacency matrix
	private int[] clusters; 		 //clusters of records 
	int Clusternum;


/*****************************************************************/
	
//constructor of clustering 
	public Graph() { 
		//parameters are zero 
		numberRecords = 0; 
		numberAttributes = 0;
		delta = 0; 
		Clusternum=0;
		//lists are empty 
		records = null; 
		matrix = null; 
		clusters = null; 
	}
/****************************************************************/ 
//method loads records from input file 
	public void loadData(String inputFile) throws IOException { 
		Scanner inFile = new Scanner(new File(inputFile)); 
		
		//read number of records, attributes 
		numberRecords = inFile.nextInt(); 
		numberAttributes = inFile.nextInt(); 
		
		//empty list of records 
		records = new ArrayList<Record>(); 
		
		//for each record 
		for (int i = 0; i < numberRecords; i++) { 
			//read attributes 
			double[] attributes = new double[numberAttributes]; 
			for (int j = 0; j < numberAttributes; j++) 
				attributes[j] = inFile.nextDouble(); 
			
			//create record 
			Record record = new Record(attributes); 
			
			//add record to list
			records.add(record); 
		}
		
		inFile.close(); 
	}
	
/***************************************************************/
	//method sets parameters of clustering 
	public void setParameters(double delta) 
	{ 
		//set neighbor threshold 
		this.delta = delta; 
	}	
	
/****************************************************************/ 
	//method performs clustering 
	public void cluster() 
	{
		//create adjacency matrix of records 
		createMatrix(); 
		
		//initialize clusters of records 
		initializeClusters();
		
		//initial record index is 0 
		int index = 0; 
				
		//initial cluster name is 0 int 
		int clusterName = 0; 
				
		//while there are more records 
		while (index < numberRecords) 
		{
			//if record does not have cluster name 
			if (clusters[index] == -1) 
			{ 
				//assign cluster name to record and all records connected to it
				assignCluster(index, clusterName); 
				
				//find next cluster name 
				clusterName = clusterName + 1; 
			}
			//go to next record 
			index = index + 1; 
		}
	}
	/********************************************************************/ 
	//method creates adjacency matrix 
	private void createMatrix() 
	{
		//allocate adjacency matrix 
		matrix = new int[numberRecords][numberRecords]; 
	
		//entry (i, j) is 0 or 1 depending on i and j are neighbors or not 
		for (int i = 0; i < numberRecords; i++) 
			for (int j = 0; j < numberRecords; j++) 
				matrix[i][j] = neighbor(records.get(i), records.get(j)); 
	}
	
/************************************************************************/
	
	//method decides whether two records are neighbors or not 
	//method is application specific 
	private int neighbor(Record u, Record v) 
	{ 
		double distance = 0; 
		
		//find euclidean distance between two records 
		for (int i = 0; i < u.attributes.length; i++) 
			distance += (u.attributes[i] - v.attributes[i])*
							(u.attributes[i] - v.attributes[i]); 
			distance = Math.sqrt(distance); 
	
			//if distance is less than neighbor threshold records are neighbors,
			//otherwise records are not neighbors
			if (distance <= delta) 
				return 1; 
			else 
				return 0; 
	}
	
/***********************************************************************/
	//method initializes clusters of records 
	private void initializeClusters() 
	{ 
		//create array of cluster labels 
		clusters = new int[numberRecords]; 
	
		//assign cluster -1 to all records 
		for (int i = 0; i < numberRecords; i++) 
			clusters[i] = -1; 
	}

/***********************************************************************/
	//method assigns cluster name to a record and all records 
	//connected to it, uses breadth first traversal 
	private void assignCluster(int index, int clusterName) 
	{
		//assign cluster name to record 
		clusters[index] = clusterName; 
		
		//list used in traversal 
		LinkedList<Integer> list = new LinkedList<Integer>(); 
		
		//put record into list 
		list.addLast(index); 
		//while list has records 
		while (!list.isEmpty()) 
		{ 
			//remove first record from list 
			int i = list.removeFirst(); 
			//find neighbors of record which have no cluster names 
			for (int j = 0; j < numberRecords; j++) 
				if (matrix[i][j] == 1 && clusters[j] == -1) 
				{ 
					//assign cluster name to neighbor 
					clusters[j] = clusterName; 
		
					//add neighbor to list
					list.addLast(j); 
				}
			}
		} 
		
/**********************************************************************/
	public void clusterNo(){
		//find max in clusters array
		for(int i=0; i<clusters.length;i++){	
			if(clusters[i]> Clusternum)
				Clusternum=clusters[i];
		}
		System.out.println(Clusternum+1);
	}
	
/**********************************************************************/ 
	//method writes records and their clusters to output file 
	public void displayResults(String outputFile) throws IOException 
	{ 
		PrintWriter outFile = new PrintWriter(new FileWriter(outputFile)); 
		
		//for each record 
		for(int k=0; k<clusters.length; k++)
			//check all record
			for (int i = 0; i < numberRecords; i++) 	
			{
				if (clusters[i]==k)
				{
					//write attribute of record
					for(int j=0; j<numberAttributes;j++)
						outFile.print(records.get(i).attributes[j] + " ");
			
					//write cluster
					outFile.println(clusters[i]+1);
					//System.out.println(clusters[i]+1);
				}
			}
		outFile.close(); 
	} 
/*********************************************************************/
	public void SumSquareError()
	{
		//sum squared Error
		double sse=0.0;
		
		// Get all centroids of clusters
		ArrayList<Record> clusterCentrs = new ArrayList<Record>(); 
		for(int k=0; k<Clusternum+1; k++)
			clusterCentrs.add(findAvgCent(k));
		
		//find the SSE	
		for (int i = 0; i < numberRecords; i++) 	
		{
			double dist=distance(records.get(i),clusterCentrs.get(clusters[i]));
			sse += (dist*dist);
		}
		System.out.println("SSE: "+sse);
		
		
	}	
/*********************************************************************/
	//find distance b.w two records
	private double distance(Record u, Record v) 
	{ 
		double sum = 0; 
			
		//find euclidean distance between two records 
		for (int i = 0; i < u.attributes.length; i++) 
			sum += (u.attributes[i] - v.attributes[i])* 
					(u.attributes[i] - v.attributes[i]); 
		return Math.sqrt(sum); 
	}
/*********************************************************************/
	//find centroid distance of one cluster 
	private Record findAvgCent(int clusterNumber)
	{
		//sum of of all record in cluster
		Record sumCluster;
		int tmp=0;
		
		//initialize cluster Sum
		double[] attributes = new double[numberAttributes]; 
		for (int j = 0; j < numberAttributes; j++) 
			attributes[j] =0; 
		sumCluster = new Record(attributes); 
	
	
		//check all record
		for (int i = 0; i < numberRecords; i++) 	
		{
			if (clusters[i]==clusterNumber) //if cluster match
			{
				sumCluster =sum(sumCluster,records.get(i));
					tmp=tmp+1;
			}
		}
	//return average sum/total record in cluster
		Record average = scale(sumCluster, 1.0/tmp); 
	return (average);
}
/*********************************************************************/
	//method finds sum of two records /used for centroid of cluster
	private Record sum(Record u, Record v) 
	{
		double[] result = new double[u.attributes.length]; 
			
		//find sum by adding corresponding attributes of records 
		for (int i = 0; i < u.attributes.length; i++) 
			result[i] = u.attributes[i] + v.attributes[i]; 
			
		return new Record(result); 
	}
/*********************************************************************/
	//method finds scaler multiple of a record 
	private Record scale(Record u, double k) 
	{
		double[] result = new double[u.attributes.length]; 
			
		//multiply attributes of record by scaler 
		for (int i = 0; i < u.attributes.length; i++) 
			result[i] = k*u.attributes[i]; 
			
		return new Record(result); 
	}
/*********************************************************************/			
}


		
		
		
		
		
