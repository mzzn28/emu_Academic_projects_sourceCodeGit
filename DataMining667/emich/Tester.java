package dMin.emich;

import java.io.*; 
import java.util.*; 

public class Tester {

	public static void main(String[] args) throws IOException
	{
				
		Ass2nNetworkP3b network = new Ass2nNetworkP3b();
			
		network.loadTrainingData("train3snp.txt");
		
		network.setParameters(6,2000000, 2342, 0.8);
		network.train();
		System.out.print("validation File ");
		network.validateNetwork("valid3snp.txt");
		System.out.print("testFile");
		network.validateNetwork("test3snp.txt");
		//Write to output file
		network.testData("test3snp.txt", "classified.txt");
	
	}

}
