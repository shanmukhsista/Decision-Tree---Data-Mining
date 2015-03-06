package homework3;
import java.util.HashMap;

public class Record {
	HashMap<String, AttributeValue> attributes ;
	public Record(){
		attributes = new HashMap<String, AttributeValue>(); 
	}
	public void addAttribute( AttributeValue a) {
		if ( a !=  null){
			attributes.put(a.name, a);
		}
	}
}
