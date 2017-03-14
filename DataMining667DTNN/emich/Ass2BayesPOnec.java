package dMin.emich;

import java.io.*; 
import java.util.*;

public class Ass2BayesPOnec 
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
	private ArrayList<Record> copyRecords;
	private int[] attributeValues;			//attribute values  
	private int numberRecords; 				//number of training records 			
	private int numberAttributes; 			//number of attributes	
	private int numberClasses; 				 //number of classes 
	private double levelCon;				//level of confidence
		
	double[] classTable; 				//class frequencies
	double[][][] table; 				 //conditional probabilities

	//constructor of classifier 
	public Ass2BayesPOnec() 
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
		
		//normalize frequencies(probability of class) 
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
		double sum=0;
		
		//for each class 
		for (int i = 0; i < numberClasses; i++) 
		{ 
			//find conditional probability of class given the attribute 
			double probability = findProbability(i+1, attributes); 
				sum +=probability;
			//choose the class with the maximum probability 
			if (probability > maxProbability) 
			{ 
				maxProbability = probability; 
				maxClass = i; 
			}
		}
		levelCon=maxProbability/sum;
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
			String label = Convert(className); 
			System.out.println(label);
			outFile.println(label); 
		}
		inFile.close(); 
		outFile.close();
	}
			

/**********************************************************************************/
		
	//method converts attribute values and class labels to numbers
	//hard coded for specific application 
	private int convert(String label, int column)
	{  
		int value; 
		 
		//convert attribute labels to number values 
		 if (column == 1) 
			   value = Integer.parseInt(label) + 1 ;
		 else if (column == 2) 
			 if (label.equals("java")) value = 1; else value = 2;
		 else if (column == 3)
			 value = Integer.parseInt(label)+1;
		 else if (column == 4) 
			 if (label.equals("cs")) value = 1; else value = 2;
		 else if (column == 5){
			 if (label.equals("A")) value = 1;
			 else if(label.equals("B")) value = 2;
			 else value =3;
			 } 
		 //convert class labels to integer values
		 else 
			 if (label.equals("hire")) value = 1;
			 else  value = 2; 
			  
		 //return numerical value return value; 
		return value; 
	}
	private String Convert(int ClassName){
		String label; 
		
		if (ClassName == 1) label ="hire";
		else label="no";
		
		 return label;
	}
		
/*************************************************************************/ 

	//method to find training error,classified training data
	public void trainingError(){
		
		//initially zero errors
		int numberErrors = 0; 
					
		//for each record
		for (int i = 0; i < numberRecords; i++) 
		{ 
			int[] attributeArray = new int[numberAttributes]; 
					
			//read attributes 
			for (int j = 0; j < numberAttributes; j++) 		 
				attributeArray[j] = records.get(i).attributes[j]; 
					
				//find class predicted by classifier 
				int predClass = classify(attributeArray); 
					
				//read actual class from file 
				int actualClass = records.get(i).className;
					
				//errror if predicted and actual classes do not match 
				if (predClass != actualClass) 
					numberErrors += 1; 
		}
					
			//find and print error rate 
			double errorRate = 100.0*numberErrors/numberRecords; 
			System.out.println("Training Error: "+ errorRate + " %"); 
	}	
	
/********************************************************************/

	//method to find validation error,leave one out method
	public void ValidationError()
	{	
		//initially zero errors
		int numberErrors = 0; 
		
		copyRecords = new ArrayList<Record>(); 	//keep one copy of record
		Record validationRec;		//for splitted record between train and validation
		
		//make a copy of all records
		for (int i = 0; i < numberRecords; i++)  
			copyRecords.add(records.get(i));
		
		//make an object of class
		Ass2BayesPOneb obj= new Ass2BayesPOneb();
		
		//leave one out Iteration
		for(int i=0;i<copyRecords.size();i++)
		{
			records.clear();  //cleae the records for each leave out iteration
			validationRec=copyRecords.get(i);	//selected validation records
			
			//get the remaining  record for training
			for(int j=0;j<copyRecords.size();j++)
			{	
				if(i==j)					//if record is taken as validation
					j++;					//so go to next one
				if(j==copyRecords.size())		//if last record is taken 
						break;				//then come out of loop
				
				//add the rest of records to record
				records.add(copyRecords.get(j));
			}
			// Now build the new model
			obj.buildModel();
			
					
			//find class predicted by classifier 
			int predClass = classify(validationRec.attributes); 
				//System.out.println(predClass +", "+validationRec.className );
			//errror if predicted and actual classes do not match 
			if (predClass != validationRec.className) 
				numberErrors += 1; 
		}
					
			//find and print error rate 
			double errorRate = 100.0*numberErrors/numberRecords; 
			System.out.println("Validation Error: "+ errorRate + " %"); 
	}	
		
}
