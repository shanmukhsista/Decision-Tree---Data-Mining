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
	List<ID3Node> children;
	RecordList rl ; 
	double entropy ;
	//features will contain column 
	public ID3Node(String label, RecordList r) {
		this.rl = r ;  
		this.label = label ; 
	}
	public double ComputeEntropy(String feature){
		double entropy = 0.0;		
		
		return entropy; 
	}
}
