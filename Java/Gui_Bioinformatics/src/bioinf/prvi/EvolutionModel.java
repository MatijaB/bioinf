package bioinf.prvi;

import java.util.ArrayList;

abstract class EvolutionModel {
	// Instancing a Reader class which reads from a .txt file
	Reader file = new Reader();
	double[][] matrix;

	//Reading from .txt file containing aligned sequences
	
	public void Read(String path){
		try{
			file.ReadS(path);
		}
		catch(FormatException f){
			System.out.println(f.getMessage());
			
		}
		
	}
}





