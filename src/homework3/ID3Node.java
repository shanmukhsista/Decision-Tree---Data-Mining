package homework3;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

public class ID3Node {
	/*
	 * An attribute node will contain value and a list of subsequent attributes.
	 * While constructing the classification tree, assign the parent to the
	 * children.
	 */
	ID3Node parent;
	String label;
	List<String> features;
	// nodeFeature is the attribute name for the node inside the circle.
	String nodeFeature;
	List<ID3Node> children;
	RecordList rl;
	boolean isTreeNode = false;
	boolean isLeaf = false;
	String edgelabel;

	// features will contain column
	public ID3Node(String label, RecordList r, List<String> features) {
		this.rl = r;
		this.label = label;
		this.features = features;
		children = new ArrayList<ID3Node>();
	}

	public double log2(double value) {
		return (Math.log(value) / Math.log(2));
	}

	public HashMap<String, Integer> GetAttributeOrder() {
		// Returns the order of attributes in the tree.
		// test this decision tree for a set of values.
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		ID3Node current = this;
		Queue<ID3Node> q = new LinkedList<ID3Node>();
		q.add(current);
		int ct = 0;
		while (!q.isEmpty()) {
			// Print current
			ID3Node c = q.poll();
			if (c.nodeFeature != null) {
				hm.put(c.nodeFeature, ct);
				ct++;
			}
			for (ID3Node n : c.children) {
				q.add(n);
			}
		}
		return hm;
	}

	public double ComputeEntropyForSet() {
		/*
		 * compute the entropy for the entire set. this method will internally
		 * call
		 */
		// get distinct values for the column
		// get the distinct values for the classifier labels
		HashMap<String, Integer> valueCntMap = new HashMap<String, Integer>();
		// Get column index for label .
		int labelIndex = rl.attributeSummaries.get(label).getColumnIndex();
		HashMap<Integer, Attribute[]> rows = rl.rows;
		int totalCount = rl.attributeSummaries.get(label).GetColumnCount();
		// iterate over all the rows to get distinct elements and counts
		for (int i : rows.keySet()) {
			Attribute[] a = rows.get(i);
			if (valueCntMap.containsKey(a[labelIndex].getName())) {
				valueCntMap.put(a[labelIndex].getName(),
						valueCntMap.get(a[labelIndex].getName()) + 1);
			} else {
				valueCntMap.put(a[labelIndex].getName(), 1);
			}
		}
		// System.out.println(valueCntMap.toString());

		double sum = 0;
		for (String l : valueCntMap.keySet()) {
			// System.out.print("-1 * p(" + l + ") * log(pi)  + ");
			double pl = valueCntMap.get(l) * 1.0 / totalCount;
			double logpl = log2(pl);
			sum = sum + (-1) * pl * logpl;
			// System.out.println("probability for  " + l + " Is " + pl );
		}

		// System.out.println("");
		// System.out.println("Entropy for set is  " + sum );

		return sum;
	}

	// feature is the column name
	public double ComputeGainForFeature(String feature) {
		System.out.println("Computing gain for " + feature);
		/*
		 * Calculates and returns the gain for one feature that can be a child.
		 */
		double gain = ComputeEntropyForSet();
		// Gain is equal to the entropy of the set - fractional entropies sum
		// compute entropies for each feature.
		int labelIndex = rl.attributeSummaries.get(label).getColumnIndex();
		HashMap<Integer, Attribute[]> rows = rl.rows;
		AttributeSummary featureSummary = rl.attributeSummaries.get(feature);
		HashMap<String, Integer> distinctElements = featureSummary
				.GetDistinctValues();
		for (String item : distinctElements.keySet()) {
			// For each feature print the count ratio
			double factor = (1.0 * distinctElements.get(item))
					/ featureSummary.totalCount;
			// System.out.println("Factor is" + factor);
			gain = gain
					- (factor * ComputeEntropyForValueSet(feature, item, rl));
		}
		 System.out.println("Gain for " + feature + " is " + gain);
		return gain;
	}

	public double ComputeEntropyForValueSet(String column, String value,
			RecordList subList) {
		// get distinct values for the column
		// get the distinct values for the classifier labels
		HashMap<String, Integer> valueCntMap = new HashMap<String, Integer>();
		// Get column index for label .
		int labelIndex = subList.attributeSummaries.get(label).getColumnIndex();
		int columnIndex = subList.attributeSummaries.get(column)
				.getColumnIndex();
		HashMap<Integer, Attribute[]> rows = subList.rows;
		int totalCount = subList.attributeSummaries.get(column)
				.GetValueCountForColumn(value);
		// iterate over all the rows to get distinct elements and counts
		for (int i : rows.keySet()) {
			Attribute[] a = rows.get(i);
			if (valueCntMap.containsKey(a[labelIndex].getName())) {
				if (a[columnIndex].getName().equals(value)) {
					valueCntMap.put(a[labelIndex].getName(),
							valueCntMap.get(a[labelIndex].getName()) + 1);
				}
			} else {
				if (a[columnIndex].getName().equals(value)) {
					valueCntMap.put(a[labelIndex].getName(), 1);
				} else {
					// valueCntMap.put(a[labelIndex].getName(), 0 );
				}
			}
		}
		// System.out.println("Entropy for " + value + "  is : " );
		// Now compute entropy for the data
		double sum = 0;
		for (String l : valueCntMap.keySet()) {
			// System.out.print("-1 * p(" + value +"|" + l + ") * log(pi)  + ");
			double pl = valueCntMap.get(l) * 1.0 / totalCount;
			double logpl = log2(pl);
			sum = sum + (-1) * pl * logpl;
			// System.out.println("probability for  " + l + " Is " + pl );
		}
		return sum;
	}

	public String GetFeatureForLocalRoot() {
		String feature = "";
		double max = 0.0;
		double gain;
		// for each feature set compute gain and select object with max gain
		int c = 0;
		System.out.println("selecting maximum gain among attributes : "
				+ features.toString());
		for (String f : features) {
			gain = ComputeGainForFeature(f);
			if (c == 0) {
				max = gain;
				feature = f;
			}
			if (gain > max) {
				max = gain;
				feature = f;
			}
			c++;
			// System.out.println("gain for " + f + "is " + gain);
		}
		// SElect max gain from priority queue
		System.out.println("Max gain is for " + feature + " value : " + max);
		//features.remove(feature);
		return feature;

	}

	public void PrintTree() {
		// test this decision tree for a set of values.
		System.out.println("%%%%%%%%%%%%--Start Printing--%%%%%%%%%%%%%%");
		ID3Node current = this;
		Queue<ID3Node> q = new LinkedList<ID3Node>();
		q.add(current);
		while (!q.isEmpty()) {
			// Print current
			ID3Node c = q.poll();
			if (c.nodeFeature != null) {
				System.out.println("Attribute Node : " + c.nodeFeature);
				if (c.edgelabel != null) {
					System.out
							.println("Edge for this attribute " + c.edgelabel);
				}
			} else if (c.isLeaf) {

				System.out.println("Leaf Node " + c.edgelabel + " Parent : "
						+ c.parent.nodeFeature);
				c.rl.PrintRecords();
			}
			for (ID3Node n : c.children) {

				q.add(n);
			}

		}
		System.out.println("%%%%%%%%%%--End Tree Print--%%%%%%%%%%%%%");
	}

	public void BuildRecursiveTree(String label, String k, List<String> fs) {
		List<String> fCopy = new ArrayList<String>(this.features); 
		if (rl.rows.size() == 0) {
			System.out.println("Empty Training set . ");
			return;
		}
		if (ComputeEntropyForSet() == 0) {
			System.out
					.println("\n**********\nentropy is zero. Assigning labels to the parent. ");
			if (this.parent != null) {
				System.out.println(this.parent.nodeFeature);
			}
			rl.PrintRecords();
			// get the label value for this.
			// if entropy is zero then this is a leave node i.e. an edge with
			// labels.
			// add this edge to the parent node.
			int labelIndex = rl.attributeSummaries.get(label).getColumnIndex();
			this.isLeaf = true;
			System.out.println("leaf node is : " + edgelabel);
			if (this.rl.rows.size() > 0) {
			}
			if (this.parent != null) {
				System.out.println("edge parent : " + this.parent.nodeFeature);

			}
			return;
		}
		if (features.isEmpty()) {
			System.out.println("feature list is empty");
			this.isLeaf = true;
			System.out.println("printing rows for leaf");
			this.rl.PrintRecords();
			return;
		} else {
			
			System.out.println("\n###########\nrecursion");
			System.out.println("Working on the dataset : ");
			this.rl.PrintRecords();
			System.out
					.println("Select Among features : " + features.toString());
			System.out.println("-----------");
			//
			String f = GetFeatureForLocalRoot();
			
			if (parent != null) {
				System.out.println("Parent Feature : "
						+ this.parent.nodeFeature);
			}
			System.out.println("Feature Selected is : " + f);
			this.nodeFeature = f;
			if ( this.parent != null){
				System.out.println("Attribute Node : " + f  + "; Parent is : " + this.parent.nodeFeature);
			}
			this.isTreeNode = true;

			// get all values for the feature.
			HashMap<String, Integer> fValues = rl.attributeSummaries.get(f)
					.GetDistinctValues();
			System.out.println("Value for the features " + fValues.toString());
			fCopy.remove(f);
			for (String key : fValues.keySet()) {
				// key variable holds all the values for the column.
				// Generate a subset for these values and compute the roots.
				int labelIndex = rl.attributeSummaries.get(label)
						.getColumnIndex();
				System.out.println("child  edge " + key + " for parent : " + this.nodeFeature + "; features " + this.features.toString());
				RecordList nrl = GenerateSubSet(this.rl, f, key);
				
				List<String> fSubsetCopy = new ArrayList<String>(fCopy);
				ID3Node root = new ID3Node(label, nrl, fSubsetCopy);
				root.edgelabel = key;
				root.parent = this;
				root.isTreeNode = false;
				this.children.add(root);
				root.BuildRecursiveTree(label, key, features);
			}
		}
	}

	public RecordList GenerateSubSet(RecordList rl, String column, String value) {
		RecordList child = new RecordList();
		// Add columns to child
		for (String col : rl.columns) {
			child.AddColumn(col);
		}
		// now for each feature filter add the records
		List<Integer> fl = rl.FilterRecordsByAttributeValue(column, value);
		for (int i : fl) {
			Attribute[] newRecord = child.InitRow();
			Attribute[] parentRecord = rl.rows.get(i);
			for (int j = 0; j < parentRecord.length; j++) {
				newRecord[j] = parentRecord[j];
			}
			child.AddRecordToList(newRecord, i);
		}
		// child.PrintRecords();
		// child.PrintAttributeSummaries();
		return child;
	}
	public Stack<ID3Node> GetAttributeOrder(HashMap<String, String> td ){
		//this method traverses the tree once and gets the attribute order 
		//of the tree. 
		//this will help in validating user input and map attribute
		//values to test in order. 
		HashMap<String, String> tdcopy = new HashMap<String, String>(); 
		tdcopy = (HashMap<String, String>) td.clone();
		ID3Node current = this;
		Queue<ID3Node> q = new LinkedList<ID3Node>();
		Stack<ID3Node> resNodes = new Stack<ID3Node>();
		q.add(current);
		int i = 0;
		//Make a single pass to get all the nodes for traversal.
		while (!q.isEmpty()) {
			// Print current
			ID3Node c = q.poll();
			//check if this node node feature contains element in hashmap
			if ( c.nodeFeature != null){
				String nodeValue =td.get(c.nodeFeature);
				if ( td.containsKey(c.nodeFeature)){
					System.out.println("Traversing Node " + c.nodeFeature);
					resNodes.push(c); 
					tdcopy.remove(c.nodeFeature);
				}
				//Now we need traverse this node's children. 
				for ( ID3Node n : c.children){
						//check if the edge label for this node matches with the parent.
					if ( nodeValue.equals("*") && !(resNodes.isEmpty() || resNodes.size() == td.size())){
						//check if all the remaining values have a * 
						for ( String k : tdcopy.keySet()){
							if (!tdcopy.get(k).equals("*")){
								//this check this nodes parents 
								return null; 
							}
						} 
						//nodeValue of # indicates that the column is 
					}
					if ( n.edgelabel.equals(nodeValue)){
						q.add(n);
						System.out.println(n.edgelabel);
						n.rl.PrintRecords();
						
					}
				}
			}
			i++;
		}
		return resNodes ; 
	}
	public void TestDecisionTreeMultipleWildCards(ID3Node startNode , HashMap<String, String> td){
		//start with the root. 
		//using recorsion. 
		if ( td.isEmpty()){
			this.rl.PrintRecords();
			return ;
			
		}
		else{			
			if ( td.containsKey(this.nodeFeature)){
				
			}
		}
	}
	public void TestDecisionTree(HashMap<String, String> td) {
		HashMap<String, Double> res = new HashMap<String, Double>();
		boolean trailingStars = false; 
		
		Stack<ID3Node> order ; 
		
		//First string for hashmap is the node attribute and second is the value to test. 
		//look for a trailing * 
		int num ;
		
		List<String> at = new ArrayList<String>();
		if (td.containsValue("*")){			
			//check for trailing * 
			order = GetAttributeOrder(td);
			if (order == null){
				//this means that there is a * in between. 
				System.out.println("Invalid input! Exception");
					//Call a different method to calculate probability.  
				//TestDecisionTreeMultipleWildCards(this, td);
			}
				
			num = order.size() -1 ;
			HashMap<Integer, String> orderMap = new HashMap<Integer, String>();
			while (num >= 0 ){
				ID3Node item = order.get(num);
				orderMap.put(num, item.nodeFeature);
				num--; 
			}
			System.out.println("Order map " + orderMap.toString());
			
			//Get all the trailing *
			int tCount = order.size() - 1 ; 
			while ( true){
				//Loop there is no traling zero. 
				if (td.size() == 0 ){
					break; 
				}
				System.out.println("While Loop: ");
				String lastElementFeature = orderMap.get(tCount); 		
				if (td.get(lastElementFeature).equals("*")){
					//then remove the last item from td. 
					td.remove(lastElementFeature); 
					//remove the last element from top of the stack. 
					order.pop();
					
				}
				else{
					break; 
				}
				tCount--; 
			}
			res= TestDecisionTreeForPath(td, orderMap , order );
			//check for any * values in betweeen. 
		}
		else{
			
			
			
			order = GetAttributeOrder(td);
			
			num = order.size() -1 ;
			
			HashMap<Integer, String> orderMap = new HashMap<Integer, String>();
			while (num >= 0 ){
				ID3Node item = order.get(num);
				orderMap.put(num, item.nodeFeature);
				num--; 
			}
			System.out.println("Order map " + orderMap.toString());
			res= TestDecisionTreeForPath(td, orderMap , order );
			System.out.println("Probability : " + res.toString());
			
			
		}
	}
				
			//if there is a * , get teh all possible values for the node and compute 
//			if (test.equals("*")) {
//				// check if there are any other
//				td.remove(atrs.length - 1);
//				for ( int j = 0 ; j < atrs.length -1 ; j++){
//					at.add(atrs[j]);
//				}
//				if (td.containsValue("*")) {
//					// There is a trailing * get all possible values and compute the
//					// probability.
//					
//				}
//				else{
//					String[] atrss = (String[]) at.toArray(new String[at.size()]);
//					res= TestDecisionTreeForPath(td, atrss);
//					System.out.println("Probability : " + res.toString());
//				}
//			else{
//				//leading or in between star
//				/*
//				 * for every * replace the value that attribute and get 
//				 */
//				String[] tdarr = new String[td.size()];
//				for ( int i : td.keySet()){
//					tdarr[i] = td.get(i);
//				}
//				
//				while( td.containsValue("*")){
//					//get the index of first * 
//					List<String> tdlist = Arrays.asList(tdarr);
//					int firstIndex = tdlist.indexOf("*");
//					//
//					String atrColumnName = atrs[firstIndex]; 
//					//Get all distinct values for column name 
//					
//				}
		
		public void TravelRecursively(HashMap<String, String> td){
			HashMap<String , String > local = (HashMap<String, String>) td.clone();
			if (this.nodeFeature == null){
				System.out.println("leaf node reached.");
				return ;
			}else{
				if ( local.containsKey(this.nodeFeature)){
					System.out.println("Node found");
					for( ID3Node c : this.children){
						if ( local.get(c.parent.nodeFeature).equals(c.edgelabel)){
							System.out.println("Matching edge found");
							c.rl.PrintRecords();
							c.TravelRecursively(local);
						}
					}
				}
				else{
					return ;
				}
			}
			
			
		}

	public HashMap<String, Double> TestDecisionTreeForPath(HashMap<String, String> td, HashMap<Integer, String> order, Stack<ID3Node> resNodes) {
		// Input validation.
		// Trailing *
		// Get the last element of the hashmap
		// Get distinct values for labels.
		HashMap<String, Double> resultProb = new HashMap<String, Double>();
		HashMap<String, Integer> rootlables = this.rl
				.GetAttributeLabelSummary(this.label);
		HashMap<Integer , String> resOrder = new HashMap<Integer, String>(); 
		Queue<ID3Node> q = new LinkedList<ID3Node>();
		System.out.println("testing for " + td.toString());
		int i = resNodes.size() - 1 ; 
		boolean found = false; 
		int ci = 0 ; 
		while ( !resNodes.isEmpty()){
			ID3Node item = resNodes.pop(); 
			//check if the count of the stack is equal to the number of items in hashmap.
			if ( item.nodeFeature.equals(order.get(i))){
				//print the count of the root node.
				for ( ID3Node c : item.children){
					//these are the leaf nodes. 
					//if the leaf node value matches that of the entry in the hashmap
					//check 
					if(td.get(item.nodeFeature).equals(c.edgelabel)){
						q.add(c); 
						System.out.println("found leaf node for value");
						c.rl.PrintRecords();
						//check if the count of the nodesfeatures is equal to the number of 
						//attributes
						if ( td.size() ==  ci ){
							System.out.println("processed all attributes.");
							int total = c.GetRowCountForLabel();
							System.out.print("Probability Labels: ");
							HashMap<String, Integer> labels = c.rl.GetAttributeLabelSummary(c.label);
							//This is the case when we have found a record until the leaf node. 
							for (String k : rootlables.keySet()) {
								if (labels.get(k) != null) {
									System.out.print(k + " => "
											+ (labels.get(k) * 1.0 / total)
											+ " |");
									resultProb.put(k,  (labels.get(k) * 1.0 / total));
								} else {
									System.out.print(k + " => 0" + "|");
									resultProb.put(k,  0.0);
								}

							}
							found = true ; 
							
						}
						else{
							if  ( c.children == null){
								System.out.println("Probability : 0");
							}
						}
						
						
					}
					ci++; 
					if ( found == true){
						break; 
					}
				}
				if (found == true ){
					break; 
				}
			}
			else{
				//this is the case where we cannot find the value for the exact node label. 
				//So we have to 
			}
			
		}
		
		return resultProb;
	}
			
			
//			if (c.nodeFeature != null && (i < atrs.length )) {
//				if (c.nodeFeature == atrs[i]) {
//					if (i == 0) {
//						resNodes.push(c);
//						i++;
//					} else {
//						System.out.println("found Attribute Node "
//								+ c.nodeFeature);
//						// this node should contain all the attribute values in
//						// the array
//						int count = 0;
//						HashMap<String, Integer> summ = c.rl
//								.GetAttributeLabelSummary(atrs[i - 1]);
//						if (summ.containsKey(td.get(i - 1))) {
//							resNodes.push(c);
//							i++;
//						} else {
//
//							System.out
//									.println("there is no data is this node.");
//							break;
//						}
//						System.out.println("Print Node data summary "
//								+ summ.toString());
//					}
//				}
//			}
//			for (ID3Node n : c.children) {
//				q.add(n);
//			}
//		}
//		int fCount = i;
//		/*
//		 * Now the stack contains all the nodes in order of traversal. So the
//		 * last node should contain the data for the matching attributes if it
//		 * is present in the tree.
//		 */
//		boolean stopParent = false;
//		while (!resNodes.isEmpty()) {
//			ID3Node top = resNodes.pop();
//			// System.out.println("Printing node");
//			q.add(top);
//			// System.out.println("printing top node. ");
//			top.rl.PrintAttributeSummaries();
//			if (top.edgelabel == atrs[atrs.length - 1]) {
//				top.rl.PrintAttributeLabelSumamry(top.label);
//				break;
//			}
//			i--;
//			// If the top of the stack matches
//			boolean found = false;
//			while (!q.isEmpty()) {
//				ID3Node temp = q.poll();
//				if (temp.children != null) {
//					ID3Node child;
//					for (int j = 0; j < temp.children.size(); j++) {
//						child = temp.children.get(j);
//						// each child would be a leaf node. so match it
//						// with the input value and compute probability.
//						System.out.println(child.edgelabel);
//						System.out.println("value to check : " + td.get(i));
//						if (child.edgelabel.equals(td.get(i))) {
//							System.out.println("Found leaf edge");
//							// This is the ans
//							// Print probability here.
//
//							if (fCount == atrs.length) {
//								HashMap<String, Integer> labels = child.rl
//										.GetAttributeLabelSummary(child.label);
//								int total = child.GetRowCountForLabel();
//								System.out.println("total " + total);
//								System.out.println("Print hashmap "
//										+ labels.toString());
//								System.out.print("Probability Labels: ");
//								// if the number of features match the number of
//								// items in the stack then
//								// directly calculate the probabilities of the
//								// nodes.
//								for (String k : rootlables.keySet()) {
//									if (labels.get(k) != null) {
//										System.out.print(k + " => "
//												+ (labels.get(k) * 1.0 / total)
//												+ " |");
//										resultProb.put(k,  (labels.get(k) * 1.0 / total));
//									} else {
//										System.out.print(k + " => 0" + "|");
//										resultProb.put(k,  0.0);
//									}
//
//								}
//								System.out.print("\n");
//								found = true;
//								stopParent = true;
//							} else {
//								HashMap<String, Integer> labels = child.rl
//										.GetAttributeLabelSummary(child.label);
//								int total = child.parent.GetRowCountForLabel();
//								System.out.println("total " + total);
//								System.out.println("Print hashmap "
//										+ labels.toString());
//								System.out
//										.print("Max Probability for given attributes: ");
//								// if the number of features match the number of
//								// items in the stack then
//								// directly calculate the probabilities of the
//								// nodes.
//								double yp = 0;
//								for (String k : rootlables.keySet()) {
//									if (labels.get(k) != null) {
//										yp = Math.random()
//												* (labels.get(k) * 1.0 / total);
//										System.out
//												.print(k + " => " + yp + " |");
//									} else {
//
//										double np = 1 - yp;
//										System.out.print(k + " => " + np + "|");
//									}
//
//								}
//								System.out.print("\n");
//								found = true;
//								stopParent = true;
//							}
//
//						}
//					}
//				}
//				if (found == true) {
//					break;
//				}
//			}
//			if (stopParent == true) {
//				break;
//			}
//		}
	

	public int GetRowCountForLabel() {
		return this.rl.attributeSummaries.get(this.label).totalCount;
	}
}
