package homework3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RecordList {
	//This is the hashmap for storing the attribute name and 
	//List of values for the columns
	int lastAddedColumnIndex = -1 ;
	int lastAddedRowIndex = -1;

	List<String> columns =  new ArrayList<String>();
	HashMap<Integer, Attribute[]> rows ;
	HashMap<String, AttributeSummary> attributeSummaries;
	public RecordList(){
		//attributes = new HashMap<Integer, AttributeValue>(); 
		rows = new HashMap<Integer, Attribute[]>(); 
		attributeSummaries = new HashMap<String, AttributeSummary>();
	}
	public Attribute[] InitRow(){
		//initialize a new row with the size and add it to the rowlist
		return new Attribute[lastAddedColumnIndex + 1];
	}
	public int GetColumnCount(){ 
		return columns.size();
	}
	public void AddColumn(String cName){
		//Add this column to the record list
		columns.add(cName);
		++lastAddedColumnIndex;
		//whenever a new column is added we initialize attribute summary object and 
		//insert it into the hashmap
		attributeSummaries.put(cName, new AttributeSummary(cName, lastAddedColumnIndex));
	}
	public void AddRecordToList(Attribute[] record){
		if ( record != null){
			//Update the attribute summaries. 
			rows.put(++lastAddedRowIndex, record);
			UpdateAttributeSummaries(record);
			this.PrintRecord(rows.get(lastAddedRowIndex), lastAddedRowIndex );
		}
	}
	public void AddRecordToList(Attribute[] record, int index){
		if ( record != null){
			//Update the attribute summaries. 
			rows.put(index, record);
			UpdateAttributeSummaries(record);
			this.PrintRecord(rows.get(index), index );
		}
	}
	private void UpdateAttributeSummaries(Attribute[] record){
		for( int i = 0 ; i < record.length ; i++){
			AttributeSummary s = attributeSummaries.get(columns.get(i));
			s.AddAttributeValue(record[i].getName());
		}
	}
	public void PrintRecord(Attribute[] record, int rowIndex){
		System.out.print("Row index : " + rowIndex + " => "); 
		for ( int i = 0 ; i < record.length ; i++){
			 System.out.print(record[i].getName() + "\t");
		}
		System.out.print("\n");
	}
	public void PrintAttributeSummaries(){
		for ( String aName : attributeSummaries.keySet()){
			AttributeSummary s = attributeSummaries.get(aName);
			s.PrintSummary();
		}
	}
	public List<Integer> FilterRecordsByAttributeValue(String colName , String value){
		System.out.println("find " + value);
		List<Integer> records = new ArrayList<Integer>(); 		
		//Get the column index for given column name and 
		//Iterate through all the records.
		int i = attributeSummaries.get(colName).getColumnIndex();
		for ( int index : rows.keySet()){
			if ((rows.get(index)[i].getName()).equals(value)){
				records.add(index);
				System.out.println("row " + (index+1) );
			}
		}	
		return records ; 
	}
	public void PrintRecords(){
		for ( int i : rows.keySet()){
			PrintRecord(rows.get(i), i); 
		}
	}
	
}
