package dMin.emich;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class AssignNearestNeighbour23 {

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
	
	
	 public AssignNearestNeighbour23() { 
		 
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
			 double[] distance = new double[records.size()]; 
			 int[] id = new int[records.size()]; 
			 
			 //find distances between attributes and all records 
			 for (int i = 0; i < records.size(); i++)
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
//method finds distance between two records specific to problem.
	private double distance(double[] u, double[] v) 
	{
			double distance = 0; 
			if (distanceMeasure.equals("heterogeneous")) 
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
	
/********************************************************************/
	
	//method finds the majority class of nearest neighbors 
	 private int majority(int[] id, double[] attributes) 
	 { 
		double[] frequency = new double[numberClasses]; 
		
		//class frequencies are zero initially 
			for (int i = 0; i < numberClasses; i++)
				frequency[i] = 0; 
					
			//if unweighted majority rule is used 
			if (majorityRule.equalsIgnoreCase("unweighted")) 
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
						  int idindex=id[i];
						  int index=records.get(idindex).className - 1;
						  frequency[index] += 1/(d + 0.001); 
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
			  for (int j = i; j < records.size(); j++)
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
			 
		 //convert first column score to 0-1 range/normalize(500-900) 
		 if (column == 1) 
		 { 
			 value =Double.parseDouble(label);
			 value=(value-500.0)/400;		//x-min/max-min
		} 
			 
		 //convert second column income to 0-1 range/normalize(30k-90k) 
		 else if (column == 2) 
		 { 
			 value =Double.parseDouble(label);
			 value=(value-30.0)/60;
			 
		} 
			
		 //convert third column age attribute to [0, 1] range /30-80
		 else if (column == 3)
		 { 
			 value =Double.parseDouble(label);
			 value=(value-30.0)/50;
		 }
		 //convert fourth column gender attribute to [2, 1] 
		 else if (column == 4)
		 { 
			 if (label.equals("male")) 
				 value = 0; 
			 else 
				 value=1;
		 }
		//convert fourth column marital status attribute to [0, 1,2]
		 else if (column == 5)
		 { 
			 if (label.equals("single")) 
				 value = 1; 
			 else if (label.equals("married")) 
				 value=2;
			 else
				 value=3;
		 }
		//convert fourth column class to number 1/2/3 .
		 else
		 {
			 if(label.equals("high"))
				 value= 1;
			 else if (label.equals("medium"))
				 value= 2;
			 else if (label.equals("low"))
				 value=3;
			 else
				 value = 4;
		 }
		 return value;
	}
	 //method converts integer values to class labels
	 private String convert(int value){
		 String label = null;
		 
		 if(value==1)
			 label="high";
		 else if(value==2)
			 label="medium";
		 else if(value==3)
			 label="low";
		 else
			 label="undetermined";
		 
		 return label;
		
	 }
	 	
/**************************************************/
	//method to compute training error
	public void tariningError(){
			
			double[] attributeArray = new double[numberAttributes];	//to save the one set of attributes 
			int incorrectClassified=0;							//count correct class
			int className,tmpClassName;
			//read attributes and convert to binary 
			for(int i=0;i<records.size();i++)
			{
				for (int j = 0; j < numberAttributes; j++) 
				{ 
					attributeArray[j] = records.get(i).attributes[j];
				} 
				
				//find class of attributes from training record.
				className=records.get(i).className;
				//classify the record
				tmpClassName = classify(attributeArray); 
				
				//compare the classes
				if(className!=tmpClassName)
					incorrectClassified++;
					
			}
			double trainingError=incorrectClassified/numberRecords;
			
			System.out.println("Training Error: "+ trainingError);
			
			
		}
		
	/*************************************************************************/ 

	//method to compute validation error leave one out
	public void validationErrorOneOut(){
				
				int InCorrectvalidated=0;			//count incorrect class validation
				int className,tmpClassName;			//class from record and to classify	
				double [] validationError= new double[records.size()];
				Record validationRecord;
				ArrayList <Record> tmptrainRecords = new ArrayList<Record>();
				
				//save the copy of training record
				for(int i=0;i<records.size();i++)
				{
					tmptrainRecords.add(records.get(i));
					
				}
					records.clear();	//reset the record for new training data;
				//leave one out for validation records
				for(int i=0;i<tmptrainRecords.size();i++)
				{
					validationRecord=tmptrainRecords.get(i);	//selected validation records
					
						//get the remaining  record for training
						for(int j=0;j<tmptrainRecords.size();j++){	
							if(i==j)						//record is taken as validation
								j++;						//so go to next one
							if(j==tmptrainRecords.size())		//if last record is taken 
									break;					//then come out of loop
							records.add(tmptrainRecords.get(j));
						}
						
					//classify the validation record
					tmpClassName = classify(validationRecord.attributes);
					className=validationRecord.className;
					
					//compare the classes
					if(className!=tmpClassName)
						InCorrectvalidated++;
					
					validationError[i]=InCorrectvalidated/1;	//incorrect result / one validation record
					
					//reset the value as its recorded in array. 
					if(className!=tmpClassName)
						InCorrectvalidated--;
				}
				
				//retreive the errors to compute avg
				double validationErrorAvg=0;
				
					for(int k=0;k<validationError.length;k++)	//find sum of validation
					{
							
						validationErrorAvg+= validationError[k];	
					}
						//compute result
						validationErrorAvg=validationErrorAvg/validationError.length;	
						double roundOffError = Math.round(validationErrorAvg * 100.0) / 100.0;
						
					System.out.println( "Leave one out validation Error: "+ roundOffError );
			}
	 
	 
}
