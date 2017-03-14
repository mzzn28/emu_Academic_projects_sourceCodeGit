package dMin.emich;

import java.io.*; 
import java.util.*; 

public class Ass2nNetworkP2c {
	
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
	public Ass2nNetworkP2c()
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
			//read inputs  and convert to [0-1] range
			double[] input = new double[numberInputs]; 
			for (int j = 0; j < numberInputs; j++)
			{ 
				String label=inFile.next();
				input[j] = convert(label, j+1);
			}
				
			//read outputs and convert to [0-1] range
			double[] output = new double[numberOutputs];
			for (int j = 0; j < numberOutputs; j++)
			{
				String label=inFile.next();
				output[j] = convert(label, numberInputs+1);
			}
	
			//create record 
			Record record = new Record(input, output); 
					
			//add record to list 
			records.add(record); 
		} 
			inFile.close();
	}
	
/************************************************************************/
	
	//method sets parameters of neural network 
	public void setParameters(int numberMiddle, int numberIterations,
										int seed, double rate) { 
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
					String label=inFile.next();
					input[j] = convert(label, j+1);
				}
					
				//find output using neural network 
				double[] output = test(input); 
				
				//write output to output file 
				for (int j = 0; j < numberOutputs; j++) {
					String lablel=revertOut(output[j]);
					outFile.print( lablel + " "); 
						System.out.println(lablel);
				}
				outFile.println(); 
			} 
			inFile.close(); 
	}	
			
/*************************************************************************/ 		 
	 
	//method converts attribute values to normalized [0-1], hard coded for 
	//specific problem -2 credit based loan of bank
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
		 //convert fourth column gender attribute to [0, 1] 
		 else if (column == 4)
		 { 
			 if (label.equals("male")) 
				 value = 0.0; 
			 else 
				 value=0.99;
		 }
		//convert fourth column marital status attribute to [0- 1]
		 else if (column == 5)
		 { 
			 if (label.equals("single")) 
				 value = 0.33; 
			 else if (label.equals("married")) 
				 value=0.66;
			 else
				 value=0.99;
		 }
		//convert fourth column class to number [0-1] .
		 else
		 {
			 if(label.equals("low"))
				 value= 0.25;
			 else if (label.equals("medium"))
				 value= 0.50;
			 else if (label.equals("high"))
				 value=0.75;
			 else
				 value = 0.99;
		 }
		 return value;
	}
	
/****************************************************************************/
	//method that revert back o/p normalize value to to original class value
	private String revertOut(double value)
	{		
			String outClass=null;
			
			//initialize normalize output values
			double a=0.25,b=0.50,c=0.75, d=0.99;
			
			//find the %age error with each o/p and predicted o/p
			double errrorPercenta= Math.abs(value-a)/a;
			double errrorPercentb= Math.abs(value-b)/b;
			double errrorPercentc= Math.abs(value-c)/c;
			double errrorPercentd= Math.abs(value-d)/d;
			
			if(errrorPercenta<errrorPercentb && errrorPercenta< errrorPercentc 
					&&  errrorPercenta< errrorPercentd)
				outClass="low";
			else if(errrorPercentb<errrorPercentc && errrorPercentb< errrorPercenta
					&& errrorPercentb< errrorPercentd)
				outClass="medium";
			else if(errrorPercentc<errrorPercentb && errrorPercentc< errrorPercenta 
					&& errrorPercentc< errrorPercentd)
				outClass="high";
			else
				outClass="undetermined";
			
			return outClass;
		}
		
/*****************************************************************/
		
	//method finds the training error
	public void trainingError()
	{
		//As we already trained network we can just test training data as
		// validation data and see if they match with actual
		
		double error=0;
		
		for (int i = 0; i < numberRecords; i++) 
		{
			//find predicted output 
			double[] predictedOutput = test(records.get(i).input);
			
			error += computeErrorRMS(records.get(i).output, predictedOutput); 
		
			//for (int j = 0; j < numberOutputs; j++){
			//	System.out.print("predicted:"+ predictedOutput[j] + " "+ revertOut(predictedOutput[j]));
			//	System.out.println(" , Actual:"+ records.get(i).output[j]);}
		}
		 System.out.println("Training Error: " + (error/numberRecords)*100); 
		
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
				String label=inFile.next();
				input[j] = convert(label, j+1);
			}
			//read outputs 
			double[] actualOutput = new double[numberOutputs];
			for (int j = 0; j < numberOutputs; j++)
			{
				String label=inFile.next();
				actualOutput[j] = convert(label,numberInputs+1 );
			}
			//find predicted output 
			double[] predictedOutput = test(input); 
			
			//print o/p
			//for (int j = 0; j < numberOutputs; j++){
			//	System.out.print("predicted:"+ predictedOutput[j] + " "+ revertOut(predictedOutput[j]));
			//	System.out.println(" , Actual:"+	actualOutput[j]);}
			
			//find error between acutual and predicted outputs RMS 
			error += computeErrorRMS(actualOutput, predictedOutput); 
			
			///find error between acutual and predicted outputs match/mis-match
			//error += computeErrorMatch(actualOutput, predictedOutput);
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
	
	//method find if predicted o/p and actual o/p match or mis-match
	private double computeErrorMatch(double[] actualOutput, double[] predictedOutput) 
	{
		double error = 0; 
	
		//sum of squares of errors 
		for (int i = 0; i < actualOutput.length; i++)
		{ 
			//find the %age error b/w both outputs
			double errrorPercent= (Math.abs(actualOutput[i]-predictedOutput[i])/actualOutput[i])*100;
			
			//if error is less than certain value(let's say 10% error we are allowing we can increase
			//or decrease depend upon the  problem) its miss-match
			if	(errrorPercent>15)
				error += 1; 
		}
		//root mean square error return 
		return error; 
	} 
}
