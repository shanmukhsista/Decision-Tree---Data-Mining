package homework3;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class ID3Node {
	/*
	 * An attribute node will contain value and a list of subsequent 
	 * attributes. While constructing the classification tree, 
	 * assign the parent to the children. 
	 */ 
	ID3Node parent ;  
	String label ; 
	List<String> features ;
	List<ID3Node> children;
	RecordList rl ; 
	double entropy ;
	//features will contain column 
	public ID3Node(String label, RecordList r, List<String> features) {
		this.rl = r ;  
		this.label = label ; 
		this.features = features; 
	}
	public double log2(double value){
		return ( Math.log(value)/ Math.log(2)); 
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
		System.out.println("printing sublist ");
		subList.PrintRecords();
		System.out.println("Entropy is : " );
		//Now compute entropy for the data
		double sum = 0 ; 
		for ( String l : valueCntMap.keySet()){ 
			System.out.print("-1 * p(" + value +"|" + l + ") * log(pi)  + ");
			double pl = valueCntMap.get(l)*1.0/totalCount; 
			double logpl = log2(pl);
			sum = sum + (-1) * pl * logpl; 
			//System.out.println("probability for  " + l + " Is " + pl );
		}
		
		System.out.println("");
		System.out.println("Entropy is for attribue value  " + value + " is "  + sum );
		//Print label count 
		System.out.println(valueCntMap.toString()); 
		AttributeSummary currentColSummary = subList.attributeSummaries.get(column);
		currentColSummary.PrintSummary();
		//for each distinct value get the count of label values 
		double entropy = 0.0;		
		if ( parent != null){
			
		}
		else{
			
			
			
			//since parent is null, we will take the entire 
			//feature is the collumn or the attribute name 
//			for ( String feature : features){
//				AttributeSummary currentColSummary = rl.attributeSummaries.get(feature);
//				HashMap<String, Integer> distinctCount = currentColSummary.GetDistinctValues(); 
//				System.out.println("\n############\nFor column : " + feature);
//				int total =currentColSummary.GetColumnCount(); 
//				for ( String dKey : distinctCount.keySet()){
//					//dkey is the distinct value for the column
//					System.out.println("P(" + dKey + ") =  "  + distinctCount.get(dKey) + "/" + total);
//				}
//				
//			}
		}
		return entropy; 
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
		//child.PrintAttributeSummaries();
		return child;
	}
}
