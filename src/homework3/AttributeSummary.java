package homework3;

import java.util.HashMap;

public class AttributeSummary {
	private String aName;
	int index = -1 ; 
	int totalCount; 
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
		totalCount++; 
	}
	public int getColumnIndex(){
		return this.index;
	}
	public HashMap<String, Integer> GetElementCounts(){
		return this.elementCount;
	}
	public int GetValueCountForColumn( String value){
		int c = -1 ;
		c = elementCount.get(value);
		return c ; 
	}
	public HashMap<String, Integer> GetDistinctValues(){
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
	public int GetColumnCount(){
		return this.totalCount; 
	}
}
