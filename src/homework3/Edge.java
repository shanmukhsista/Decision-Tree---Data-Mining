package homework3;

import java.util.HashMap;

public class Edge {
	public String value ; 
	public HashMap<String, Integer> labelCount;
	public Edge(){
	  labelCount = new HashMap<String, Integer>();
	}
	public String getValue(){
		return this.value; 
	}
	
}
