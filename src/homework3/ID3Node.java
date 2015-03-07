package homework3;

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
	public double ComputeEntropy(){
		//get distinct values for the column
		
		double entropy = 0.0;		
		if ( parent != null){
			
		}
		else{
			//since parent is null, we will take the entire 
			//feature is the collumn or the attribute name 
			for ( String feature : features){
				AttributeSummary currentColSummary = rl.attributeSummaries.get(feature);
				HashMap<String, Integer> distinctCount = currentColSummary.GetDistinctValues(); 
				System.out.println("\n############\nFor column : " + feature);
				int total =currentColSummary.GetColumnCount(); 
				for ( String dKey : distinctCount.keySet()){
					//dkey is the distinct value for the column
					System.out.println("P( " + dKey + ") =  "  + distinctCount.get(dKey) + "/" + total);
				}
				
			}
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
