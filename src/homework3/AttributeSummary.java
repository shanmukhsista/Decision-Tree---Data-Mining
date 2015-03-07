package homework3;

import java.util.HashMap;

public class AttributeSummary {
	private String aName;
	int index = -1 ; 
	private HashMap<String, Integer> elementCount; 
	public AttributeSummary(String a, int index){
		this.aName = a ;
		this.index = index;		 
		elementCount = new HashMap<String, Integer>();
	}
	public String GetAttributeName(){
		return aName ;
	}
	public void AddAttributeValue(String value){
		//Adds the given attribute to the hashset and increments the count of attributes. 
		if( elementCount.containsKey(value)){
			//Increament count  by 1 
			elementCount.put(value, elementCount.get(value) + 1 );
		}
		else{
			elementCount.put(value, 1 );
		}
	}
	public int getColumnIndex(){
		return this.index;
	}
	public HashMap<String, Integer> GetElementCounts(){
		return this.elementCount;
	}
	public void PrintSummary(){
		System.out.println("*********************");
		System.out.println("For Attribute => " + aName);
		for ( String s : elementCount.keySet()){
			System.out.println("ELemenet :  " + s + " Count -> " + elementCount.get(s));			
		}
		System.out.println("");
	}
}
