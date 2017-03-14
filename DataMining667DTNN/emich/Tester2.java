package dMin.emich;

import java.io.IOException;

public class Tester2 {

	public static void main(String[] args) throws IOException
	{
		AssignNearestNeighbour23 treeclassifier22 = new AssignNearestNeighbour23();
		treeclassifier22.loadTrainingData("trainfile23.txt");
		treeclassifier22.classifyData("testFile23.txt", "classifiedFile23.txt");
		treeclassifier22.tariningError();
		treeclassifier22.validationErrorOneOut();
	}
}
