package bioinf.prvi;
import java.util.*;

// PUBLIC class Cluster 

public class Cluster {
	public String id; // identification number
	public int cardinal; // number of joined clusters in a cluster 
	
	public HashMap<String, Double> distance = new HashMap<String, Double>(); // current distance used for finding minimum
	public HashMap<String, Double> distance_orig; // initial distances used for calculating new distances 
	public Cluster left_child, right_child; //left and right child of a type cluster
	public double depth; // depth of a cluster in a graph
	
	// Constructor for Clusters with children
	public Cluster(String id, int cardinal, HashMap<String,Double> dist, Cluster left_child, Cluster right_child, double depth){
		
		this.id = id;
		this.cardinal = cardinal;
		this.left_child = left_child;
		this.right_child = right_child;
		this.depth = depth;
		this.distance = dist;

	}
	
	//Constructor for clusters with no children
	public Cluster(String id, double[] distance){
		
		this.id = id;
		this.cardinal = 1;
		this.left_child = null;
		this.right_child = null;
		this.depth = 0;
		
		int i;
		
		// Fills the HashMap with id-s of other clusters and distances to them 
		for( i = 65; i < 65 + distance.length; i++){
			
			if(id.equals(String.valueOf((char)(i)))){
				continue;
				
			}
			
			else{
				this.distance.put(String.valueOf((char)(i)), distance[i - 65]);
			}
		}
		this.distance_orig = new HashMap<String,Double>(this.distance);
	}

	
	// Method which takes an id of a cluster and returns distance from that cluster
	// to this cluster recursively
	public double distanceTo(String other_id, Cluster other, double dist){
		
		// If both Clusters contain only one Cluster return distance from original distance matrix + distance passed as an argument
		if(this.cardinal == 1 && other.cardinal == 1){
			return dist + distance_orig.get(other_id);
			}
		// If this Cluster has more than one Cluster contained in itself return distance passed as an argument and call distanceTo
		// method on each of its children
		if(this.cardinal > 1){
			return dist + this.left_child.distanceTo(other_id, other, dist) + this.right_child.distanceTo(other_id, other, dist);
			}
		// Same as the previous if but just call the method distanceTo for other's children
		if(other.cardinal > 1){
			return dist + other.left_child.distanceTo(this.id, this, dist) + other.right_child.distanceTo(this.id, this, dist);
		}
		else{
			return dist;
		}
		}
	}
	
	
	
	

