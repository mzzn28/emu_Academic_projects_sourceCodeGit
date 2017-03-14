package dMinCluster.emich;

import java.io.*;
import java.util.*; 


public class Ass3PThree {
	//data record class 
		private class Record 
		{ 
			private int[] attributes; 	//attributes of record 	
			
			private Record(int[] attributes) //constructor of record 
			{ 
				this.attributes = attributes; 	//assign attributes 
			}
		}  	

	/************************************************************************/ 

		private int numberRecords; 			//number of records
		private int numberAttributes; 		 //number of attributes
		private int numberClusters; 		 //number of cluster
		private ArrayList<Record> records;		//list of records 
		private ArrayList<Record> centroids; 	 //list of centroids	
		private int[] clusters; 				//cluster labels of records 
		private Random rand;  					//random number generator 
		private int fileLength;
	/***********************************************************************/
		
		//constructor of clustering 
		public Ass3PThree(int fileLength) 
		{ 
			this.fileLength=fileLength;
			//parameters are zero 
			numberRecords = 0; 
			numberAttributes = 2; //as we are taking image input in pairs
			numberClusters = 0; 
			//lists are empty
			records = null; 
			centroids = null; 
			clusters = null; 
			rand = null; 

		}
		
/***********************************************************************/
				
	//method loads records from input file 
	public void loadData(String inputFile) throws IOException 
	{ 
		Scanner inFile = new Scanner(new File(inputFile)); 
				
		//empty list of records 
		records = new ArrayList<Record>(); 
		
		//read image file untill it ends 
		while(inFile.hasNext()){
				
			//read attributes 
			int[] attributes = new int[numberAttributes]; 
			for (int j = 0; j < numberAttributes; j++) 
				attributes[j] = inFile.nextInt(); 
				
				//create record 
				Record record = new Record(attributes); 
				numberRecords=numberRecords+1;
				//add record to list 
				records.add(record); 
			} 
			inFile.close(); 
			System.out.println("success, record:" + numberRecords);
		} 
			
/***********************************************************************/
	
	//method sets parameters of clustering 
	public void setParameters(int numberClusters, int seed) 
	{ 
		//set number of clusters 
		this.numberClusters = numberClusters; 
		
		//create random number generator with seed 
		this.rand = new Random(seed); 
	}

/**********************************************************************/
	
	//method performs k-means clustering 
	public void cluster() 
	{	
		//initialize clusters of records 
		initializeClusters(); 
		
		//initialize centroids of clusters 
		initializeCentroids(); 
		
		//stop condition has not been reached 
		boolean stopCondition = false; 
		
		//while stop condition is not reached 
		while (!stopCondition) 
		{ 
			//assign clusters to records 
			int clusterChanges = assignClusters(); 
		
			//update centroids of clusters 
			updateCentroids(); 
		
			//stop condition is reached if no records changed clusters 
			stopCondition = clusterChanges == 0;
		}
	}

/********************************************************************/
	//method initializes clusters of records 
	private void initializeClusters() 
	{ 
		//create array of cluster labels 
		clusters = new int[numberRecords]; 
	
		//assign cluster -1 to all records 
		for (int i = 0; i < numberRecords; i++) 
			clusters[i] = -1;
	}
	
/**********************************************************************/
	
	//method initializes centroids of clusters 
	private void initializeCentroids() 
	{
		centroids = new ArrayList<Record>(); 
		
		//for each cluster 
		for (int i = 0; i < numberClusters; i++) 
		{ 
			//randomly pick a record 
			int index = rand.nextInt(numberRecords); 
			//use record as centroid 
			centroids.add(records.get(index)); 
		}
	}

/**********************************************************************/
	
	//method assigns clusters to records 
	private int assignClusters()
	{ 
		int clusterChanges = 0; 
	
		//go thru records and assign clusters to them 
		for (int i = 0; i < numberRecords; i++) 
		{ 
			Record record = records.get(i); 
			
			//find distance between record and first centroid 
			double minDistance = distance(record, centroids.get(0));
			int minIndex = 0; 
			
			//go thru centroids and find closest centroid 
			for (int j = 0; j < numberClusters; j++) 
			{ 
				//find distance between record and centroid 
				double distance = distance(record, centroids.get(j)); 
	
				//if distance is less than minimum, update minimum 
				if (distance < minDistance) { 
	
					minDistance = distance; 
					minIndex = j; 
				}
			}
			//if closest cluster is different from current cluster 
			if (clusters[i] != minIndex) 
			{ 
				//change cluster of record 
				clusters[i] = minIndex; 
				//keep count of cluster changes 
				clusterChanges++; 
			} 
		}
		//return number of cluster changes 
		return clusterChanges; 
	}

/*************************************************************************/ 
		
	//method updates centroids of clusters
	private void updateCentroids() 
	{ 
		//list of cluster sums 
		ArrayList<Record> clusterSum = new ArrayList<Record>(); 
		
		//for each cluster 
		for (int i = 0; i < numberClusters; i++)
		{ 
			//create vector [0 0 . 0] 
			int[] attributes = new int[numberAttributes]; 
			for (int j = 0; j < numberAttributes; j++) 
				attributes[j] = 0; 
			
			//initialize sum to [0 0 . . 0] 
			clusterSum.add(new Record(attributes)); 
		}
		
		//array of cluster sizes 
		int[] clusterSize = new int[numberClusters]; 
			
		//initialize cluster sizes to 0 
		for (int i = 0; i < numberClusters; i++) 
			clusterSize[i] = 0; 
			
		//for each record 
		for (int i = 0; i < numberRecords; i++) 
		{ 
			//find cluster of record 
			int cluster = clusters[i]; 
			
			//add record to cluster sum 
			Record sum = sum(clusterSum.get(cluster), records.get(i)); 
			clusterSum.set(cluster, sum); 
			
			//increment cluster size 
			clusterSize[cluster] += 1; 
		}
			
		//for each cluster 
		for (int i = 0; i < numberClusters; i++) 
		{ 
			//find average by dividing cluster sum by cluster size 
			Record average = scale(clusterSum.get(i), 1.0/clusterSize[i]); 
		
			//set centroid to average
			centroids.set(i,average);
		}
	}
/******************************************************************************/
	//method finds distance between two records 
	//distance is application specific 
	private double distance(Record u, Record v) 
	{ 
		double sum = 0; 
		
		//find euclidean distance between two records 
		for (int i = 0; i < u.attributes.length; i++) 
			sum += (u.attributes[i] - v.attributes[i])* 
					(u.attributes[i] - v.attributes[i]); 
		return Math.sqrt(sum); 
	}
	
/*************************************************************************/ 

	//method finds sum of two records 
	private Record sum(Record u, Record v) 
	{
		int[] result = new int[u.attributes.length]; 
		
		//find sum by adding corresponding attributes of records 
		for (int i = 0; i < u.attributes.length; i++) 
			result[i] = u.attributes[i] + v.attributes[i]; 
		
		return new Record(result); 
	}
		
/***********************************************************************/ 
	
	//method finds scaler multiple of a record 
	private Record scale(Record u, double k) 
	{
		int[] result = new int[u.attributes.length]; 
		double tmpr=0;
		//multiply attributes of record by scaler 
		for (int i = 0; i < u.attributes.length; i++) {
			tmpr = k*u.attributes[i]; 
			result[i]=(int)tmpr;		//take int value
		}
		return new Record(result); 
	}
		
/*************************************************************************/ 
	
	//method writes  
	public void displayResults(String outputFileC, String outputFileD) throws IOException 
	{
			//write compressed file with cluster lablel
			PrintWriter outFile = new PrintWriter(new FileWriter(outputFileC)); 
			
			//write cluster centroids
			for (int i = 0; i < centroids.size(); i++) 	
			{
				for (int j = 0; j < numberAttributes; j++) 	
				outFile.print( centroids.get(i).attributes[j]+ " ");
				outFile.println();	
			}
			
			//write cluster label
			for (int i = 0; i < numberRecords; i++) 	
			{
				outFile.print( clusters[i]+ " ");
				//change line if as per file size
				if ((i+1) % (fileLength/2) ==0)
				outFile.println();
			}
			outFile.close();

			//wite decompressed image file
			PrintWriter outFileD = new PrintWriter(new FileWriter(outputFileD)); 
			for (int i = 0; i < numberRecords; i++) 	
			{
				for (int j = 0; j < numberAttributes; j++)
				outFileD.print( centroids.get(clusters[i]).attributes[j]+ " ");
				//change line if as per file size
				if ((i+1) % (fileLength/2) ==0)
					outFileD.println();
							
			}
			
			outFileD.close();
		}
/*************************************************************************/
	
	public void findCompRatio(){
	
		//original image size assuming pixel value 0-255 
		double imagebits=fileLength*fileLength *8;
		double compbits=(fileLength* (fileLength/2))+ (numberClusters *8*2 );
		double ComRatio=compbits/imagebits;
		System.out.println("Compression Ratio: "+ ComRatio );
		
	}
}
