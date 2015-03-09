@@ -18,12 +18,9 @@ import java.nio.file.WatchEvent.Kind;
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

@@ -31,7 +28,7 @@ public class ID3tree {
		//Read all lines from the input file -- passed as the program arguments. 
		List<String> lines = Files.readAllLines(Paths.get("tree.txt"));
		String inputFile = lines.get(0);
		String label = lines.get(1);
		String classifier = lines.get(1);
		String[] featuresArray = lines.get(2).split("\t");
		for ( String feature :featuresArray){
			
@@ -57,7 +54,7 @@ public class ID3tree {
//			    s.executeUpdate("create database homework3");
//			    s.executeUpdate("use homework3");
			    //Read all headers from the text file. 
			    BufferedReader br = new BufferedReader(new FileReader(inputFile));
			    BufferedReader br = new BufferedReader(new FileReader("input1.txt"));
			    System.out.println("file size is : " + new File("input1.txt").length() + " bytes");
			    //Read the first line as header	
			    String rLine = br.readLine();
@@ -114,48 +111,22 @@ public class ID3tree {
				}
				//r.PrintAttributeSummaries();
				List<String> features = new ArrayList<String>();				
				for(String f : featuresArray){
					features.add(f);
				}
				features.add("Outlook");
				features.add("Wind");
				features.add("Temperature");
				features.add("Humidity");
				
					
				//ID3Node root = new ID3Node(features, rows, "C", r);
				
				
				//System.out.println(features.toString());
				//ID3Node nroot = new ID3Node("Playball", GenerateSubSet(r, "Outlook", "Overcast"), features); 
				//nroot.GetFeatureForLocalRoot();
				br.close();
				ID3Node root = new ID3Node(label, r, features); 
				root.BuildRecursiveTree(label,"");
				HashMap<String, Integer> attributeOrder ; 
				//Store the attribute order in a seperate hashmap
				attributeOrder = root.GetAttributeOrder();
				System.out.println("Attribute Order is " + attributeOrder.toString());
				//root contains the tree to test. 
				//Now test the tree. 
				//read test.txt
				root.PrintTree();
				String[] attrValues = new String[featuresArray.length]; 
				List<String> str = Arrays.asList(featuresArray);
				HashMap<Integer, String> testMap; 
				 br = new BufferedReader(new FileReader("test.txt"));
				 //for each entry put key value pairs in hashmap and test it in the tree. 
				 //report the results for it
				 		
						while ( (line = br.readLine()) != null){
							//Reading a new line. recreate a hashmap
							
							testMap = new HashMap<Integer, String>();
							StringTokenizer insTk = new StringTokenizer(line,"\t");
							int c = 0 ; 
							while ( insTk.hasMoreTokens()){
								int ind = attributeOrder.get(featuresArray[c]);
								System.out.println("index of attribute " + featuresArray[c] + " : " + ind);
								String token = insTk.nextToken();
								testMap.put(ind, token);		
								System.out.println(featuresArray[c]);
								attrValues[ind] = featuresArray[c];
								//testMap.put(featuresArray[c], token);
								c++; 
							}
							System.out.println(testMap.toString());
							root.TestDecisionTree(testMap, attrValues);
				 }
				 
				System.out.println("DOne; " + root);
				ID3Node root = new ID3Node("Playball", r, features); 
				root.BuildRecursiveTree("Playball","");
			 

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block