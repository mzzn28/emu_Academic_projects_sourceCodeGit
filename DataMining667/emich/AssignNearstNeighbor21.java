
package dMin.emich;
import java.io.*;
import java.util.*;

public class AssignNearstNeighbor21 {

	private class Record{
		
		private double [] attributes;
		private int className;
		
		public Record(double [] attributes, int className ){
		 this.attributes=attributes; 
		 this.className=className;	
		}
	}
/***************************************************/
	//Initialize basic variable
	private ArrayList<Record>records;
	private String [] attributeTypes;
	
	private int numberRecords;
	private int numberAttributes;
	private int numberClasses;
	
	private int numberNeighbors;	//number of neighbor
	private String distanceMeasure;
	private String majorityRule;
	
	
	 public AssignNearstNeighbor21() { 
		 
		 records = null; 
		 attributeTypes = null; 
		 numberRecords = 0;
		 numberAttributes = 0;
		 numberClasses = 0;
		 numberNeighbors = 0; 
		 distanceMeasure = null; 
		 majorityRule = null; 
	 }
	
/*****************************************************/
	//method loads training records from training file 
	 public void loadTrainingData(String trainingFile) throws IOException
	 { 
		 Scanner inFile = new Scanner(new File(trainingFile)); 
		 
		//read number of records, attributes, classes 
		 numberRecords = inFile.nextInt(); 
		 numberAttributes = inFile.nextInt(); 
		 numberClasses = inFile.nextInt(); 
		 
		 //read neighbors, distance measure, majority rule
		 numberNeighbors = inFile.nextInt(); 
		 distanceMeasure = inFile.next(); 
		 majorityRule = inFile.next();
		 
		//read attribute types
		 attributeTypes = new String[numberAttributes];
		 	for (int i = 0; i < numberAttributes; i++)
		 		attributeTypes[i] = inFile.next(); 
		 	
		//empty list of records 
		 	records = new ArrayList<Record>(); 
		 	
		//for each record 
		for (int i = 0; i < numberRecords; i++) 
		{
			//create attribute array 
			double[] attributeArray = new double[numberAttributes]; 
			
			//read attributes and convert them to numerical form 
			for (int j = 0; j < numberAttributes; j++) 
			{	
				if(inFile.hasNext()){
					 String label = inFile.next();
					attributeArray[j] = convert(label, j+1); 
				}
			}
			int className=0;
			//read class and convert it to numerical form 
			if(inFile.hasNext()){
				String label = inFile.next(); 
				className = (int)convert(label, numberAttributes+1); 
			}
			//create record
				Record record = new Record(attributeArray, className); 
			//add record to list of records 
				records.add(record); 
		}
			inFile.close();
	}
	
/************************************************************************/ 
	//method reads test records from test file and writes classes
	//to classified file 
	public void classifyData(String testFile, String classifiedFile) throws IOException 
	{ 
		Scanner inFile = new Scanner(new File(testFile));  
		PrintWriter outFile = new PrintWriter(new FileWriter(classifiedFile)); 
			 
		//read number of records
		int numberRecords = inFile.nextInt(); 
			 
			 //for each record 
			 for (int i = 0; i < numberRecords; i++) 
			 { 
				 //create attribute array
				 double[] attributeArray = new double[numberAttributes]; 
				 
				 //read attributes and convert them to numerical form 
				 for (int j = 0; j < numberAttributes; j++) {  
					 String label = inFile.next(); 
					 attributeArray[j] = convert(label, j+1); 
				 }
				 //find class of attribute
				 int className = classify(attributeArray); 
				 
				 //find class label and write to output file
				 String label = convert(className); 
				 outFile.println(label); 
			 }
			inFile.close(); 
			outFile.close(); 
		} 
		 /************************************************************************/ 
		
		 //method finds class of given attributes 
		 private int classify(double[] attributes) 
		{ 
			 double[] distance = new double[numberRecords]; 
			 int[] id = new int[numberRecords]; 
			 
			 //find distances between attributes and all records 
			 for (int i = 0; i < numberRecords; i++)
			{
				 distance[i] = distance(attributes, records.get(i).attributes);
				 id[i] = i; 
			}
			 
			 //find the nearest neighbors 
			 nearestNeighbor(distance, id); 
			 
			 //find majority class of neighbors 
			 int className = majority(id, attributes); 
			 //return class
			 return className; 
		}
/*************************************************************************/
//method finds distance between two records specific to problem only for euclidistance
	private double distance(double[] u, double[] v) 
	{
			double distance = 0; 
				
			//if euclidean maeasure is used 
			if (distanceMeasure.equals("euclidean")) { 
					double sum = 0; 
					for (int i = 0; i < u.length; i++) //find euclidean distanc
						sum = sum + (u[i] -v[i])*(u[i] - v[i]);
						 distance=Math.sqrt(sum);
				}
				
				return distance; 
	}	
	
/********************************************************************/
	
	//method finds the majority class of nearest neighbors 
	 private int majority(int[] id, double[] attributes) 
	 { 
		double[] frequency = new double[numberClasses]; 
		
		//class frequencies are zero initially 
			for (int i = 0; i < numberClasses; i++)
				frequency[i] = 0; 
					
			//if unweighted majority rule is used 
			if (majorityRule.equals("unweighted")) 
				{ 
					//each neighbor contributes 1 to its class 
					for (int i = 0; i < numberNeighbors; i++)
						frequency[records.get(id[i]).className - 1] +=1 ;
				}
				//if weighted majority rule is used 
				else 
				{ 		
					//each neighbor contributes 1/distance to its class
					for (int i = 0; i < numberNeighbors; i++) 
					{
						  double d = distance(records.get(id[i]).attributes, attributes);
						  frequency[records.get(id[i]).className - 1] += 1/(d + 0.001); 
					}
				}
					  
			//find majority class 
			int maxIndex = 0;
				for (int i = 0; i < numberClasses; i++) 
					if (frequency[i] > frequency[maxIndex]) 
						maxIndex = i; 
				
					  return maxIndex + 1; 
	 } 
					 
/***********************************************************************/	

	 //method finds the nearest neighbors 
	 private void nearestNeighbor(double[] distance, int[] id) 
	 {
	
		 //sort the records by their distances and choose the 
		 //closest neighbors 
		  for (int i = 0; i < numberNeighbors; i++) 
			  for (int j = i; j < numberRecords; j++)
				  if (distance[i] > distance[j]) 
				  {
					  double tempDistance = distance[i]; 
					  distance[i] = distance[j];
					  distance[j] = tempDistance; 
					  
					  int tempId = id[i];
					  id[i]= id[j];
					 id[j] = tempId; 
				  }
		  }
		
/*************************************************************************/		 
	 //method converts attribute values to normalized numerical values, hard coded for 
	 //specific problem
	 private double convert(String label, int column) 
	{ 
	
		 double value=0; 
			 
		 //convert first column score to 0-1 range 
		 if (column == 1) 
		 { 
			 value =Double.parseDouble(label);
			 value=value/100;
		} 
			 
		 //convert second column gpa to 0-1 range
		 else if (column == 2) 
		 { 
			 value =Double.parseDouble(label);
			 value=value/4;
			 
		} 
			
		 //convert third column grade attribute to [0, 1] range 
		 else if (column == 3)
		 { 
			 if (label.equals("A")) 
				 value = 1.0; 
			 else if (label.equals("B")) 
				 value=0.75;
			 else
				 value=0.50;
		 }
		//convert fourth column class to number 1/2/3 .
		 else
		 {
			 if(label.equals("good"))
				 value= 1;
			 else if (label.equals("average"))
				 value= 2;
			 else
				 value=3;
		 }
		 return value;
	}
	 //method converts integer values to class labels
	 private String convert(int value){
		 String label = null;
		 
		 if(value==1)
			 label="good";
		 else if(value==2)
			 label="average";
		 else
			 label="bad";
		 
		 return label;
	 }
	 	 
}
