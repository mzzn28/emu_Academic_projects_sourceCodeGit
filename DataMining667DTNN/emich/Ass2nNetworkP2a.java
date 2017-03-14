package dMin.emich;

import java.io.*; 
import java.util.*; 


public class Ass2nNetworkP2a {

	//training record class 
		private class Record 
		{ 
			private double[] input;	//inputs of record
			private double[] output; //outputs of record 
		 
			//constructor of record 
			private Record(double[] input, double[] output) 
			{
				this.input = input; 	//assign inputs 
				this.output = output;	//assign outputs 
			}
		} 
		 
	/*************************************************************************/ 
		
		private int numberRecords;		//number of training records 
		private int numberInputs; 	 	//number of inputs 
		private int numberOutputs;		//number of outputs  	
		private int numberMiddle; 		//number of hidden nodes
		private int numberIterations;
		private int seed; 				//random number generator seed
		private double rate; 			//learning rate 
		private ArrayList<Record> records; 
		private double[] input;
		private double[] middle; 		//outputs at hidden nodes 
		private double[] output; 		//outputs at output nodes 
		private double[] errorMiddle;	//errors at hidden nodes
		private double[] errorOut; 
		private double[] thetaMiddle; 	//thetas at hidden nodes
		private double[] thetaOut; 		 //theats at output nodes 
		private double[][] matrixMiddle;	//weights between input/hidden nodes 
		private double[][] matrixOut; 		//weights between hidden/output nodes 	
		
/*************************************************************************/ 

	//constructor of neural network 
	public Ass2nNetworkP2a()
	{ 
			//parameters are zero 
			numberRecords = 0;
			numberInputs = 0; 
			numberOutputs = 0;
			numberMiddle = 0; 
			numberIterations = 0; 
			seed = 0; 
			rate=0;
			
			//Arrays are empty
			 records = null; 
			 input = null; 
			 middle = null; 
			 output = null; 
			 errorMiddle = null; 
			 errorOut = null; 
			 thetaMiddle = null; 
			 thetaOut = null; 
			 matrixMiddle = null; 
			 matrixOut = null; 
	} 

/************************************************************************/ 
		
	//method loads training records from training file 
	public void loadTrainingData(String trainingFile) throws IOException 
	{ 
			Scanner inFile = new Scanner(new File(trainingFile)); 
		
			//read number of records, inputs, outputs 
			numberRecords = inFile.nextInt(); 
			numberInputs = inFile.nextInt(); 
			numberOutputs = inFile.nextInt(); 
			
			//empty list of records 
			records = new ArrayList<Record>(); 
			
			//for each record 
			for (int i = 0; i < numberRecords; i++) 
			{ 
				//read inputs 
				double[] input = new double[numberInputs]; 
				for (int j = 0; j < numberInputs; j++) 
					input[j] = inFile.nextDouble(); 
				
				//read outputs 
				double[] output = new double[numberOutputs];
				for (int j = 0; j < numberOutputs; j++)
					output[j] = inFile.nextDouble(); 
				
				//create record 
				Record record = new Record(input, output); 
				
				//add record to list 
				records.add(record); 
			} 
			inFile.close();
	}
		
/*************************************************************************/ 	
	
	//method sets parameters of neural network 
	public void setParameters(int numberMiddle, int numberIterations,
									int seed, double rate) 
	{ 
		//set hidden nodes, iterations, rate 
		this.numberMiddle = numberMiddle;
		this.numberIterations = numberIterations; 
		this.seed = seed; 
		this.rate = rate;
			
		//initialize random number generation 
		Random rand = new Random(seed); 
			
		//create input/output arrays 
		input = new double[numberInputs]; 
		middle = new double[numberMiddle]; 
		output = new double[numberOutputs];
			
		//create error arrays
		errorMiddle = new double[numberMiddle]; 
		errorOut = new double[numberOutputs]; 
			
		//initialize thetas at hidden nodes 
		thetaMiddle = new double[numberMiddle];
		for (int i = 0; i < numberMiddle; i++) 
			thetaMiddle[i] = 2*rand.nextDouble() - 1; 
			
		//initialize thetas at output nodes 
		thetaOut = new double[numberOutputs]; 
		for (int i = 0; i < numberOutputs; i++)
			thetaOut[i] = 2*rand.nextDouble() - 1; 
			
		//initialize weights between input/hidden nodes 
		matrixMiddle = new double[numberInputs][numberMiddle]; 
		for (int i = 0; i < numberInputs; i++)
			for (int j = 0; j < numberMiddle; j++) 
				matrixMiddle[i][j] = 2*rand.nextDouble()-1; 
			
		//initialize weights between hidden/output nodes 
		matrixOut = new double[numberMiddle][numberOutputs]; 
		for (int i = 0; i < numberMiddle; i++) 
			for (int j = 0; j < numberOutputs; j++)
				matrixOut[i][j] = 2*rand.nextDouble() - 1; 
	} 
		
/*************************************************************************/ 	
	//method trains neural network 
	public void train() 
	{
		//repeat iteration number of times 
		for (int i = 0; i < numberIterations; i++) 
			//for each training record 
			for (int j = 0; j < numberRecords; j++) 
			{ 
				//calculate input/output 
				forwardCalculation(records.get(j).input); 
					
				//compute errors, update weights/thetas 
				backwardCalculation(records.get(j).output); 
			} 
	}
			
/************************************************************************/ 
	//method performs performs forward pass - computes input/output 
		private void forwardCalculation(double[] trainingInput) 
		{ 
			//feed inputs of record 
			for (int i = 0; i < numberInputs; i++) 
				input[i] = trainingInput[i]; 
			
			//for each hidden node
			for (int i = 0; i < numberMiddle;i++) 
			{
				double sum = 0;
				
				//compute input at hidden node 
				for (int j = 0; j < numberInputs; j++)
					sum += input[j]*matrixMiddle[j][i]; 
				
				//add theta 
				sum += thetaMiddle[i];
				
				//compute output at hidden node 
				middle[i] = 1/(1 + Math.exp(-sum)); 
			}
			
			//for each output node 
			for (int i = 0; i < numberOutputs; i++) 
			{
				double sum = 0; 
			
				//compute input at output node 
				for (int j = 0; j < numberMiddle; j++) 
					sum += middle[j]*matrixOut[j][i]; 
		
				//add theta 
				sum += thetaOut[i]; 

				//compute output at output node 
				output[i] = 1/(1 + Math.exp(-sum)); 
			}
		}
		
/*******************************************************************/ 
		
	//method performs backward pass - computes errors, updates weights/thetas 
	private void backwardCalculation(double[] trainingOutput) 
	{
			//compute error at each output node 
			for (int i = 0; i < numberOutputs; i++)
				errorOut[i] = output[i]*(1-output[i])*(trainingOutput[i]-output[i]); 
		
			//compute error at each hidden node 
			for (int i = 0; i < numberMiddle; i++)
			{
				double sum = 0; 
				
				for (int j = 0; j < numberOutputs; j++)
					sum += matrixOut[i][j]*errorOut[j]; 
		
				errorMiddle[i] = middle[i]*(1-middle[i])*sum; 
			}
			
			//update weights between hidden/output nodes 
			for (int i = 0; i < numberMiddle; i++) 
				for (int j = 0; j < numberOutputs; j++) 
					matrixOut[i][j] += rate*middle[i]*errorOut[j]; 
			
			//update weights between input/hidden nodes 
			for (int i = 0; i < numberInputs; i++) 
				for (int j = 0; j < numberMiddle; j++) 
					matrixMiddle[i][j] += rate*input[i]*errorMiddle[j]; 
			
			//update thetas at output nodes 
			for (int i = 0; i < numberOutputs; i++) 
				thetaOut[i] += rate*errorOut[i]; 
			
			//update thetas at hidden nodes 
			for (int i = 0; i < numberMiddle; i++) 
				thetaMiddle[i] += rate*errorMiddle[i]; 
		}
			
	
/*************************************************************************/ 	
	
	//method computes output of an input 
		private double[] test(double[] input) 
		{	
			//forward pass input 
			forwardCalculation(input); 
			
			//return output produced 
			return output; 
		}
			
/************************************************************************/ 

	//method reads inputs from input file and writes outputs to output file
	public void testData(String inputFile, String outputFile) throws IOException 
	{ 
			Scanner inFile = new Scanner(new File(inputFile)); 
			PrintWriter outFile = new PrintWriter(new FileWriter(outputFile)); 
			
			int numberRecords = inFile.nextInt(); 
			
			//for each record 
			for (int i = 0; i < numberRecords; i++) 
			{ 
				double[] input = new double[numberInputs]; 
				
				//read input from input file 
				for (int j = 0; j < numberInputs; j++) 
					input[j] = inFile.nextDouble();
				
				//find output using neural network 
				double[] output = test(input); 
			
				//write output to output file 
				for (int j = 0; j < numberOutputs; j++) 
					outFile.print(output[j] + " "); 
					
				outFile.println(); 
			} 
			inFile.close(); 
		}	
	
}
