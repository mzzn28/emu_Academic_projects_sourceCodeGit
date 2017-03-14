package dMin.emich;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class AssignNumberImageClassifier {

private class Record{
		
		private int [][] attributes;
		private int className;
		
		public Record(int [][] attributes, int className ){
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
	
	
	 public AssignNumberImageClassifier() { 
		 
		 records = null; 
		 attributeTypes = null; 
		 numberRecords = 50;
		 numberAttributes = 20;
		 numberClasses = 10;
		 numberNeighbors = 0; 
		 distanceMeasure = null; 
		 majorityRule = null; 
	 }
	
/*****************************************************/
	 
	//method loads training records from training file 
	public void loadTrainingData(String trainingFile) throws IOException
	 { 
			 Scanner inFile = new Scanner(new File(trainingFile)); 
			
			 //empty list of records 
			 	records = new ArrayList<Record>(); 
			 	
			//for each record 
			for (int i = 0; i < numberRecords; i++) 
			{
				//create attribute array 
				int[][] attributeArray = new int[numberAttributes][numberAttributes]; 
				
				//read attributes and convert them to numerical form 
				for (int j = 0; j < numberAttributes; j++) 
				{	
					for (int k = 0; j < numberAttributes; j++) 
					if(inFile.hasNext()){
						attributeArray[j][k]  =Integer.parseInt( inFile.next());
						
					}
				}
				int classname=0;
				asignClass(classname, i );
				Record record = new Record(attributeArray, classname); 
				records.add(record);
			}
			inFile.close();
	 }
	private void asignClass(int classname,int recnum ){
		
		//class one for 1st five record
		//class 2 for 6-10 record 
		//class 3 for 11-15
		//class 4 for 15-20
		
	}
}
