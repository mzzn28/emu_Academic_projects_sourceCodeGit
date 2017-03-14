package dMin.emich;

import java.io.*; 
import java.util.*; 


public class Ass2nNetworkP3a {
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
		
		private double minOut;
		private double maxOut;
		private double minIn[];
		private double maxIn[];
		
			
/*************************************************************************/ 

	//constructor of neural network 
	public Ass2nNetworkP3a()
	{ 
			//parameters are zero 
			numberRecords = 0;
			numberInputs = 0; 
			numberOutputs = 0;
			numberMiddle = 0; 
			numberIterations = 0; 
			seed = 0; 
			rate=0;
			minOut=0;
			maxOut=0;
				
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
			 
			 minIn=null;
			maxIn=null;
	} 

/***************************************************************/ 
			
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
				//read inputs  and convert to [0-1] range
				double[] input = new double[numberInputs]; 
				for (int j = 0; j < numberInputs; j++){ 
					input[j]=inFile.nextDouble();
				}
				
				//read outputs and convert to [0-1] range
				double[] output = new double[numberOutputs];
				for (int j = 0; j < numberOutputs; j++){
					output[j] =inFile.nextDouble();
					
				}
	
				//create record 
				Record record = new Record(input, output); 
					
				//add record to list 
				records.add(record); 
			} 
				inFile.close();
				//normalize the i/p & o/p to [0-1]
				normalizeRecordInputs();
				normalizeRecordsOutput();
				
	}

/***************************************************************/ 
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
				{
					double value=inFile.nextDouble();
					input[j]=normalizTestIn(value,j);
								
				}
				
				//find output using neural network 
				double[] output = test(input); 
					
				//write output to output file 
				for (int j = 0; j < numberOutputs; j++) {
							double lablel=revertOut(output[j]);
							outFile.print( lablel + " "); 
							System.out.print(lablel + " ");
				}
				outFile.println(); 
				System.out.println();
			} 
			inFile.close(); 
	}	

	/******************************************************************************/	
	//methods finds validates the network using the data from a file 
	public void validateNetwork(String validationFile) throws IOException 
	{ 
		Scanner inFile = new Scanner(new File(validationFile)); 
		int numberRecords = inFile.nextInt(); 
		
		//error is zero 
		double error = 0; 
		
		//for each record 
		for (int i = 0; i < numberRecords; i++) 
		{
			//read inputs 
			double[] input = new double[numberInputs]; 
			for (int j = 0; j < numberInputs; j++)
			{ 
				double value=inFile.nextDouble();
				input[j]=normalizTestIn(value,j);
			}
			//read outputs 
			double[] actualOutput = new double[numberOutputs];
			for (int j = 0; j < numberOutputs; j++)
			{
				double value=inFile.nextDouble();
				actualOutput[j] = normalizTestIn(value,numberInputs+1);
			}
			//find predicted output 
			double[] predictedOutput = test(input); 
			
			//print o/p
			//for (int j = 0; j < numberOutputs; j++){
				//System.out.print("predicted:"+ revertOut(predictedOutput[j]));
				//System.out.println(" , Actual:"+	revertOut(actualOutput[j]));}
			
			//find error between acutual and predicted outputs RMS 
			error += computeErrorRMS(actualOutput, predictedOutput); 
			
			///find error between acutual and predicted outputs match/mis-match
			//error += computeErrorMatch(actualOutput, predictedOutput);} 
		}
		//find average error 
		System.out.println("Validation Error: " + (error/numberRecords)*100); 
		inFile.close();
	}
	
/*************************************************************************/ 

	//method finds root mean square error between actual and predicted output 
	private double computeErrorRMS(double[] actualOutput, double[] predictedOutput) 
	{
		double error = 0; 
	
		//sum of squares of errors 
		for (int i = 0; i < actualOutput.length; i++) 
			error += Math.pow(actualOutput[i] - predictedOutput[i], 2); 
	
		//root mean square error return 
		return Math.sqrt(error/actualOutput.length); 
	} 
	
/**********************************************************************/ 		
	
	//Helping Methods to normalize inputs (Too many, can be generalise , may be later)
	private void normalizeRecordInputs(){
			
			minIn=new double[numberInputs];
			maxIn=new double[numberInputs];
			//find max and min of all inputs
			for(int i=0;i<numberInputs; i++){
				 minIn[i]=findMinIn(i);
				 maxIn[i]=findMaxIn(i);
				
				//normalize the input at i
				for(int j=0;j<records.size();j++){
					
					//normalize the record jth record ith input 
					double value=records.get(j).input[i];
						value=(value-minIn[i])/(maxIn[i]-minIn[i]);
						
					//save the record in records
						records.get(j).input[i]=value;
						//System.out.println(records.get(j).input[i]);
				}
			}	
	}
	private double findMinIn(int index){
				
				double minIn=0;
				//find the min of inputs and out puts
				for(int i=0;i<records.size();i++){
						if(minIn> records.get(i).input[index])
							minIn=records.get(i).input[index];
					}
				//System.out.println("Min: " + minIn);
				return minIn;
	}
	private double findMaxIn(int index){
			
			double maxIn=-1;
			//find the min of inputs and out puts
			for(int i=0;i<records.size();i++){
					if(maxIn< records.get(i).input[index])
						maxIn=records.get(i).input[index];
				}
			//System.out.println("Max: " + maxIn);
			return maxIn;
	}
	
	//method to normalize test and validation file i/o
	private double normalizTestIn(double value,int index){
		
		double normalValue=0;
		
		if(index>numberInputs)	//its output
			normalValue=(value-minOut)/(maxOut-minOut);
		else					//it's input
			normalValue=(value-minIn[index])/(maxIn[index]-minIn[index]);
			
		
		return normalValue;
	}
	/**************************************************************/	
	//Helping Methods to normalize outputs
	private void normalizeRecordsOutput(){
			
			//find max and min of all inputs
			for(int i=0;i<numberOutputs; i++){
				 minOut=findMinOut(i);
				 maxOut=findMaxOut(i);
				
				//normalize the input at i
				for(int j=0;j<records.size();j++){
					
					//normalize the  jth record ith output 
					double value=records.get(j).output[i];
						value=(value-minOut)/(maxOut-minOut);
						
					//save the record in records
						records.get(j).output[i]=value;
						//System.out.println(revertOut(records.get(j).output[i]));
				}
			}	
		}
	//can be done in above function but building it seprate
	private double findMinOut(int index){
			
			double min=0;
			//find the min of inputs and out puts
			for(int i=0;i<records.size();i++){
					if(min> records.get(i).output[index])
						min=records.get(i).output[index];
				}
			//System.out.println("Min:" + min);
			return min;
	}
	private double findMaxOut(int index){
	
		double max=0;
		//find the min of inputs and out puts
		for(int i=0;i<records.size();i++){
				if(max< records.get(i).output[index])
					max=records.get(i).output[index];
			}
		//System.out.println("Max:" + max);
		return max;
	}

/**************************************************************/
//method to revert outputs to origional format	
private double revertOut(double normalvalue)
{
	double actualValue=0;
		
		actualValue= (normalvalue*(maxOut-minOut)) + minOut;
		
		return actualValue;
}
	
	
}