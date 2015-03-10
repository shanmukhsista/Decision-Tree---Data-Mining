package homework3;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
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
		features.remove(feature);
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
			for (String key : fValues.keySet()) {
				// key variable holds all the values for the column.
				// Generate a subset for these values and compute the roots.
				int labelIndex = rl.attributeSummaries.get(label)
						.getColumnIndex();
				System.out.println("child  edge " + key + " for parent : " + this.nodeFeature + "; features " + this.features.toString());
				RecordList nrl = GenerateSubSet(this.rl, f, key);
				List<String> fCopy = new ArrayList<String>(features);
				ID3Node root = new ID3Node(label, nrl, fCopy);
				root.edgelabel = key;
				root.parent = this;
				root.isTreeNode = false;
				this.children.add(root);
				root.BuildRecursiveTree(label, key, fCopy);
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

	public void TestDecisionTree(HashMap<Integer, String> td, String[] atrs) {
		HashMap<String, Double> res = new HashMap<String, Double>();
		
		if ( td.size() > atrs.length){
			System.out.println("Invalid Input. Please check values. ");
		}
		List<String> at = new ArrayList<String>();
		if (!td.containsValue("*")){
			
			res= TestDecisionTreeForPath(td, atrs);
			System.out.println("Probability : " + res.toString());
		} 
		else{ 
			//test for traling *
			String test = td.get(atrs.length - 1);
			if (test.equals("*")) {
				// check if there are any other
				td.remove(atrs.length - 1);
				for ( int j = 0 ; j < atrs.length -1 ; j++){
					at.add(atrs[j]);
				}
				if (td.containsValue("*")) {
					// There is a trailing * get all possible values and compute the
					// probability.
					
				}
				else{
					String[] atrss = (String[]) at.toArray(new String[at.size()]);
					res= TestDecisionTreeForPath(td, atrss);
					System.out.println("Probability : " + res.toString());
				}
			}
			else{
				//leading or in between star
				/*
				 * for every * replace the value that attribute and get 
				 */
				String[] tdarr = new String[td.size()];
				for ( int i : td.keySet()){
					tdarr[i] = td.get(i);
				}
				
				while( td.containsValue("*")){
					//get the index of first * 
					List<String> tdlist = Arrays.asList(tdarr);
					int firstIndex = tdlist.indexOf("*");
					//
					String atrColumnName = atrs[firstIndex]; 
					//Get all distinct values for column name 
					
				}
			}
			
		}
		
		
	}

	public HashMap<String, Double> TestDecisionTreeForPath(HashMap<Integer, String> td, String[] atrs) {
		// Input validation.
		// Trailing *
		// Get the last element of the hashmap
		// Get distinct values for labels.
		HashMap<String, Double> resultProb = new HashMap<String, Double>();
		HashMap<String, Integer> rootlables = this.rl
				.GetAttributeLabelSummary(this.label);

		
		System.out.println("testing for " + td.toString());
		ID3Node current = this;
		Queue<ID3Node> q = new LinkedList<ID3Node>();
		Stack<ID3Node> resNodes = new Stack<ID3Node>();
		q.add(current);
		int i = 0;
		while (!q.isEmpty()) {
			// Print current
			ID3Node c = q.poll();
			if (c.nodeFeature != null && (i < atrs.length )) {
				if (c.nodeFeature == atrs[i]) {
					if (i == 0) {
						resNodes.push(c);
						i++;
					} else {
						System.out.println("found Attribute Node "
								+ c.nodeFeature);
						// this node should contain all the attribute values in
						// the array
						int count = 0;
						HashMap<String, Integer> summ = c.rl
								.GetAttributeLabelSummary(atrs[i - 1]);
						if (summ.containsKey(td.get(i - 1))) {
							resNodes.push(c);
							i++;
						} else {

							System.out
									.println("there is no data is this node.");
							break;
						}
						System.out.println("Print Node data summary "
								+ summ.toString());
					}
				}
			}
			for (ID3Node n : c.children) {
				q.add(n);
			}
		}
		int fCount = i;
		/*
		 * Now the stack contains all the nodes in order of traversal. So the
		 * last node should contain the data for the matching attributes if it
		 * is present in the tree.
		 */
		boolean stopParent = false;
		while (!resNodes.isEmpty()) {
			ID3Node top = resNodes.pop();
			// System.out.println("Printing node");
			q.add(top);
			// System.out.println("printing top node. ");
			top.rl.PrintAttributeSummaries();
			if (top.edgelabel == atrs[atrs.length - 1]) {
				top.rl.PrintAttributeLabelSumamry(top.label);
				break;
			}
			i--;
			// If the top of the stack matches
			boolean found = false;
			while (!q.isEmpty()) {
				ID3Node temp = q.poll();
				if (temp.children != null) {
					ID3Node child;
					for (int j = 0; j < temp.children.size(); j++) {
						child = temp.children.get(j);
						// each child would be a leaf node. so match it
						// with the input value and compute probability.
						System.out.println(child.edgelabel);
						System.out.println("value to check : " + td.get(i));
						if (child.edgelabel.equals(td.get(i))) {
							System.out.println("Found leaf edge");
							// This is the ans
							// Print probability here.

							if (fCount == atrs.length) {
								HashMap<String, Integer> labels = child.rl
										.GetAttributeLabelSummary(child.label);
								int total = child.GetRowCountForLabel();
								System.out.println("total " + total);
								System.out.println("Print hashmap "
										+ labels.toString());
								System.out.print("Probability Labels: ");
								// if the number of features match the number of
								// items in the stack then
								// directly calculate the probabilities of the
								// nodes.
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
								System.out.print("\n");
								found = true;
								stopParent = true;
							} else {
								HashMap<String, Integer> labels = child.rl
										.GetAttributeLabelSummary(child.label);
								int total = child.parent.GetRowCountForLabel();
								System.out.println("total " + total);
								System.out.println("Print hashmap "
										+ labels.toString());
								System.out
										.print("Max Probability for given attributes: ");
								// if the number of features match the number of
								// items in the stack then
								// directly calculate the probabilities of the
								// nodes.
								double yp = 0;
								for (String k : rootlables.keySet()) {
									if (labels.get(k) != null) {
										yp = Math.random()
												* (labels.get(k) * 1.0 / total);
										System.out
												.print(k + " => " + yp + " |");
									} else {

										double np = 1 - yp;
										System.out.print(k + " => " + np + "|");
									}

								}
								System.out.print("\n");
								found = true;
								stopParent = true;
							}

						}
					}
				}
				if (found == true) {
					break;
				}
			}
			if (stopParent == true) {
				break;
			}
		}
		return resultProb;
	}

	public int GetRowCountForLabel() {
		return this.rl.attributeSummaries.get(this.label).totalCount;
	}
}
