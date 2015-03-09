package homework3;

import java.util.HashMap;

public class Child {
	public String edgeValue ; 
	public HashMap<String, Integer> labelCount;
	public ID3Node childNode; 
	public Child(){
	  labelCount = new HashMap<String, Integer>();
	}
	public String getValue(){
		return this.edgeValue; 
	}
	
}
