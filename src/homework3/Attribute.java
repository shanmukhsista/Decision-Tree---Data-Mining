package homework3;

public class Attribute {
	private String name ;
	public Attribute(String n){
		this.name = n ; 
	}
	public Attribute( Attribute c){
		this.name = c.name; 
	}
	public String getName(){
		return this.name; 
	}
}
