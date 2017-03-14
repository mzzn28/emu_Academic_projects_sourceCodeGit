package packageone;

//Although jama matrix package is used for different matrix computation
//boolean multiplication function is defined.
//(comment:this package doesn't help that much as i thought.

import java.util.Scanner;
import Jama.Matrix;

public class LabTwoMatrix {

	public static void main(String[] args) {
	
	      Scanner in = new Scanner(System.in);
	      //as relation on R is always square matrix so m=n
	      System.out.println("Enter the number of rows/columns of n*n matrix");
	      int m = in.nextInt();
	      
	      Matrix a= new Matrix(m,m );
	     
	      //taking input elements.
	      System.out.println("Enter the elements of matrix 0 or 1");
	 
	      for ( int i = 0 ; i < m ; i++ )
	         for (int j = 0 ; j < m ; j++ ){
	        	 System.out.print("Element [" + i +"][" + j+ "]:");
	        	 	double val = in.nextInt();
	            	a.set(i, j, val);	
	         }
	      //call all function required.
	      symmetry(a);
	      antisymmetry(a);
	      reflexiv(a);
	      trasitiv(a);
	     
	}
	// for symmetry just checked tranpose and compare with origional
	//matrix if these are equal.
	private static void symmetry(Matrix a){
		 Matrix at=a.transpose();
		 int n=a.getRowDimension();
		 int eql=0;
		 
			 for ( int i = 0 ; i < n ; i++ ){
		         for (int j = 0 ; j < n ; j++ ){
		        	 if(a.get(i, j)==at.get(i, j)){
		        	 eql++;
		        	 }
		         }
			 }
			 if(eql== n*n)
				 System.out.println("Symmetric: yes.");
			 else
				System.out.println("Symmetric: no, as transpose are not equal."); 
	}
	
	//The relation R is antisymmetric if and only if (a, b) ∈ R and (b, a) ∈ R imply that a = b.
	//Consequently, the matrix of an antisymmetric relation has the property that if Aij = 1 with
	//	i != j , then Aji = 0. 
	
	private static void antisymmetry(Matrix a){
		
		 int n=a.getRowDimension();
		 int counter=0;	 
			 for ( int i = 0 ; i < n ; i++ ){
		         for (int j = 0 ; j < n ; j++ ){
		        	 if(i==j)
		        		break; 
		        	 else{
			        	 if(a.get(i, j)==1 && a.get(j, i)==1)
			        	 counter++;
		        	 }
		         }
			 }
			 if(counter== 0)
				 System.out.println("AntiSymmetric: yes.");
			 else
				System.out.println("Anti-Symmetric: no, as for every value and the value in its\n"
						+ " transposed position are not antisymmerical."); 
		 
	}
	//to check transitivity:  MR square <= MR
	private static void reflexiv(Matrix a){
		int n=a.getRowDimension();
		int counter=0;
		for ( int i = 0 ; i < n ; i++ ){
			if(a.get(i, i)==1)
				counter++;
		}
		 if(n== counter)
			 System.out.println("Reflexive: yes.");
		 else
			System.out.println("Reflexive: no, as diagonal elements are not 1's."); 
		 
	}
	//to check transitivity:  MR square <= MR
	private static void trasitiv(Matrix a){
	    int n=a.getRowDimension();
	    int counter=0;
	    //get the square for MR.
		Matrix b=multiply(a,a);
			for ( int i = 0 ; i < n ; i++ ){
	         for (int j = 0 ; j < n ; j++ ){
	        	 if(b.get(i, j)<=a.get(i, j))
	        		 counter++;
	         }
			}
			 if(counter== n*n)
				 System.out.println("Transitive: yes.");
			 else
				System.out.println("Transitive: no, as MR2 is not less than or equal to MR."); 
	}
	//to diplay any matrix to see results
	private static void showmat(Matrix a){
		for ( int i = 0 ; i < a.getRowDimension() ; i++ ){
	         for (int j = 0 ; j < a.getColumnDimension() ; j++ ){
	        	 System.out.print(a.get(i, j) + " "); 		
	         }	
		System.out.println();
		}
	}
	
	//boolean multiplication defination
	// no benifit to use jama matrix library as we need to define it again.
	
	private static Matrix multiply(Matrix a, Matrix b){
		int n=a.getRowDimension();
		Matrix c=new Matrix(n,n);
		
		 for ( int i = 0 ; i < n ; i++ ){
	         for (int j = 0 ; j < n ; j++ ){
	        	 int sum=0;
	        	 for(int k=0; k<n;k++ ){
	        		if(a.get(k, j)==1 && b.get(i, k)==1) 
	        			sum=1;
	        	 }
	        	 c.set(i, j, sum);
	        }
		 }
		
		return c ;
	}
	
}
