/*COSC 471/571:  Assignment-1
 * Implement system catalogue
 * Author: Mahmood uz Zaman
 * ID: E01407909 */

package dbms.emich;

import java.io.*;
import java.util.*;

public class SysCatalogue {
	
/*This program will act on some of sql commands and is kind of tiny tiny implementation of mysql,
 * will create respective system catalogues files. For example: sys_dbname.tab contain all the information 
 * about the tables and sys_dbname.att will contain infortaion about the fields of table */
	
	public static void main(String[] args) {
		
		//list of valid sql Commands to implement
		List <String> commands= new ArrayList();
		commands.add("create database dbName");
		commands.add("drop database dbName");
		commands.add("Print catalogue dbName");
		commands.add("quit");
		
		//take input from user
		InputStreamReader read = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(read);
		String command ="";
		
		while(!command.equalsIgnoreCase("quit")){
        System.out.println("Enter Command >");
        	try {
					command = in.readLine();
					
					//to check if command is valid
					String lCommand=command.toLowerCase();
					if(lCommand.equalsIgnoreCase("quit")){
						break;
					}
					if(lCommand.contains("create database")||lCommand.contains("drop database")
							||lCommand.contains("print catalogue"))
					{
						//call action method
						myaction(lCommand);	
					}
					else{
						System.out.println("Invalid input,try again: ");
						//for(object obj inn commands){
						//System.out.println(obj);
						//}
					}
				} catch (Exception e) {
			
					e.getMessage();
				}
        	
        	System.out.println(command);
        	
		}
		System.out.println("you are safely out from program !");		
	}
	public static void myaction(String comm){
		
		CreateCatalogue mycatalogue= new CreateCatalogue();
		/*action to the commands
    	1-if commad is create db */
    	if(comm.contains("create database")){
    		/*assuming that if we have database name just after create database 
    		 *as we already filtered three commands*/
    		String dbname=comm.substring(16); //location hard coded(bad idea);
    		mycatalogue.createdb(dbname);
    	}
    	
    	//2-if command is drop db
    	if(comm.contains("drop database")){
    		//14
    		String dbname=comm.substring(14);
    		mycatalogue.dropdb(dbname);
    	}
    	
    	//3-if its print catalogue
    	if(comm.contains("print catalogue")){
    		//16
    		String dbname=comm.substring(16);
    		mycatalogue.printcat(dbname);
    	}
	}
}
class CreateCatalogue{
	
	public void createdb(String database){
		
		String tabName="sys_"+database;
		File myfile=new File(tabName +".tab");
		File myfile2=new File(tabName+".att");
		//System.out.println(myfile.getAbsolutePath());
			try {
				if(myfile.exists()||myfile2.exists()){
					System.out.println("Data base with same name already exist");
				} else
				myfile.createNewFile();
				myfile2.createNewFile();
			} catch (IOException e) {
				
				e.getMessage();
			}
			try {
				FileWriter fwTab=new FileWriter(myfile);
				FileWriter fwAtt=new FileWriter(myfile2);
				BufferedWriter buffTab = new BufferedWriter(fwTab);
				BufferedWriter buffAtt = new BufferedWriter(fwAtt);
				writeCatloguTab(buffTab,tabName );
				writeCatloguAtt(buffAtt,tabName);
			} catch (IOException e) {
				
				e.getMessage();
			}		
	}
	private void writeCatloguTab(BufferedWriter w, String table){
		try {
			w.write("Name \t\t Location \t Type \n");
			w.write(table + ".tab \t" + table + ".tab \t"+ "21 \n");
			w.write(table + ".att \t" + table + ".att \t"+ "21 \n");
			w.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void writeCatloguAtt(BufferedWriter w, String table){
		int tnameSize=16;
		int intType=1;
		int charType=2;
		/*an object containing the table attribute information
		*will be passed later instead of string table to avoid hardcoding*/
		try {
			w.write("Name \t\t TName \t  OffSet \t Type \t Length \n");
			//db.tab table attributes always same
			w.write("Name \t sys_"+table+".tab \t 0 \t 2 \t 16 \n" );
			w.write("Location sys_"+table+".tab \t 16 \t 2 \t 64 \n");
			w.write("Type  \t sys_"+table+".tab \t 80 \t 1 \t 4 \n");
			//write attribute for db.att table(its own)
			w.write("Name \t sys_"+table+".att \t 0 \t 2\t 16\n");
			w.write("TName \t sys_"+table+".att \t 16 \t "+ charType +"\t 16\n");
			w.write("Offset \t sys_"+table+".att \t 32 \t "+ intType +"\t 4\n");
			w.write("Type \t sys_"+table+".att \t 36 \t "+ intType +"\t 4\n");
			w.write("Type \t sys_"+table+".att \t 40 \t "+ intType +"\t 4\n");
			
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void dropdb(String database){
		
		try {
			File file = new File("sys_"+database+".att");
			File file2 = new File("sys_"+database+".tab");
			
			file.setWritable(true);
			file2.setWritable(true);
			
			    if(file.delete() && file2.delete()){
			         System.out.println("Database dropped sucessfully");
			    }
			    else{
			    	System.out.println("not droped");
			    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void printcat(String database){
		
		File f = new File("sys_"+database +".tab");
		if (!f.exists()){
			System.out.println("Database doesn't exist, try again");
		}
		else{
		 printTable("sys_"+database +".tab");
		 printTable("sys_"+database +".att");
		 
		}
	}
	private void printTable(String tableName){
		
		System.out.println("********************************");
		System.out.println(tableName);
		
		//reading the content
		
		BufferedReader rdr = null;
		try {

			String CurrentLine;
			rdr = new BufferedReader(new FileReader(tableName));
			while ((CurrentLine = rdr.readLine()) != null) {
				System.out.println(CurrentLine);
			}
			rdr.close();
			System.out.println();
		} catch (IOException e) {
			e.getMessage();
		} 
	}
} 
