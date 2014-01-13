package bioinf.prvi;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;;

// Class for reading and formating sequences from a .txt file
// Accepts Fasta Format
public class Reader {
	
	ArrayList<String> lines = new ArrayList<String>();
	
	public void ReadS(String file_path) throws FormatException{
		String path = file_path;
		
		try{
		FileReader filer = new FileReader(path);
		BufferedReader input = new BufferedReader(filer);
		String line = input.readLine(), sequence_new;	
		int first = 1;
		while(true){
			
			if(line == null) break;
			
			else if(line.startsWith(">") == false && first == 1) 
				throw new FormatException("Check if sequences are in FASTA format.");
		
			else if(line.startsWith(">")){
				
				first = 0;
				
				line = input.readLine();
				
				sequence_new = "";
				while(line.startsWith(">") == false){
					sequence_new += line;
					line = input.readLine();
					if(line == null) break;
					
				}
				lines.add(sequence_new);
//				if(lines.size() == 60) break;
			}
			}
		filer.close();
		input.close();
		}
		catch(FileNotFoundException e){
			System.out.println(e);
			
		}
		catch(IOException e){
			System.out.println(e);
		}	
	}
	
	public void Print(){
		System.out.println(lines);
	}

}
