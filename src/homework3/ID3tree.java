package homework3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class ID3tree {

	public static void main(String[] args) throws IOException {
		//Read all lines from the input file -- passed as the program arguments. 
		List<String> lines = Files.readAllLines(Paths.get("tree.txt"));
		String inputFile = lines.get(0);
		String label = lines.get(1);
		boolean threefoldtest = true; 
		String[] featuresArray = lines.get(2).split("\t");
		for ( String feature :featuresArray){
			
		}
		// TODO Auto-generated method stub
		//Read input form the the file. 
		Connection conn = null; 
		Statement s = null; 
		PreparedStatement ps = null; 
		ResultSet rs = null; 
		//Load jdbc driver	
		
		
		try {
			List<RecordList> trainingList = new ArrayList<RecordList>(); 
			Class.forName("com.mysql.jdbc.Driver").newInstance();

//				conn = DriverManager
//				         .getConnection("jdbc:mysql://localhost/?user=root&password=");
//
//				s = conn.createStatement();
//			    s.executeUpdate("drop database if exists homework3");
//			    s.executeUpdate("create database homework3");
//			    s.executeUpdate("use homework3");
			    //Read all headers from the text file. 
			    BufferedReader br = new BufferedReader(new FileReader(inputFile));
			    //Read the first line as header	
			    String rLine = br.readLine();
			    StringTokenizer tk = new StringTokenizer(rLine,"\t");
			    StringBuilder createQuery =new StringBuilder();
			    createQuery.append("CREATE TABLE data ( ");
				RecordList r = new RecordList();
				List<String> cols = new ArrayList<String>(); 
			    while ( tk.hasMoreTokens())
			    {
			    	String token = tk.nextToken();
			    	createQuery.append(token);
			    	r.AddColumn(token);
			    	cols.add(token); 
			    	if ( tk.hasMoreTokens()){
			    		createQuery.append(" varchar(200), ");
			    	}
			    	else{
			    		createQuery.append(" varchar(140) )"); 
			    	}
			    }
			    
			    //rs = s.executeQuery("select occupation, count(occupation) as countoccupation from users group by occupation"); 
				//while ( rs.next()){
//				s.executeUpdate(createQuery.toString());
				//Read the remaing lines of the file. 
			    
				String line = null;
				while ( (line = br.readLine()) != null){
					StringTokenizer insTk = new StringTokenizer(line,"\t");
					StringBuilder insertQuery = new StringBuilder();
					insertQuery.append("INSERT INTO data values ( "); 
					
					//Create a new record for each line read
					Attribute[] newRecord = r.InitRow();
					int c = 0 ; 
					while ( insTk.hasMoreTokens()){
						
						String token = insTk.nextToken();
						newRecord[c++] = new Attribute(token); 
						//Create a new Attribute and insert it into a record. 
					
						insertQuery.append("'");
						insertQuery.append(token);
						if ( insTk.hasMoreTokens()){
							insertQuery.append("',");
							
				    	}
				    	else{
				    		insertQuery.append("')");
				    	}
					}
					//Add this row to the recordlist
					r.AddRecordToList(newRecord);
					
//					s.executeUpdate(insertQuery.toString());
				}
				//r.PrintAttributeSummaries();
				//Get a list of rows as strings 				
				List<String> features = new ArrayList<String>();				
				for(String f : featuresArray){
					features.add(f);
				}
				List<String> fCopy = new ArrayList<String>(features); 

				br.close();
				ID3Node root = new ID3Node(label, r, fCopy);
				List<String> fs = new ArrayList<String>(features);
				root.BuildRecursiveTree(label,"",fs);
				HashMap<String, Integer> attributeOrder ; 
				//Store the attribute order in a seperate hashmap
				//attributeOrder = root.GetAttributeOrder();
				//System.out.println("Attribute Order is " + attributeOrder.toString());
				//root contains the tree to test. 
				//Now test the tree. 
				//read test.txt
				root.PrintTree();
				String[] attrValues = new String[featuresArray.length]; 
				List<String> str = Arrays.asList(featuresArray);
				HashMap<String, String> testMap; 
				 br = new BufferedReader(new FileReader("test.txt"));
				 //for each entry put key value pairs in hashmap and test it in the tree. 
				 //report the results for it	 		
				 HashMap<String, Double> resMap ; 
						while ( (line = br.readLine()) != null){
							//Reading a new line. recreate a hashmap
							testMap = new HashMap<String, String>();
							StringTokenizer insTk = new StringTokenizer(line,"\t");
							int c = 0 ; 
							while ( insTk.hasMoreTokens()){
								String token = insTk.nextToken();
									//int ind = attributeOrder.get(featuresArray[c]);
									//Insert the feature and feature value in the hashmap. 				
									testMap.put(featuresArray[c], token);		
									//testMap.put(featuresArray[c], token);
									c++; 								
							}
							//System.out.println(testMap.toString());
							resMap = root.TestDecisionTree(testMap);
							if ( resMap != null){
								System.out.println("Probability Labels : " + resMap.toString());
							}
							//root.TravelRecursively(testMap);
							//Generate test data. 						
						} 
				System.out.println("\nDone testing with test data. Three fold validation by constructing and testing the tree.\n" );
				if ( threefoldtest){
					//Get the sublist for the given data. 
					List<RecordList> recordPairs = GetRecordList(r, cols);
					//Now develop a training set for pairs and test the remaining 
					List<RecordList> copypair = GetRecordList(r, cols);
					List<RecordList> validationRecords = new ArrayList<RecordList>(); 
					int ci = 1 ;
					int split = 3 ;
					int rr = 2 ; 
					ID3Node testNode ;
					List<HashMap<String, Double>> probRes = new ArrayList<HashMap<String,Double>>(); 
					List<HashMap<String, Double>> rootProbTest = new ArrayList<HashMap<String,Double>>(); 
					
					for ( int j = 0  ; j < recordPairs.size(); j++){
						//Combine pairs to get the records
							if ( ci >= 3){							 
								
								 ci = 0 ; 
							}
							if ( rr == 3){
								rr = 0 ; 
							}
							//copypair will contain appended list.
							//take next pair of records from recordPair and append to copypair
								 //System.out.println("Appending and printing records");
								 RecordList source = recordPairs.get(ci);
								 //source.PrintRecords();
								 //System.out.println("Appending to list : ");
								 RecordList dest = copypair.get(j);
								 //dest.PrintRecords();
								 //the source of the third pair will be 						
								 //construct decision tree and validate it here 
								 dest.AppendList(source);
								 
								 //System.out.println("The pair to test is ");	 
								 //System.out.println("Building tree for Treefold Validation  :");
								 //dest.PrintRecords();
								 
								 System.out.println("Testing on the data - Iteration  : " + (j+1));
								 RecordList test =  recordPairs.get(rr);
								 //for each test data, perform test. 
								 test.PrintRecords();
								 List<String[]> rowsToTest = test.GetAllRowsForList();
								 for ( String[] rt : rowsToTest){
									 //perform test for this entry
									 testNode = new ID3Node(label, dest, fCopy);
									 int ccc = 0 ; 
									 testMap = new HashMap<String, String>();
									 String colName ; 
									 for ( String ss  :rt){
										 	//ccc has the column index use this to get the order from 
										 //the test file.
										 	colName = testNode.rl.columns.get(ccc);
										 	int ind = Arrays.asList(featuresArray).indexOf(colName);
										 	//Features array has the test columns in order
										 	//find the row corrosponding to this value
											if ( ind != -1){
												testMap.put(featuresArray[ind], ss);	
											}
										 	
											ccc++;
									 }
									 List<String> fss = new ArrayList<String>(features);
									 testNode.BuildRecursiveTree(label, "" , fss);
									probRes.add(testNode.TestDecisionTree(testMap));
									rootProbTest.add(root.TestDecisionTree(testMap));
									
									
								 }
								 //copypair.get(j).PrintRecords();
								 ci++;	
								 rr++;
								 
					}
					
					//Now compute the % error for each test and report them. 
					int counter = 0 ; 
					HashMap<String, Double> errormap = new HashMap<String, Double>();
					for ( HashMap<String, Double> entry : rootProbTest){
						//Compute error
						/*
						 * Each entry in the list of hashtable corrosponds to 
						 * the test result for the same row on two trees. 
						 * One is the root tree and the other is the 
						 * threefold validation tree. 
						 */
						for ( String k : entry.keySet()){
							//For each key compare the result. 
							
							 
							if ( errormap.containsKey(k)){
								double value = errormap.get(k);
								errormap.put(k, value + Math.abs(entry.get(k) - probRes.get(counter).get(k)));
							}
							else{
								errormap.put(k, Math.abs(entry.get(k) - probRes.get(counter).get(k)));
							}
						
						}
						counter++;
					}
					System.out.println("\nThree Fold validation Tree Probabilities result for each test");
					System.out.println(probRes.toString());
					System.out.println("\nOriginal Decision Tree Probabilities for test data in three fold validation.");
					System.out.println(rootProbTest.toString());
					System.out.println("\nAverage difference in the error for the given data labels is : ");
					System.out.println(errormap.toString());
				}
			
				System.exit(0);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	public static List<RecordList> GetRecordList(RecordList r, List<String> cols){
		int totalCount = r.lastAddedRowIndex + 1 ;
		//System.out.println("Total Records = "+ totalCount);
		//Split it in 3 parts. 
		int split = totalCount/3 ; 
		int nCount = 3 ; 
		int processed = 0 ; 
		int cc= 0 ;
		List<RecordList> recordPairs = new ArrayList<RecordList>();
		
		for ( int i = 0 ; i < split ; i++ ){
			List<String[]> rl ; 
			//System.out.println("Starting at " + i);
			if ( i == split - 1){
				//this is the last record
				nCount = totalCount - processed;

			}
			rl = r.GetRowsForRoot(processed, nCount);
			//rl contains the rows for all the columns
			//loop
			RecordList nrl = new RecordList();
			for ( String col : cols){
				nrl.AddColumn(col);
			}

			for ( String[] srow : rl){
				
				Attribute[] newRecord = r.InitRow();
				for ( int kk =  0 ; kk < srow.length ; kk++ ){
					//for each column add a new element to the
					//newly created row.
					newRecord[kk] = new Attribute(srow[kk]); 
					
				}
				nrl.AddRecordToList(newRecord, cc);
				cc++;
			}	
			processed = processed+ nCount ;					
			recordPairs.add(nrl); 
		}
		return recordPairs ; 
	}
	public static RecordList GenerateSubSet( RecordList rl, String column, String value ){
		RecordList child = new RecordList();
		//Add columns to child
		for ( String col : rl.columns){
			child.AddColumn(col);			
		}
		//now for each feature filter add the records
		List<Integer> fl = rl.FilterRecordsByAttributeValue(column, value);
		for ( int i : fl){
			Attribute[] newRecord = child.InitRow();
			Attribute[] parentRecord = rl.rows.get(i) ; 
			for (int j = 0 ; j < parentRecord.length ; j++){
				newRecord[j] = parentRecord[j];
			}
			child.AddRecordToList(newRecord,i);
		}
		//child.PrintAttributeSummaries();
		return child;
	}

}
