import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Comparator;
import bioinf.prvi.Cluster;
import bioinf.prvi.DomainException;
import bioinf.prvi.JukesCantor;
import bioinf.prvi.Kimura;


public class UPGMA {
	
	Comparator<Double> Comp = new Comparator<Double>() {
		
		@Override
		public int compare(Double arg0, Double arg1) {
			
			if(arg0 == 0) return 1;
			
			else if(arg1 == 0) return -1;
			
			else if(arg0 <= arg1) return -1;
			
			else return 1;
			
		}
	};
	
	double min;
	double[][] dis_matrix;

	ArrayList<Cluster> Tree = new ArrayList<Cluster>();
	String[] min_ids = new String[2];

	// Initializes Leaf Clusters with no children
	// There is as much Clusters as there is sequences
	public UPGMA(int c, String path){
		
		
		// User chooses evolution model which will make a dissimilarity matrix
		// JUKESCANTOR(INPUT: 1)
		// OTHERONE(INPUT: 2)
		Choice(c, path);
		
		// Prints out dissimilarity matrix
		int i, id = 65;
		
//		int j;
//
//		for(i = 0; i < dis_matrix.length; i++){
//			for(j = 0; j < dis_matrix.length; j++)
//				System.out.print(dis_matrix[i][j] + " ");
//			System.out.println();
//		}
//		System.out.println();
		
		// Adds each cluster to an ArrayList Tree
		// Each Cluster gets an id of 1 character from ASCII table starting at decimal number 65
		// Each CLuster has an initial distance to other Clusters which is passed as a second argument to the constructor
		for(i = 0; i < dis_matrix.length; i++){
			Tree.add(new Cluster(String.valueOf((char)(id)), dis_matrix[i]));
			id++;
		}
		
	}

	// Method which chooses an evolution model which user specifies by entering
	// an integer into console or by choosing a radiobox in GUI. If JukesCantor is chosen 
	// then method checks if  sequences are too dissimilar and catches a DomainException thrown by method JukesCantor.init
	
	void Choice(int i, String path){
		if(i == 1){
			
			try{
				JukesCantor model = new JukesCantor(path);
				long startTime = System.currentTimeMillis();
				dis_matrix = model.init();
				long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				System.out.println("JukesCantor elapsed time:" + Long.toString(elapsedTime) + "ms");
				}
			catch(DomainException e){
				System.out.println(e.getMessage());
				}
			}
		else if(i == 2){
			
			try{
				Kimura model = new Kimura(path);
				long startTime = System.currentTimeMillis();
				dis_matrix = model.init();
				long stopTime = System.currentTimeMillis();
				long elapsedTime = stopTime - startTime;
				System.out.println("Kimura elapsed time:" + Long.toString(elapsedTime) + "ms");
			}
			catch(DomainException e1){
				System.out.println(e1.getMessage());
				}
		}
		else System.out.println("No other models implemented. Wrong input.");
	}

	// This method contracts the ArrayList until its size is 1, that is, until all
	// Clusters aren't contracted to one root Cluster
	public void Contract(){
		long startTime = System.currentTimeMillis();
		while(Tree.size() != 1){
			
			min_ids = FindMinimum();
			
//			System.out.println(min_ids[0]+ " " + min_ids[1]);
			
			int i = indexOf(min_ids[0]); // Index of the first Cluster which forms the minimum
			int j = indexOf(min_ids[1]); // Index of the second Cluster which forms the minimum
			
			Cluster old1 = Tree.get(i);
			Cluster old2 = Tree.get(j);

			MergeAndUpdateDistances(old1, old2);

		}
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.println("Cotract loop elapsed time:" + Long.toString(elapsedTime) + "ms");
	}


	// Finds minimum iterating through HashMaps of every Cluster
	// Returns id-s of two clusters with minimal distance
	String[] FindMinimum(){
		
		int i;
		String min_id1, min_id2;
		String[] ret = new String[2];
//		min = Collections.min(Tree.get(0).distance.values());// Takes the minimum distance from the first Cluster in ArrayList to some other Cluster
	
		min = 1;
		for(i = 0; i < Tree.size(); i++){
			
			Cluster temp = Tree.get(i);
			Iterator<String> iter = temp.distance.keySet().iterator();
			
			while(true){
				
				if(iter.hasNext() == false) break;
				
				else{
					String temp_s = iter.next();
					if(temp.distance.get(temp_s) <= min && temp.distance.get(temp_s)!= 0){
						min = temp.distance.get(temp_s);
						min_id1 = temp.id;
						min_id2 = Tree.get(indexOf(temp_s)).id;
						ret[0] = min_id1;
						ret[1] = min_id2;
					}
				}

			}


		}
		return ret;

	}

	// Finds alphabetically greater id and returns a list containing 
	// descending sorted clusters
	// Used for forming id-s of new Clusters in alphabetical order
	Cluster[] greater(Cluster old1, Cluster old2){
		Cluster[] list = new Cluster[2];

		if(old1.id.compareTo(old2.id) < 0 ){
			list[0] = old1;
			list[1] = old2;
			return list;
		}

		else{
			list[0] = old2;
			list[1] = old1;
			return list;
		}
	}

	// Takes String id of a Cluster and returns its index in ArrayList Tree
	int indexOf(String key){
		int i;
		for(i = 0; i < Tree.size(); i++){
			if(Tree.get(i).id.equals(key)){
				return i;
			}
		}
		return -1;
	}

	// Method Merges two Clusters and calculates distance from that 
	// new Cluster formed by merging to all the other Clusters
	void MergeAndUpdateDistances(Cluster old1, Cluster old2){ 

		Set<String> keys = old1.distance.keySet();
		Iterator<String> iter = keys.iterator();
		Cluster[] parent = greater(old1, old2); // Cluster at the index 0 is alphabetically greater
		String new_id = parent[0].id + parent[1].id;
		int index_of_greater = Tree.indexOf(parent[0]); 

		HashMap<String,Double> temporary = new HashMap<String,Double>();

		double element;
		double depth = (min)/(double)(2); // Full length of a branch is half the minimum

		String temp_id;;

		// Calculating distance from newly formed Cluster to others 
		while(true){
			
			if(iter.hasNext()){
				
				// Temporary id of a Cluster
				temp_id = iter.next();
				
				// If id of a new Cluster contains temp_id and temporary field does not yet have new_id as a key
				// add new_id as a key and as a value distance 0.0 (because distance to itself is zero)
				if (new_id.contains(temp_id) && temporary.containsKey(new_id) == false){
					temporary.put(new_id, 0.0);
				}
				// Calculate distance to Cluster with temp_id if temp_id is not part of the newly formed Clusters id
				else if(new_id.contains(temp_id) == false){
					Cluster Temp_Cluster = Tree.get(indexOf(temp_id));
					
					// Sums the distances from two old Clusters to the temporary Cluster
					element = old1.distanceTo(temp_id, Temp_Cluster, 0) + old2.distanceTo(temp_id, Temp_Cluster, 0);
					// Divides by the number of each with each combinations
					element = element/(double)((old1.cardinal + old2.cardinal)*(Temp_Cluster.cardinal));
					temporary.put(temp_id, element);
				}
			}
			else break;
		}

		// Forming new Cluster by merging two old ones and removing 
		// old ones from the ArrayList
		Cluster newly_formed = new Cluster(new_id, old1.cardinal + old2.cardinal, temporary, parent[0], parent[1], depth );
		Tree.add(index_of_greater, newly_formed);
		Tree.remove(parent[1]);
		Tree.remove(parent[0]);

		// Updating distances of other Clusters in ArrayList
		UpdateOthers(newly_formed);


	}

	// This method removes distance to Clusters which merged into a new Cluster from 
	// HashMaps of other Clusters and adds distance from those Clusters to newly formed Cluster
	
	void UpdateOthers(Cluster newly_formed){
		int i;
		for(i = 0; i < Tree.size(); i++){
			String current_id = Tree.get(i).id;
			if(newly_formed.id.equals(current_id)){
				continue;
			}
			else{
				Tree.get(i).distance.remove(newly_formed.left_child.id);
				Tree.get(i).distance.remove(newly_formed.right_child.id);
				Tree.get(i).distance.put(newly_formed.id, newly_formed.distance.get(current_id));
			}
		}
	}


}
