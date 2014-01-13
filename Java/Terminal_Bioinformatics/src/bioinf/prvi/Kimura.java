package bioinf.prvi;

import java.util.ArrayList;
import java.util.Comparator;

public class Kimura extends EvolutionModel{
	
	public Kimura(String path){
		Read(path);
	}

	//Checks Transitions (A <-> G,i.e from purine to purine, or C <-> T from pyrimidine to pyrimidine
	Comparator<Character> Transition = new Comparator<Character>() {
		@Override
		public int compare(Character o1, Character o2) {

			if ((o1.equals('A') && o2.equals('G')) || (o1.equals('G') && o2.equals('A')))
				return 1;

			else if((o1.equals('C') && o2.equals('T')) || (o1.equals('T') && o2.equals('C')))
				return 1;

			else return 0;
		};

	};
	
	// Checks Transversions - from purine to pyrimidine or vice versa
	Comparator<Character> Transversion = new Comparator<Character>() {
		@Override
		public int compare(Character c0, Character c1) {

			if((c0.equals('A') || c0.equals('G')) && (c1.equals('C') || c1.equals('T')))
				return 1;

			else if((c0.equals('C') || c0.equals('T')) && (c1.equals('A') || c1.equals('G')))
				return 1;

			else return 0;
		};
	};




	// Initialization of a distance matrix
	public double[][] init() throws DomainException{
		
		
		ArrayList<String> sequences = file.lines;
		int i,j,z;
		int number_of_sequences = file.lines.size();
		int transitions = 	0;
		int transversions = 0;
		double length_of_sequence, proportion_transitions, proportion_transversions;
		matrix = new double[number_of_sequences][number_of_sequences];
		String sequence_upper, sequence_bottom;
		char from_bottom, from_upper;
		double help1, help2, distance;
		
		// Sets the main diagonale to zero due to sequence similarity to itself
		for(i=0; i < number_of_sequences; i++){
			matrix[i][i] = 0;
		}

		// Takes sequence which was first in .fasta or .txt file and calculates distance from that sequence to all the sequences that are 
		// beneath that sequence. Does this for every sequence treating it as the first. 0(NlogN) complexity.
		for(i=0; i < number_of_sequences; i++){
			sequence_upper = sequences.get(i);
			length_of_sequence = sequence_upper.length();
			for(j=i+1; j < number_of_sequences; j++){
				
				sequence_bottom = sequences.get(j);
				

				
				for(z=0; z < length_of_sequence; z++){
					
					from_upper = sequence_upper.charAt(z);
					from_bottom = sequence_bottom.charAt(z);

					if(Transition.compare(from_upper, from_bottom) == 1) transitions+=1;
					
					else if(Transversion.compare(from_upper, from_bottom) == 1) transversions+=1;

				}
				
				// Calculating formula d = -0.5log(1.0 - 2*p - q) - 0.25log(1 - 2*q) where p is proportion of transitions and q
				// is the proportion of transversions
				proportion_transitions = (double)(transitions)/length_of_sequence;
				proportion_transversions = (double)(transversions)/length_of_sequence;
				
				help1 = -0.5*Math.log(1.0 - 2.0*proportion_transitions - proportion_transversions);
				help2 = -0.25*Math.log(1.0 - 2.0 * proportion_transversions);
				distance = help1 + help2;
				
				//
				if((Double.isInfinite(help1)|| Double.isNaN(help1)) || (Double.isInfinite(help2)|| Double.isNaN(help2))){
					throw new DomainException("Sequences too different. Try with other data.");
					}
				matrix[i][j] = distance;
				matrix[j][i] = distance;
				
				transversions = 0; transitions = 0;
				
			}
		}
		return matrix;
	}
} 

