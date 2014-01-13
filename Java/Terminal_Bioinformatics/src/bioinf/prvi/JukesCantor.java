package bioinf.prvi;

import java.util.ArrayList;

public class JukesCantor extends EvolutionModel {
	
	public JukesCantor(String path){
		Read(path);
	}
	
	
	public double[][] init() throws DomainException{
		
		double d, p, f, log;
		ArrayList<String> sequences = file.lines;
		int i,j,z;
		int len = file.lines.size();
		int distance = 	0;
//		int counter = 1;
		matrix = new double[len][len];
		
		String sequence_upper, sequence_bottom;
		
		// Sets the main diagonale to zero due to sequence similarity to itself
		for(i=0; i < len; i++){
			matrix[i][i] = 0;
		}
		
		// Loops through ArrayList containing sequences and counts differences between two characters at same index
		
		for(i=0; i < len; i++){
			sequence_upper = sequences.get(i);
			
			for(j=i+1; j < len; j++){
				sequence_bottom = sequences.get(j);

				for(z=0; z < sequence_upper.length() ; z++){
					
					if(sequence_upper.charAt(z) != sequence_bottom.charAt(z)) distance+=1;
					}
				
				// Calculates part of the JukesCantor formula which can 
				// result in -Infinity if p >= 0.75
				
				p = (double)(distance)/(double)(sequence_upper.length());
				f = 1.0 - ((double)4/3)*p;
				log = Math.log(f);
				d = -0.75*log;
				
				
				// Throws an Exception if p(proportion of sites 
				// that differ between two sequences), p >= 0.75
				if (Double.isInfinite(log) || Double.isNaN(log)){
					throw new DomainException("Out of domain. Sequences too different. Try other data " +
							"or try using another model of DNA evolution.");
					}
				
				// Symmetrically stores d to dissimilarity matrix
				else{
					d = -0.75*log;
					matrix[i][j] = d;
					matrix[j][i] = d;
					distance = 0;
				}
				

			}
			
		}
	return matrix;
	}	
}
