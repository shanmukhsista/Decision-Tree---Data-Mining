package homework3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
class DTreeNode{
	public List<ID3Node> nodes ;
	public DTreeNode(){
		nodes = new ArrayList<ID3Node>(); 
	}
}
public class DTree{
	public DTreeNode lastInserted; 
	private LinkedList<DTreeNode> tree; 
	public ID3Node parent; 
	public DTree(){
		tree = new LinkedList<DTreeNode>(); 
	}
	public void AddNodeToTree(ID3Node node){
//		//check if node feature is null. 
//		if ( node.parent != null){
//			while ( tree.ge)
//		}
//		else{
//			DTreeNode n = new DTreeNode();
//			n.nodes.add(node);
//			lastInserted = n ; 
//		}
	}
	
}
