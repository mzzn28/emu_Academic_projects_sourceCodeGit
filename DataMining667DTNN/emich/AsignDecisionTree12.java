package dMin.emich;

import java.io.*; 
import java.util.*; 


public class AsignDecisionTree12 {

/************************************************************/
	
	private class Record { //trainig record class 
	

		public int[] attributes;	//attributes of record 
		public int className;	//class of record 

		//constructor of record 
		private Record(int[] attributes, int className) {
			this.attributes = attributes; 	//assign attributes & assign class 
			this.className = className;
		}
	}

/*************************************************************************/ 

	//decision tree node class 
	private class Node
	{ 
		private String nodeType;	//node type - internal or leaf
		private int condition;		//condition if node is internal
		private int className; 		//class name if node is leaf
		private Node left;			//left branch 
		private Node right; 		//right branch 
   
		//constructor of node 
		private Node(String nodeType, int value, Node left, Node right) 
		{ 
			this.nodeType = nodeType; 	//assign node type  
			this .left = left; 			//assign left branch
			this.right = right; 		//assign right branch 

			if (nodeType.equals("internal")) //if node is internal { 
				{
					condition = value; 	//assign condition to node ame 
					className = -1; 	//node has no class name
				}
			else{						//if node is leaf 
				className = value;		//assign class name to node
				condition = -1; 		//node has no condition 
			}
		}
		//method for Printing the tree 
		private void printNode(Node node){
			
			int NodeLevel =0; 	//initial level of tree
			//level order traversal is used and keep track of levels
			//to trace the tree, stack the node in queue
			Queue<Node> queue=new LinkedList<Node>();  
			  queue.add(node);  //insert the root node in queue
			  
			         while(!queue.isEmpty())  
				  {  
			        	 NodeLevel = queue.size(); //to keep track of levels
			        	 while(NodeLevel>0)
			        	 { 
						   Node tempNode=queue.remove();  
						   
						   //if leaf then print the class name and L
						   if(tempNode.nodeType.equalsIgnoreCase("leaf")){
							   System.out.print(tempNode.className + "L	"); 
						   	}
						   //if internal node print the attribute no./condition
						   if(tempNode.nodeType.equalsIgnoreCase("internal"))
							   System.out.print(tempNode.condition + "	"); 
						   
						   if(tempNode.left!=null)  
						    queue.add(tempNode.left);  //add left node
						   if(tempNode.right!=null)  
						    queue.add(tempNode.right);  //add right node
						   
						   NodeLevel--;
			        	 }
			        	 //new line for next level
			        	 System.out.println();
				  }  
		}
	}
 
/************************************************************************/
	
	//intialize tree and its attributes through constructor
	private Node root; 							//root of decision tree
	private ArrayList<Record> records;			//list of training records
	private ArrayList<Integer> attributes; 		//list if attributes
	private int numberRecords; 					//number of training records
	private int numberAttributes; 				 //number of attributes 
	private int numberClasses; 					//number of classes 
	
	//constructor of decision tree 
	public AsignDecisionTree12() { 
		root = null;					//initialize root, records,
		records = null; 				//attributes to empty 
		attributes = null; 				//number of records, attributes,
		numberRecords = 0;				 //classes are zero 
		numberAttributes = 0;
		numberClasses = 0; 
	} 

/*************************************************************************/ 

	 //mehod loads training records from file 
	 public void loadTrainingData(String trainingFile) throws IOException 
	 { 
		 
		 Scanner inFile = new Scanner(new File(trainingFile)); 
		 
		 //read number of records, attributes, classes 
		 numberRecords = inFile.nextInt(); 
		 numberAttributes = inFile.nextInt(); 
		 numberClasses = inFile.nextInt(); 
		 
		 		 
		 //empty list of records 
		 records = new ArrayList<Record>();
		 
		 //for each record
		 for (int i = 0; i < numberRecords; i++)
		 { 	 
		 
			 //create attribute array 
			 int[] attributeArray = new int[numberAttributes]; 
			 
				 //for each attribute 
				 for (int j = 0; j < numberAttributes; j++) 
				 { 
					 //read attribute value and save it
					 int label = Integer.parseInt(inFile.next()); 
					 attributeArray[j] = label;
				 } 
			 
			//read class and convert to integer value 
			 int className =  Integer.parseInt(inFile.next()); 
	
			 //create record using attributes and class 
			 Record record = new Record(attributeArray, className); 
	
			 //add record to list 
			 records.add(record); 
		 }

		//ceate list of attributes
		attributes = new ArrayList<Integer>(); 
		 for (int i = 0; i < numberAttributes; i++)
			 attributes.add(i+1); 
		
		 inFile.close(); 
	} 
		
/*************************************************************************/ 
	
	//method builds decision tree for the whole training data 
		public void buildTree() 
		{
			root = build(records, attributes); 		//initial call to build method 
		}
		
/*************************************************************************/ 
		
		//method builds decision tree from given records and attributes
		//returns root of tree that is built
		private Node build(ArrayList<Record> records, ArrayList<Integer> attributes) { 
		
			//root node is empty initially 
			Node node = null; 
		
			//if all records have same class 
			if (sameClass(records)) { 
				
				//find class name 
				int className = records.get(0).className; 
				
				//node is leaf with that class 
				node = new Node("leaf", className, null, null); 
			} 
			//if there are no attributes 
			else if (attributes.isEmpty()) 
			{ 
				//find majority class of records 
				int className = majorityClass(records); 
		
				//node is leaf with that class
				node = new Node("leaf", className, null, null); 
			}
			else
			{ 
		
				//find best condition for current records and attributes
				int condition = bestCondition(records, attributes); 
				
				//collect all records which have 0 for condition 
				ArrayList<Record> leftRecords = collect(records, condition, 0);
		
				//collect all records which have 1 for condition 
				ArrayList<Record> rightRecords = collect(records, condition, 1); 
			
			
				//if either left records or right records is empty 
				if (leftRecords.isEmpty() || rightRecords.isEmpty()) 
				{
					//find majority class of records
					int className = majorityClass(records);
					//node is leaf with that class
					node = new Node("leaf", className, null, null);
				}
				else
				{
					//create copies of current attributes 
					ArrayList<Integer> leftAttributes = copyAttributes(attributes);
					ArrayList<Integer> rightAttributes = copyAttributes(attributes); 
			
					//remove current condition from current attributes 
					leftAttributes.remove(new Integer(condition)); 
					rightAttributes.remove(new Integer(condition)); 
					
					//create internal node with current condition 
					node = new Node("internal", condition, null, null); 
			
					//create left subtree recursively 
					node.left = build(leftRecords, leftAttributes); 
					
					//create right subtree recursively
					node.right = build(rightRecords, rightAttributes); 
				}
			}
			//return root node of tree that is built
			return node; 
		}	
/********************************************************************/		
		//printing the decision tree
		public void printmyNode(){
			System.out.println("Tree traversal:");
			root.printNode(root);
		}	
					
/*********************************************************************/ 

		//method decides whether all records have the same class 
		private boolean sameClass(ArrayList<Record> records) 
		{ 
			
			//compare class of each record with class of first record 
			for (int i = 0; i < records.size(); i++) 
				if (records.get(i).className != records.get(0).className) 
					return false;
			
			return true; 
		}
		
/**************** *********************************************************/ 
		
		//method finds the majority class of records
		private int majorityClass(ArrayList<Record> records) 
		{ 
			
			int[] frequency = new int[numberClasses]; 	//frequency array 
			
			for (int i = 0; i < numberClasses; i++)
				frequency[i] = 0; 	//initialize frequencies 
			
			for (int i = 0; i < records.size(); i++) //find frequencies of classes 
				frequency[records.get(i).className - 1] += 1; 

			int maxIndex = 0;						 	//find class with maximimum 
			for (int i = 0; i < numberClasses; i++) 	//frequency 
				if (frequency[i] > frequency[maxIndex])
					maxIndex = i; 
			
			//return majority class 
			return maxIndex + 1; 	
		} 
		
/*************************************************************************/ 
		
		//method finds best condition for given records and attributes
		private int bestCondition(ArrayList<Record> records, ArrayList<Integer> attributes) 
		{
			//evaluate first attribute 
			double minValue = evaluate(records, attributes.get(0)); 
			int minIndex = 0; 
		
			//go thru all attributes 
			for (int i = 0; i < attributes.size(); i++) 
			{
				double value = evaluate(records, attributes.get(i)); //evaluate attribute 
			
				if (value < minValue)
				{							 //if value is less than 
				
						minValue = value; 	//current minimum then
						minIndex = i; 		 //update minimum 
				}
			}
			return attributes.get(minIndex); //return best attribute 
		}

		
/*************************************************************************/ 

	//method evaluates an attribute using weighted average entropy
		private double evaluate(ArrayList<Record> records, int attribute) 
		{ 
			//collect records that have attribute value 0 
			ArrayList<Record> leftRecords = collect(records, attribute, 0); 
			
			//collect records that have attribute value 1 
			ArrayList<Record> rightRecords = collect(records, attribute, 1); 
			
			//find class entropy of left records 
			double entropyLeft = entropyG(leftRecords); 
			
			//find class entropy of right records 
			double entropyRight = entropyG(rightRecords); 
			
			//find weighted average entropy 
			double average = entropyLeft*leftRecords.size()/records.size() + 
					entropyRight*rightRecords.size()/records.size(); 

			//return weighted average entropy 
			return average;
		}

/*************************************************************************/ 
		
		//method collects records that have a given value for a given attribute 
		private ArrayList<Record> collect(ArrayList<Record> records, int condition, int value)
		{ 
			//initialize collection 
			ArrayList<Record> result = new ArrayList<Record>(); 
			
			//go thru records and collect those that have given value 
			//for given attribute
			for (int i = 0; i < records.size(); i++) 
				if (records.get(i).attributes[condition-1] == value) 
					result.add(records.get(i)); 
			
			//return collection 
			return result; 
		}	
		
/*************************************************************************/

	//method finds class entropy of records gini
	private double entropyG(ArrayList<Record> records)
	{ 
			double[] frequency = new double[numberClasses]; //frequency array 
			
				for (int i = 0; i < numberClasses; i++) 	//initialize frequencies 
					frequency[i] = 0; 
				
				for (int i = 0; i < records.size(); i++) //find class frequencies 
					frequency[records.get(i).className - 1] += 1; 
				
				double sum = 0; 							//find sum of frequencies
				for (int i = 0; i < numberClasses; i++) 
					sum = sum + frequency[i];
				
				for (int i = 0; i < numberClasses; i++) 	//normalize frequencies
					frequency[i] = frequency[i]/sum; 
				
					sum = 0;								//find sum of squares 
					for (int i = 0; i < numberClasses; i++) 
						sum = sum + frequency[i]*frequency[i]; 
					
					return 1 - sum; 							//gini measure 
				
	}

	//method finds class entropy of records class measure
	private double entropyC(ArrayList<Record> records)
		{ 
			double[] frequency = new double[numberClasses]; //frequency array 
			
				for (int i = 0; i < numberClasses; i++) 	//initialize frequencies 
					frequency[i] = 0; 
				
				for (int i = 0; i < records.size(); i++) //find class frequencies 
					frequency[records.get(i).className - 1] += 1; 
				
				double sum = 0; 							//find sum of frequencies
				for (int i = 0; i < numberClasses; i++) 
					sum = sum + frequency[i];
				
				for (int i = 0; i < numberClasses; i++) 	//normalize frequencies
					frequency[i] = frequency[i]/sum; 
				
					double max=0;
					for (int i = 0; i < numberClasses; i++) //find max
					{
						if(max> frequency[i])
							max=frequency[i];
					}
					return 1-max;
				
		}	
	//method finds class entropy of records Shanon's entropy
	private double entropyS(ArrayList<Record> records)
	{ 
		double[] frequency = new double[numberClasses]; //frequency array 
		
			for (int i = 0; i < numberClasses; i++) 	//initialize frequencies 
				frequency[i] = 0; 
			
			for (int i = 0; i < records.size(); i++) //find class frequencies 
				frequency[records.get(i).className - 1] += 1; 
			
			double sum = 0; 							//find sum of frequencies
			for (int i = 0; i < numberClasses; i++) 
				sum = sum + frequency[i];
			
			for (int i = 0; i < numberClasses; i++) 	//normalize frequencies
				frequency[i] = frequency[i]/sum; 
			sum=0;								//find sum of logs 
			for (int i = 0; i < numberClasses; i++) 
				sum = sum + frequency[i] * logOfBase(2,frequency[i]); 
			
			return (sum *(-1)); 							//Shanon's measure 
				
			
	}	
/*********************************************************************/ 
		
		private ArrayList<Integer> copyAttributes(ArrayList<Integer> attributes) 
		{ 
			
			//initialize copy list 
			ArrayList<Integer> result = new ArrayList<Integer>(); 
			
			//insert all attributes into copy list 
			for (int i = 0; i < attributes.size(); i++) 
				result.add(attributes.get(i)); 
			
			//return copy list 
			return result; 
		}
		
/************************************************************************/
	
		//method reads test records from file and writes classified records 
		 //to output file 
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
			
				//read attributes and convert to binary 
				for (int j = 0; j < numberAttributes; j++) 
				{ 
					attributeArray[j] = Integer.parseInt(inFile.next()); 
				} 
			
				//find class of attributes
				int className = classify(attributeArray); 
			
				//write attributes to output file
				for (int j = 0; j < numberAttributes; j++) 
				{ 
					int label = attributeArray[j]; 
					outFile.print(label + " "); 
				}
			
				//find class name from integer value and write to output file 
				int label = className; 
				outFile.println(label); 
			}
			
			inFile.close(); 
			outFile.close(); 
		} 
	 
/************************************************************************/ 
		 
		//method finds class of given attributes 
		 private int classify(int[] attributes)
		 { 
			 
			 //start at root node 
			 Node current = root; 
			 
			 //go down the tree 
			 while (current.nodeType.equals("internal")) 
			 {			 										//if attribute value 
				 if (attributes[current.condition - 1] == 0)	 //of condition is 0 
					 current = current.left;			 		//go to left 
				else	 
						current = current.right; 				//else go to right 
								
				 
			}		 											//return class name
			 return current.className;	 						//when reaching leaf 
		 }

/*************************************************************************/ 
		
	//method to compute training error
	public void tariningError(){
		
		
		int[] attributeArray = new int[numberAttributes];	//to save the one set of attributes 
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
		
		System.out.println("Training Error: "+ incorrectClassified);
		
		
	}
	
/*************************************************************************/ 

	//method to compute validation error leave one out
	public void validationErrorOneOut(){
				
				int InCorrectvalidated=0;			//count incorrect class validation
				int className,tmpClassName;			//class from record and to classify	
				double [] validationError= new double[records.size()];
				Record validationRecord;
				ArrayList <Record> trainRecords = new ArrayList<Record>();
				
				//leave one out for validation records
				for(int i=0;i<records.size();i++)
				{
					validationRecord=records.get(i);	//selected validation records
					
						//get the remaining  record for training
						for(int j=0;j<records.size();j++){	
							if(i==j)						//record is taken as validation
								j++;						//so go to next one
							if(j==records.size())		//if last record is taken 
									break;					//then come out of loop
							trainRecords.add(records.get(j));
						}
						
					//build the tree	
					root=build(trainRecords,attributes);
					
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

	/*************************************************************************/ 
	//method to compute validation error by random  sampling
	public void validationErrorRandS()
	{
		int rand = (int) Math.ceil(Math.random() * 50);
		//let's take rand% of training record to validate the result and 
		//rest for train the tree.
		
		double segPoint=(records.size()*rand/100 );
		
		int InCorrectvalidated=0;			//count incorrect class validation
		int className,tmpClassName;			//class from record and to classify	
		double validationError;
		ArrayList <Record> validateRecords = new ArrayList<Record>();
		ArrayList <Record> trainRecords = new ArrayList<Record>();
			
			//segrigate the record
			for(int i=0;i<records.size();i++)
			{
				if(i<segPoint)
					validateRecords.add(records.get(i));
				else
					trainRecords.add(records.get(i));
			}
			
			//build the tree	
			root=build(trainRecords,attributes);
			//validate result
			for(int j=0;j<validateRecords.size();j++)
			{
				tmpClassName = classify(validateRecords.get(j).attributes);
				className=validateRecords.get(j).className;
			
				//compare the classes
				if(className!=tmpClassName)
					InCorrectvalidated++;	//increment error count
			}
			//compute result
			validationError=InCorrectvalidated/validateRecords.size();	
			double roundOffError = Math.round(validationError * 100.0) / 100.0;
			System.out.println( "Random Sampling validation Error: "+ roundOffError );
	}
	
	/*************************************************************************/ 
	//Extract rules from tree
	ArrayList <String> rules;
	public void extractRule(){
		
		Node current=root;
		int tmpcondition;
		if(current.nodeType.equalsIgnoreCase("internal")){
			tmpcondition=current.condition;
			makerule(tmpcondition);
			
		}
		
	}
	private void makerule(int tmpcondition){
		
	}
	/*************************************************************************/ 
	//helper log function
	public double logOfBase(double base, double num) {
	    return Math.log(num) / Math.log(base);
	}
	
}
