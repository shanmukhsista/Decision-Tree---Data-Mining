package homework3;

import java.util.ArrayList;
import java.util.List;

public class AttributeNode {
	/*
	 * An attribute node will contain value and a list of subsequent 
	 * attributes. While constructing the classification tree, 
	 * assign the parent to the children. 
	 */
	Attribute atr ; 
	AttributeNode parent ; 
	List<AttributeNode> children ; 
	public AttributeNode() {
		children = new ArrayList<AttributeNode>();
		//atr = new Attribute() ; 
	}
}
