package dMin.emich;

import java.io.*;
import java.util.*;

public class NearestNeighbor {
	
	private class Record{
		
		private double [] attributes;
		private int className;
		
		private Record(double [] attributes, int className ){
		 this.attributes=attributes; 
		 this.className=className;	
		}
	}
	
	private ArrayList<Record>records;
	private String [] attributeTypes;
	
	private int numberRecords;
	private int numberAttributes;
	private int numberClasses;
	
	private int numberNeighbors;
	private String distanceMeasure;
	private String majorityRule;
	
	/*******************************/
	 public NearestNeighbor() { 
		 
		 records = null; 
		 attributeTypes = null; 
		 numberRecords = 0;
		 numberAttributes = 0;
		 numberClasses = 0;
		 numberNeighbors = 0; 
		 distanceMeasure = null; 
		 majorityRule = null; 
	 }
	 /*************************************************************************/
	 //method loads training records from training file 
	 public void loadTrainingData(String trainingFile) throws IOException { 
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
			String label = inFile.next();
			attributeArray[j] = convert(label, j+1); 
		}
		
		//read class and convert it to numerical form 
		String label = inFile.next(); 
		int className = (int)convert(label, numberAttributes+1); 
	
		//create record
			Record record = new Record(attributeArray, className); 
		//add record to list of records 
			records.add(record); 
	}
		inFile.close();
}
	/*************************************************************************/ 
	//method reads test records from test file and writes classes
	//to classified file 
	 public void classifyData(String testFile, String classifiedFile) throws IOException { 
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
				else { 
		
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
	 
	 //method finds distance between two records 
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
			
			//if matching is used 
			else if (distanceMeasure.equals("matching")) 
			{ 
				int matches = 0; 
				for (int i = 0; i < u.length; i++) 
					if ((int)u[i] == (int)u[i]) 
						matches = matches + 1; 
					distance = 1 - (double)matches/u.length; 
			} 
			
			//if heterogeneous measure is used 
			else if (distanceMeasure.equals("heterogeneous")) 
			{ 
				double sum = 0;
				double dist = 0; 
					for (int i = 0; i < u.length; i++) 
					{ 
			 
						if (attributeTypes[i].equals("binary") ||
						attributeTypes[i].equals("nominal")) 
							if ((int)u[i] == (int)v[i]) 
								dist = 0; 
							else							 //find distance between 
								dist = 1; 					//corresponding attributes 
						if (attributeTypes[i].equals("ordinal") ||
						attributeTypes[i].equals("continuous")) 
							dist = u[i] - v[i]; 
						
						sum = sum + dist*dist; 				//sum of square of distances 
					}
					
						distance = Math.sqrt(sum); 
			}
			return distance; 
		}
	
	 /*************************************************************************/
	
	 //method validates classifier using validation file and displays 
	 //error rate 
	 public void validate(String validationFile) throws IOException { 
			
		 Scanner inFile = new Scanner(new File(validationFile)); 
		
		 //read number of records 
		 int numberRecords = inFile.nextInt(); 	
		 int numberErrors = 0; 					 //initially zero errors
		
		 //for each record
		 for (int i = 0; i < numberRecords; i++) 
		 { 
			 double[] attributeArray = new double[numberAttributes]; 		  
	
			//read attributes
			 for (int j = 0; j < numberAttributes; j++) 
			 { 
				 String label = inFile.next();
				 attributeArray[j] = convert(label, j+1); 
			 } 
			 
			 //find class predicted by classifier 
			 int predictedClass = classify(attributeArray); 
			 
			 //read actual class from file 
			 String label = inFile.next();
			 int actualClass = (int)convert(label, numberAttributes+1); 
			 
			 //errror if predicted and actual classes do not match 
			 if (predictedClass != actualClass)
				 numberErrors += 1; 
		 }
			
		 //find and print error rate 
		 double errorRate = 100.0 * numberErrors/numberRecords;
		 System.out.println(errorRate + " percent error"); 
			
		 inFile.close(); 
		
	 }
		
	 /************************************************************************/ 
			 
	 //method converts attribute values to numerical values, hard coded for 
	 //specific application 
	 private double convert(String label, int column) 
	{ 
	
		 double value=0; 
			 
		 //convert first column sex attribute to 0/1 
		 if (column == 1) 
		 { 
			 if (label.equals("male"))
				 value = 0; 
			 else 
				 value = 1; 
		} 
			 
		 //convert second column marital attribute to 1/2/3 
		 else if (column == 2) 
		 { 
			 if (label.equals("single")) 
				 value = 1;
			 else if (label.equals("married"))
				 value = 2;
			 else
				 value = 3; 
		} 
			
		 //convert third column grade attribute to [0, 1] range 
		 else if (column == 3)
		 { 
			 if (label.equals("A")) 
				 value = 1.0; 
			 else if (label.equals("B")) 
				 value=0.75;
			 else if (label.equals("C")) 
				 value=0.50;
			 else
				 value=0.25;
		 }
		 
		 //convert fourth column income to attribute [0,1] range.
		 else if(column==4){
			 value =Double.valueOf(label);
			 value=value/100;
		 }
		 //convert fifth column income to attribute [0,1] range.
		 else if (column==5){
			 value =Double.valueOf(label);
			 value=value/4;
		 }
		 
		 //convert sixth column income to attribute 1/2/3 .
		 else
		 {
			 if(label.equals("highrisk"))
				 value= 1;
			 else if (label.equals("mediumrisk"))
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
			 label="highrisk";
		 else if(value==2)
			 label="mediumrisk";
		 else
			 label="lowrisk";
		 
		 return label;
	 }
}
