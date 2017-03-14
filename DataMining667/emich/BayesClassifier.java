package dMin.emich;

import java.io.*; 
import java.util.*;

//Bayes classifier class 
public class BayesClassifier 
{ 
	//training record class 
	private class Record { 
		private int[] attributes; 	//attributes of record
		private int className;		 //class of record 
		
		//constructor of record 
		private Record(int[] attributes, int className)
		{
				this.attributes = attributes;	//assign attributes 
				this.className = className; 	//assign class 
		}
	}
	
/*************************************************************************/ 
	
	private ArrayList<Record> records; 		//list of training records 
	private int[] attributeValues;			//attribute values  
	private int numberRecords; 				//number of training records 			
	private int numberAttributes; 			//number of attributes	
	private int numberClasses; 				 //number of classes 
	
	double[] classTable; 				//class frequencies
	double[][][] table; 				 //conditional probabilities

	//constructor of classifier 
	public BayesClassifier() 
	{
		records = null; 			//initialize records, attribute 
		attributeValues = null;		//values to empty  
		numberRecords = 0;			//number of records, attributes 
		numberAttributes = 0; 		//classes are zero 
		numberClasses = 0; 			//initialize class frequencies
		classTable = null; 			 //and conditional probabilities 
		table= null; 				//to empty 
	}

/******************************************* **************************/ 
	
	//method loads training records from training file
	public void loadTrainingData(String trainingFile) throws IOException 
	{
		Scanner inFile = new Scanner(new File(trainingFile)); 
		
		//read number of records, attributes, classes 
		numberRecords = inFile.nextInt(); 
		numberAttributes = inFile.nextInt(); 
		numberClasses = inFile.nextInt(); 
		
		//read attribute values 
		attributeValues = new int[numberAttributes]; 
		for (int i = 0; i < numberAttributes; i++) 
			attributeValues[i] = inFile.nextInt(); 
		
		//empty list of records 
		records = new ArrayList<Record>(); 
		
		//for each record
		for (int i = 0; i < numberRecords; i++) 
		{
			//create attribute array 
			int[] attributeArray = new int[numberAttributes]; 
		
			//read attributes and convert them to numerical form 
			for (int j = 0; j < numberAttributes; j++)
			{	
				String label = inFile.next(); 
				attributeArray[j] = convert(label, j+1); 
			}
			//read class and convert it to numerical form 
			String label = inFile.next();
			int className = convert(label, numberAttributes+1); 
			
			//create record 
			Record record = new Record(attributeArray, className); 
			//add record to list of records 
			records.add(record); 
		}
		inFile.close();
	}
		
/*************************************************************************/ 
		
	//method build bayes's model
	public void buildModel() 
	{ 
		//compute class frequencies 
		fillClassTable(); 
		//compute conditional probabilities 
		fillProbabilityTable(); 
	}
		
/**********************************************************************/ 
	
	//method computes class frequencies 
	private void fillClassTable() 
	{ 
		classTable = new double[numberClasses]; 
		
		//initialize frequencies 
		for (int i = 0; i < numberClasses; i++) 
			classTable[i] = 0;
		
		//compute frequencies 
		for (int i = 0; i < numberRecords; i++) 
			classTable[records.get(i).className-1] += 1; 
		
		//normalize frequencies 
		for (int i = 0; i < numberClasses; i++) 
			classTable[i] /= numberRecords; 
	} 

/*************************************************************************/ 
		
	//method computes conditional probabilities 
	private void fillProbabilityTable()
	{
		//array to store probabilites 
		table = new double[numberAttributes][][]; 
		
		//compute probabilities for each attribute 
		for (int i = 0; i < numberAttributes; i++)
			fill(i+1);
	}
		
/*************************************************************************/ 

	//method computes conditional probabilities for an attribute 
	private void fill(int attribute) 
	{
		//find number of attribute values 
		int attributeValues = this.attributeValues[attribute-1]; 
		
		//create array to hold probabilities 
		table[attribute-1] = new double[numberClasses][attributeValues]; 
		
		//initialize probabilities 
		for (int i = 0; i < numberClasses; i++) 
			for (int j = 0; j < attributeValues; j++) 
				table[attribute-1][i][j] = 0; 
		
		//compute class-attribute frequencies 
		for (int k = 0; k < numberRecords; k++)
		{
			int i = records.get(k).className - 1;
			int j = records.get(k).attributes[attribute-1] - 1;
			table[attribute-1][i][j] += 1; 
		}
		
		//compute probabilities, use Laplace correction 
		for (int i = 0; i < numberClasses; i++) 
			for (int j = 0; j < attributeValues; j++) 
			{
				double value = (table[attribute-1][i][j] + 1)/ 
								(classTable[i]*numberRecords + attributeValues); 
				table[attribute-1][i][j] = value; 
			}
	}
		
/*************************************************************************/ 
		
	//method classifies an attribute 
	private int classify(int [] attributes)
	{ 
		double maxProbability = 0; 
		int maxClass = -1; 
			
		//for each class 
		for (int i = 0; i < numberClasses; i++) 
		{ 
			//find conditional probability of class given the attribute 
			double probability = findProbability(i+1, attributes); 
			
			//choose the class with the maximum probability 
			if (probability > maxProbability) 
			{ 
				maxProbability = probability; 
				maxClass = i; 
			}
		}
		//return maximum class 
		return maxClass + 1; 
	}
			
/*************************************************************************/ 
			
	//method computes conditional probability of a class for a given attribute 
	private double findProbability(int className, int[] attributes)
	{
		double value;
		double product = 1; 
		
		//find product of conditional probabilities stored in table model 
		for (int i = 0; i < numberAttributes; i++) 
		{ 
			value = table[i][className-1][attributes[i]-1]; 
			product = product*value; 
		}
			
		//multiply product and class probability 
		return product*classTable[className-1]; 
	}
			
/*************************************************************************/ 
			
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
			int[] attributeArray = new int[numberAttributes]; 
			
			//read attributes and convert them to numerical form 
			for (int j = 0; j < numberAttributes; j++)
			{
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
		
/*************************************************************************/ 
			
	//method validates classifier using validation file and displays 
	//error rate 
	public void validate(String validationFile) throws IOException 
	{
		Scanner inFile = new Scanner(new File(validationFile)); 
		
		//read number of records
		int numberRecords = inFile.nextInt(); 
			
		//initially zero errors
		int numberErrors = 0; 
			
		//for each record
		for (int i = 0; i < numberRecords; i++) 
		{ 
			int[] attributeArray = new int[numberAttributes]; 
			
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
			int actualClass = convert(label, numberAttributes+1); 
			
			//errror if predicted and actual classes do not match 
			if (predictedClass != actualClass) 
				numberErrors += 1; 
		}
			
		//find and print error rate 
		double errorRate = 100.0*numberErrors/numberRecords; 
		System.out.println(errorRate + " percent error"); 
		
		inFile.close();		
	}

/**********************************************************************************/
	
	//method converts attribute values and class labels 
	//hard coded for specific application 
	private int convert(String label, int column)
	{  
		int value; 
		
		if (column== 1)
			if (label.equals("college")) value= 1; 
			else value = 2; 
		else if (column ==2)
			if (label.equals("smoker"))	value =1;
			else value = 2; 
		else if (column == 3) 
			if (label.equals("married")) value = 1; 
			else value = 2; 
		else if (column == 4)
			if (label.equals("male")) value = 1;
			else value = 2; 
		else if (column == 5) 
			if (label.equals("works")) value = 1; 
			else value = 2; 
		else
			if (label.equals("highrisk")) value=1;
			else if (label.equals("mediumrisk")) value =2;
			else if (label.equals("lowrisk")) value = 3;
			else value=4;
		
		return value; 
	}
	
/*************************************************************************/ 
	
	//method converts integer values to class labels, hard coded for 
	//specific application 
	private String convert(int value) 
	{ 
		String label; 
		
		if (value == 1) label ="highrisk";
		else if (value== 2) label= "mediumrisk";  
		else if (value== 3) label ="lowrisk";  
		else label ="undetermined";
		
		return label; 
	}	
}
