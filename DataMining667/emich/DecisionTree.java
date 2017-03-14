package dMin.emich;

import java.io.*; 
import java.util.*; 


public class DecisionTree { 
	
/************************************************************/
						
	private class Record { //trainig record class 
	

		private int[] attributes;	//attributes of record 
		private int className;	//class of record 

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
		
		private void printNode(Node node){
			
			int NodeLevel =0; 
			Queue<Node> queue=new LinkedList<Node>();  
			  queue.add(node);  
			         while(!queue.isEmpty())  
				  {  
			        	 NodeLevel = queue.size();
			        	 while(NodeLevel>0)
			        	 { 
						   Node tempNode=queue.remove();  
						   
						   if(tempNode.nodeType.equalsIgnoreCase("leaf")){
							   System.out.print(tempNode.className + "	"); 
						   	}
						   if(tempNode.nodeType.equalsIgnoreCase("internal"))
							   System.out.print(tempNode.condition + "	"); 
						   
						   if(tempNode.left!=null)  
						    queue.add(tempNode.left);  
						   if(tempNode.right!=null)  
						    queue.add(tempNode.right);  
						   
						   NodeLevel--;
			        	 }
			        	 System.out.println();
				  }  
		}
	}
 
/************************************************************************/

	private Node root; 							//root of decision tree
	private ArrayList<Record> records;			//list of training records
	private ArrayList<Integer> attributes; 		//list if attributes
	private int numberRecords; 					//number of training records
	private int numberAttributes; 				 //number of attributes 
	private int numberClasses; 					//number of classes 
	
/*************************************************************************/ 
	//constructor of decision tree 
	public DecisionTree() { 
		root = null;					//initialize root, records,
		records = null; 				//attributes to empty 
		attributes = null; 				//number of records, attributes,
		numberRecords = 0;				 //classes are zero 
		numberAttributes = 0;
		numberClasses = 0; 
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
		//node.printNode(node);
		//return root node of tree that is built
		return node; 
	}	
/************************************************************************/
	
	//printing the decision tree
	public void printmyNode(){
		root.printNode(root);
	}	
/****************************************************** *******************/ 
		
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
		
		//method makes copy of list of attributes 
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
		double entropyLeft = entropy(leftRecords); 
		
		//find class entropy of right records 
		double entropyRight = entropy(rightRecords); 
		
		//find weighted average entropy 
		double average = entropyLeft*leftRecords.size()/records.size() + 
				entropyRight*rightRecords.size()/records.size(); 
		
		//System.out.println(average + " "+ attribute);
		//return weighted average entropy 
		return average;
	}

/*************************************************************************/ 

	//method finds class entropy of records using gini measure 
	private double entropy(ArrayList<Record> records)
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
			
			sum = 0;									 //find sum of squares 
			for (int i = 0; i < numberClasses; i++) 
				sum = sum + frequency[i]*frequency[i]; 
			
			return 1 - sum; 							//gini measure 
	}
	
/*********************************************************************/ 
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

		 //mehod loads training records from file 
		 public void loadTrainingData(String trainingFile1) throws IOException 
		 { 
			 Scanner inFile = new Scanner(new File(trainingFile1)); 
			 
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
						 //read attribute 
						 String label = inFile.next(); 
						 //convert to binary 
						 attributeArray[j] = convert(label, j+1); 
					 } 
				 
				//read class and convert to integer value 
				 String label = inFile.next(); 
				 int className = convert(label, numberAttributes+1); 
		
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
					String label = inFile.next(); 
					attributeArray[j] = convert(label, j+1); 
				} 
			
				//find class of attributes
				int className = classify(attributeArray); 
			
				//write attributes to output file
				for (int j = 0; j < numberAttributes; j++) 
				{ 
					String label = convert(attributeArray[j], j+1); 
					outFile.print(label + " "); 
				}
			
				//find class name from integer value and write to output file 
				String label = convert(className, numberAttributes+1); 
				outFile.println(label); 
			}
			
			inFile.close(); 
			outFile.close(); 
		} 
	 
/************************************************************************/ 
			
		 //method converts attribute labels to binary values and class labels 
		 //to integer values. 
		 private int convert(String label, int column) 
		 {
			 int value; 
			 
			//convert attribute labels to binary values 
			 if (column == 1) 
				 if (label.equals("highschool")) value = 0; else value = 1;
			 else if (column == 2) 
				 if (label.equals("smoker")) value = 0; else value = 1;
			 else if (column == 3)
				 if (label.equals("married")) value = 0; else value = 1; 
			 else if (column == 4) 
				 if (label.equals("male")) value = 0; else value = 1;
			 else if (column == 5)
				 if (label.equals("works")) value = 0; else value = 1; 
			 
			 //convert class labels to integer values
			 else 
				 if (label.equals("highrisk")) value = 1;
				 else if (label.equals("mediumrisk")) value = 2; 
				 else if (label.equals("lowrisk")) value = 3;
				 else value = 4; 
			
			 //return numerical value return value; 
			return value; 
		 }
			
/************************************************************************/ 
		
		 //method converts binary values to attribute labels 
		 //to class labels 
		 private String convert(int value, int column) 
		 { 
			String label; 
			
			//convert binary values to attribute labels
			if (column == 1)
				if (value == 0) label = "highschool"; else label = "college"; 
			else if (column == 2) 
				if (value == 0) label = "smoker"; else label = "nonsmoker"; 
			else if (column == 3)
				if (value == 0) label = "married"; else label = "notmarried"; 
			else if (column == 4) 
				if (value == 0) label = "male"; else label = "female"; 
			else if (column == 5) 
				if (value == 0) label = "works"; else label = "retired";
			
			//convert integer values to class labels 
			else 
				if (value == 1) label = "highrisk"; 
				else if (value == 2) label = "mediumrisk"; 
				else if (value == 3) label = "lowrisk"; 
				else label = "undetermined"; 
			 
			//return label 
			
			return label; 
		 }
}
		
		
