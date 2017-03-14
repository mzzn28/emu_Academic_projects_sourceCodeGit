package packageone;

import java.io.*;
import java.util.*;

public class LabOnePowerset {

	public static void main(String[] args) {
		
		//read the input positive integer from user into number
		
		InputStreamReader read = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(read);
        int number = 0;
                System.out.println("Enter the number");
                try {
					number = Integer.parseInt(in.readLine());
				} catch (Exception e) {
					
					e.getMessage();
				}
		//get the integer list from user input
                
		List inputlist= getIntList(number);
		//call the all subsets method to print o/p
		
        System.out.println("Input Set:"+inputlist);	
        System.out.println("All subset :"+ AllSubSets(inputlist)); 
	}
	
	private static List getIntList(int number) {
		
		List<Integer> inputList =new ArrayList <Integer>();
		for(int i=0;i<number;i++)
		{
			inputList.add(i+1);
		}
		
		return inputList;
	}

	public static Set<Set<Integer>> AllSubSets(List<Integer> intList){

	    Set<Set<Integer>> res = new HashSet();
	    res.add(new HashSet());
	    
        //outer loop will iterate through all the input list element and 
	    //inner loop will iterate though resultant set starting from [] set 
	    //and add elements in each set as for [1]-->[[],[1]].
	    
	    for (int i=1 ;i<=intList.size();i++){
	    	Set<Set<Integer>> tmp = new HashSet();
	    		for(Set<Integer> innerSet : res){

	    			innerSet = new HashSet(innerSet);
	    			innerSet.add(i);                
	    			tmp.add(innerSet);
	    		}
	    	//union with last res set.	
	        res.addAll(tmp);
	    }
	    return res;
	}
    
}
