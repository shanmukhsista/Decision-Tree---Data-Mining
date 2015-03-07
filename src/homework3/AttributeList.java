package homework3;

import java.util.HashMap;
public class AttributeList {
	String name ;
	HashMap<String, Integer> attributes ;
	public AttributeList(String n ){	
		this.name = n ; 
		attributes = new HashMap<String, Integer>();
		attributes.put(n, 0);
	}
	
}
