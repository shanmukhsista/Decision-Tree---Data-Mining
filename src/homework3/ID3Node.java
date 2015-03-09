package homework3;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
public class ID3Node {
	/*
	 * An attribute node will contain value and a list of subsequent 
	 * attributes. While constructing the classification tree, 
	 * assign the parent to the children. 
	 */ 
	ID3Node parent ;  
	String label ; 
	List<String> features ;
	//nodeFeature is the attribute name for the node inside the circle. 
	String nodeFeature ; 
	List<Edge> edges ; 
	RecordList rl ; 
	//features will contain column 
	public ID3Node(String label, RecordList r, List<String> features) {
		this.rl = r ;  
		this.label = label ; 
		this.features = features;
		this.edges = new ArrayList<Edge>(); 
	}
	public double log2(double value){
		return ( Math.log(value)/ Math.log(2)); 
	}
	
	public double ComputeEntropyForSet(){
		/*
		 * compute the entropy for the entire set. 
		 * this method will internally call
		 */
		//get distinct values for the column
		//get the distinct values for the classifier labels 
		HashMap<String, Integer> valueCntMap = new HashMap<String, Integer>(); 
		//Get column index for label . 
		int labelIndex = rl.attributeSummaries.get(label).getColumnIndex();
		HashMap<Integer, Attribute[]> rows = rl.rows; 
		int totalCount = rl.attributeSummaries.get(label).GetColumnCount();
		//iterate over all the rows to get distinct elements and counts 
		for ( int i : rows.keySet()){
			Attribute[] a = rows.get(i);	
				if( valueCntMap.containsKey(a[labelIndex].getName()) ){
						valueCntMap.put(a[labelIndex].getName(), valueCntMap.get(a[labelIndex].getName()) + 1 ); 
				}
				else{
					valueCntMap.put(a[labelIndex].getName(), 1 ); 
			}
		}		
		//System.out.println(valueCntMap.toString());
		
		double sum = 0 ; 
		for ( String l : valueCntMap.keySet()){ 
			//System.out.print("-1 * p(" + l + ") * log(pi)  + ");
			double pl = valueCntMap.get(l)*1.0/totalCount; 
			double logpl = log2(pl);
			sum = sum + (-1) * pl * logpl; 
			//System.out.println("probability for  " + l + " Is " + pl );
		}
		
		//System.out.println("");
		//System.out.println("Entropy for set is  "  + sum );
		
		
		return sum;
	}
	//feature is the column name
	public double ComputeGainForFeature(String feature){
		/*
		 * Calculates and returns the gain for one feature that can be a child. 
		 * 
		 */
		double gain= ComputeEntropyForSet(); 
		//Gain is equal to the entropy of the set - fractional entropies sum
		//compute entropies for each feature. 
		int labelIndex = rl.attributeSummaries.get(label).getColumnIndex();
		HashMap<Integer, Attribute[]> rows = rl.rows; 
		AttributeSummary featureSummary = rl.attributeSummaries.get(feature); 
		HashMap<String, Integer> distinctElements = featureSummary.GetDistinctValues(); 
		for ( String item : distinctElements.keySet()){
			//For each feature print the count ratio
			double factor = (1.0 * distinctElements.get(item))/ featureSummary.totalCount; 
			//System.out.println("Factor is" + factor);
			gain = gain - (factor * ComputeEntropyForValueSet(feature, item, rl));
		}
		//System.out.println("Gain for " + feature + " is " + gain);
		return gain; 
	}
	public double ComputeEntropyForValueSet(String column, String value, RecordList subList){
		//get distinct values for the column
		//get the distinct values for the classifier labels 
		HashMap<String, Integer> valueCntMap = new HashMap<String, Integer>(); 
		//Get column index for label . 
		int labelIndex = subList.attributeSummaries.get(label).getColumnIndex();
		int columnIndex = subList.attributeSummaries.get(column).getColumnIndex();
		HashMap<Integer, Attribute[]> rows = subList.rows; 
		int totalCount = subList.attributeSummaries.get(column).GetValueCountForColumn(value);
		//iterate over all the rows to get distinct elements and counts 
		for ( int i : rows.keySet()){
			Attribute[] a = rows.get(i);	
				if( valueCntMap.containsKey(a[labelIndex].getName()) ){
					if ( a[columnIndex].getName().equals(value) ){
						valueCntMap.put(a[labelIndex].getName(), valueCntMap.get(a[labelIndex].getName()) + 1 ); 
					}
				}
				else{
					if ( a[columnIndex].getName().equals(value) ){
						valueCntMap.put(a[labelIndex].getName(), 1 ); 
					}
					else{
						//valueCntMap.put(a[labelIndex].getName(), 0 ); 
				}
			}
		}
		//System.out.println("Entropy for " + value + "  is : " );
		//Now compute entropy for the data
		double sum = 0 ; 
		for ( String l : valueCntMap.keySet()){ 
			//System.out.print("-1 * p(" + value +"|" + l + ") * log(pi)  + ");
			double pl = valueCntMap.get(l)*1.0/totalCount; 
			double logpl = log2(pl);
			sum = sum + (-1) * pl * logpl; 
			//System.out.println("probability for  " + l + " Is " + pl );
		}
		return sum; 
	}
	public String GetFeatureForLocalRoot(){
		String feature = ""; 
		double max = 0.0; 
		double gain; 
		//for each feature set compute gain and select object with max gain
		int c = 0; 
		System.out.println("selecting maximum gain among attributes : " + features.toString());
		for ( String f : features){
			gain = ComputeGainForFeature(f);
			if ( c== 0){
				max = gain; 
				feature = f ; 
			}
			if ( gain > max){
				max = gain ; 
				feature = f ; 
			}
			c++; 
			//System.out.println("gain for " + f + "is " + gain);
		}
		//SElect max gain from priority queue
		System.out.println("Max gain is for " + feature + " value : " + max); 
		features.remove(feature);
		return feature;
		
	}
	public boolean BuildRecursiveTree(String label, String k){
		System.out.println("\n###########\nrecursion");
		System.out.println("Working on the dataset : ");
		this.rl.PrintRecords();
		System.out.println("Select Among features : " + features.toString());
		System.out.println("-----------");
		if ( rl.rows.size() == 0){
			System.out.println("Empty Training set . ");
			return false; 
		}
		if (ComputeEntropyForSet() == 0){
			System.out.println("\n**********\nentropy is zero. Assigning labels to the parent. ");
			rl.PrintRecords();
			//get the label value for this.
			//if entropy is zero then this is a leave node i.e. an edge with labels. 
			//add this edge to the parent node.
			int labelIndex = rl.attributeSummaries.get(label).getColumnIndex();
			if ( this.rl.rows.size() > 0){
				if ( this.parent != null){
					Edge e = new Edge(); 
					e.value = k; 
					this.parent.edges.add(e);
					e.labelCount.put(this.rl.rows.get(this.rl.GetLastAddedRowIndex())[labelIndex].getName(), this.rl.rows.size() );
				}
				else{
					//this.resultCount.put(this.rl.rows.get(this.rl.GetLastAddedRowIndex())[labelIndex].getName(), this.rl.rows.size() );
				}
				
			}
			if ( this.parent != null){
				System.out.println("edge parent : " + this.parent.nodeFeature);
				for ( Edge e : this.parent.edges){
			
					System.out.println("Edge : " + e.value + " value : " + e.labelCount.toString()); 
				}
				
			}
			
			return false; 
		}
		if ( features.isEmpty()){
			System.out.println("feature list is empty");
			return false; 
		}
		else{
			//			
			String f = GetFeatureForLocalRoot(); 			
			if ( parent != null){
				System.out.println("Parent Feature : "+ this.parent.nodeFeature);
			}
			
			System.out.println("Feature Selected is : " + f);
			this.nodeFeature = f ; 
			//get all values for the feature. 
			HashMap<String, Integer> fValues = rl.attributeSummaries.get(f).GetDistinctValues();
			System.out.println("Value for the features " + fValues.toString());
			for ( String key : fValues.keySet()){
				//key variable holds all the values for the column. 
				//Generate a subset for these values and compute the roots. 
				System.out.println("generataing subset for edge " + key);
				RecordList nrl = GenerateSubSet(this.rl, f, key);
				ID3Node root = new ID3Node(label, nrl, features); 
				root.parent  = this;
				root.BuildRecursiveTree(label,key);				
				root = null; 
				Runtime runtime = Runtime.getRuntime();
				runtime.gc();
			}
			return false;
		}
		
	}
	public RecordList GenerateSubSet( RecordList rl, String column, String value ){
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
		//child.PrintRecords();
		//child.PrintAttributeSummaries();
		return child;
	}
	
	public void PrintNode(){
		System.out.println("%%%%%%%%%%%%--Node Print--%%%%%%%%%%%%%%");
		System.out.println("Node Attribute : " + this.nodeFeature);
		
		System.out.println("%%%%%%%%%%--End Node Print--%%%%%%%%%%%%%");
	}
}
