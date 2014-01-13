import bioinf.prvi.*;
import java.util.*;

public class Newick {
	public LinkedHashMap<String, String[]> Newick = new LinkedHashMap<String, String[]>();
	
	
	// This method takes a cluster and stores a string into LinkedHashMap Newick in format
	// (Cluster_child:branch_length, Cluster_child:branch_length)Cluster_parent:branch_length
	public void Recursion(Cluster current){
		String temp[] = new String[2];

		// If Cluster has children store a string for each of them
		if(current.left_child !=null && current.right_child != null){
			temp[0] = current.left_child.id + ":" +  Double.toString(current.depth - current.left_child.depth);
			temp[1] = current.right_child.id + ":" + Double.toString(current.depth - current.right_child.depth);
		
		// Store created string and call this method for each of the children
		if(temp.length != 0){
			Newick.put(current.id, temp );
			Recursion(current.left_child);
			Recursion(current.right_child);
		}
		
		}
	
}
	// Method which takes root Cluster and recursively generates Newick tree String
	public String GetNewickFormat(Cluster root){
		
		String[] temp = new String[2];
		
		// If Cluster does not have any children it is a leaf Cluster and its position is the inner most parenthesis
		// so a string is returned
		if((Newick.keySet().contains(root.left_child.id) == false)
		&& (Newick.keySet().contains(root.right_child.id) == false)){
			
			temp = Newick.get(root.id);
			return "(" + temp[0] + "," + temp[1] + ")";
		}
		// Each of this ifs checks whether a Cluster has children and calls recursively method on its children
		else if(Newick.keySet().contains(root.left_child.id) == true &&
				Newick.keySet().contains(root.right_child.id) == false){
			
			temp = Newick.get(root.id);
			return "(" + GetNewickFormat(root.left_child)+ temp[0] + "," + temp[1] + ")";
			}
		else if(Newick.keySet().contains(root.left_child.id) == false &&
				Newick.keySet().contains(root.right_child.id) == true){
			
			temp = Newick.get(root.id);
			return "(" + temp[0] + "," + GetNewickFormat(root.right_child) + temp[1] + ")";
		}
		else if(Newick.keySet().contains(root.left_child.id) == true &&
				Newick.keySet().contains(root.right_child.id) == true){
			
			temp = Newick.get(root.id);
			return "(" + GetNewickFormat(root.left_child) + temp[0] + "," + GetNewickFormat(root.right_child) + temp[1] + ")";
		}
		else return null;
			
			
		
		
	}
}
