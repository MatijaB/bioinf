import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import bioinf.prvi.Cluster;

// Accepts two command line arguments (int Choice, String path)

// Integer argument Choice defines which evolution model will be used for
// calculating the dissimilarity matrix. 

// Currently: 	1 - JukesCantor
// 				2 - OtherOne

// String argument path shows the path to .txt file containing aligned sequences

public class Main {


	private Process proc;

	public Main(String arg1, String arg2){
		
		
		UPGMA u = new UPGMA(Integer.valueOf(arg1), arg2);
		
		long startTime = System.currentTimeMillis();
		u.Contract();
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		System.out.println("UPGMA.Contract elapsed time:" + Long.toString(elapsedTime) + "ms");
		
		
		// Initilization of a Newick class which is used for formating already 
		// calculated contracted graph
		Newick n = new Newick();
		
		
		// Takes the root node and recursively fills HashMap
		// with Clusters that have children as keys and as values formated 
		// strings that represent their children and branch values
		Cluster Root_Cluster = u.Tree.get(0);
		n.Recursion(Root_Cluster);
		
		// Prints out Newick String 
		String Newick_string = n.GetNewickFormat(Root_Cluster) + ";";
		System.out.println("Newick String Format:");
		System.out.println(Newick_string);
		
		// Saves the Newick string to a file called newick.txt which is later 
		// used for ploting
		try {
			
			File file = new File( System.getProperty("user.dir") + "/newick.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(Newick_string);
			bw.close();
			} 
		
		catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		
		// This part executes external app (NJplot) which is used for ploting a phylogeny
		// tree in Newick format. The required .txt file is saved in same directory as the
		// executable file. If njplot is installed on user's OS it runs immediately.
		// If it's not installed user must confirm running of an executable file. 
		// Sudo permissions are required.
		System.gc();
		System.out.println();
		try{
			proc = Runtime.getRuntime().exec("njplot " + System.getProperty("user.dir") + "/newick.txt");
			
			
		}
		catch(IOException e){
			System.out.println(e.getMessage());
			System.out.println("###############################################################################################");
			System.out.println("############ Need to install Njplot: http://pbil.univ-lyon1.fr/software/njplot.html ############");
			System.out.println("###############################################################################################");
			
			System.out.println();
			System.out.println();
			// User input
			Scanner scan = new Scanner(System.in);
			System.out.println("WARNING: Would you like to directly run Njplot now? y/n");
			String choice = scan.nextLine();
			
			if(choice.equals("y")){
				try{
					String run_command = System.getProperty("user.dir") + "/njplot.linux ";
					String run_argument = System.getProperty("user.dir") + "/newick.txt";
					Process proc = Runtime.getRuntime().exec(run_command + run_argument);
				}
				catch(IOException e1){
					
					System.out.println(e.getMessage());
					System.out.println("Try changing njplot.linux permissions with sudo chmod +x command.");
					
				}
			}
			else if(choice.equals("n")) System.out.println("Install Njplot for graphical representation of phylogenetic tree in Newick format.");
			
			else System.out.println("Incorrect input! Install Njplot for graphical representation of phylogenetic tree in Newick format..");
		}
		
	
	}
}
