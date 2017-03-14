package dMinCluster.emich;

import java.io.IOException;

public class Clustertest {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Ass3PThree tester= new Ass3PThree(512);
		tester.loadData("imagefile");
		
		//parameters
		tester.setParameters(8,1624);
		tester.cluster();
		
		tester.displayResults("output1","output2");
		tester.findCompRatio();
		
	}

}
