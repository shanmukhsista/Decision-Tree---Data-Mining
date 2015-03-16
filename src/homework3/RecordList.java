package homework3;
import java.util.ArrayList;
import java.util.Arrays;
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
	public RecordList( RecordList copy){
		//copy constructor 
		lastAddedColumnIndex = copy.lastAddedColumnIndex; 
		lastAddedRowIndex  = copy.lastAddedRowIndex; 
		columns = new ArrayList<String>(copy.columns); 
		rows = new HashMap<Integer, Attribute[]>(copy.rows); 
		attributeSummaries = new HashMap<String, AttributeSummary>(copy.attributeSummaries);
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
			//this.PrintRecord(rows.get(lastAddedRowIndex), lastAddedRowIndex );
		}
	}
	public int GetLastAddedRowIndex(){
		return lastAddedRowIndex;
	}
	public void AddRecordToList(Attribute[] record, int index){
		if ( record != null){
			//Update the attribute summaries. 
			rows.put(index, record);
			UpdateAttributeSummaries(record);
			lastAddedRowIndex = index; 
			//this.PrintRecord(rows.get(index), index );
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
	public void PrintAttributeLabelSumamry(String label){
		System.out.println(this.attributeSummaries.get(label).GetDistinctValues().toString()); 
	}
	public HashMap<String, Integer> GetAttributeLabelSummary(String label ){
		return this.attributeSummaries.get(label).GetDistinctValues(); 
	}
	public List<Integer> FilterRecordsByAttributeValue(String colName , String value){
		List<Integer> records = new ArrayList<Integer>(); 		
		//Get the column index for given column name and 
		//Iterate through all the records.
		int i = attributeSummaries.get(colName).getColumnIndex();
		for ( int index : rows.keySet()){
			if ((rows.get(index)[i].getName()).equals(value)){
				records.add(index);
				//System.out.println("row " + (index+1) );
			}
		}	
		return records ; 
	}
	public void PrintRecords(){
		for ( int i : rows.keySet()){
			PrintRecord(rows.get(i), i); 
		}
	}
	public RecordList GetSubList(int start, int count){
		//Generates a sublist with id's from start to the number 
		//of elements specified.
		//if start + count  > size , then we get records from start. 
		
			RecordList child = new RecordList();
			// Add columns to child
			for (String col : this.columns) {
				child.AddColumn(col);
			}
			// now for each feature filter add the records
			int lcount = 0 ; 
			while ( lcount < count){
				
				if ( start > this.rows.size() - 1){
					start = 0 ; 
				}
				//Get that recrod and add to list. 
				//
				//
				Attribute[] parentRecord = this.rows.get(start);
				Attribute[] newRecord = child.InitRow();
				for (int j = 0; j < parentRecord.length; j++) {
					Attribute newAttr = new Attribute(parentRecord[j]);
					newRecord[j] = newAttr;
				}
				child.AddRecordToList(newRecord, start);
				start++; 
				lcount++; 
			}
			// child.PrintRecords();
			// child.PrintAttributeSummaries();
			return child;
	}
	public RecordList AppendList(RecordList r1  ) {
		
		for ( int k : r1.rows.keySet()){
			Attribute[] parentRecord = r1.rows.get(k);
					Attribute[] newRecord = this.InitRow();
					for (int j = 0; j < parentRecord.length; j++) {
						Attribute newAttr= new Attribute(parentRecord[j]);
						newRecord[j] =newAttr;
					}
					this.AddRecordToList(newRecord);
		}
		return this;
	}
	public List<String[]> GetRowsForRoot(int start, int count){
		List<String[]> returnRows  =new ArrayList<String[]>(); 
		int lcount = 0 ; 
		while ( lcount < count){
			
			if ( start > this.rows.size() - 1){
				start = 0 ; 
			}
			//Get that recrod and add to list. 
			//
			//
			Attribute[] parentRecord = this.rows.get(start);
			String[] row = new String[parentRecord.length];
			for (int j = 0; j < parentRecord.length; j++) {
				row[j] = parentRecord[j].getName();
			}
			returnRows.add(row);
			start++; 
			lcount++; 
		}
		// child.PrintRecords();
		// child.PrintAttributeSummaries();
		return returnRows;
	}
	public List<String[]> GetAllRowsForList(){
		List<String[]> returnRows  =new ArrayList<String[]>(); 
		int lcount = 0 ; 
		//add headers for this rows as well . 
		for ( int k : rows.keySet()){

			Attribute[] parentRecord = this.rows.get(k);
			String[] row = new String[parentRecord.length];
			for (int j = 0; j < parentRecord.length; j++) {
				row[j] = parentRecord[j].getName();
			}
			returnRows.add(row);
		}
		// child.PrintRecords();
		// child.PrintAttributeSummaries();
		return returnRows;
	}
}
