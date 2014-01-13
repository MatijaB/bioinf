import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JTextField;


import javax.swing.JLabel;
import javax.swing.JRadioButton;


public class GUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	String arg1 = null, arg2 = null;
	private JTextField textField_1;
	private JButton btnUpgma;
	final JFileChooser fc = new JFileChooser();
	int returnVal;
	private JLabel lblFileWithAligned;
	
	
	GUI(){
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Creates a 
		JButton btnNewButton_1 = new JButton("Browse");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e1) {
				returnVal = fc.showOpenDialog(GUI.this);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = fc.getSelectedFile(); 
					textField_1.setText(file.getPath());
				}
			}
		});
		btnNewButton_1.setBounds(273, 183, 155, 25);
		getContentPane().add(btnNewButton_1);
		
		textField_1 = new JTextField();
		textField_1.setBounds(22, 186, 239, 19);
		getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		
		
		btnUpgma = new JButton("Let's go");
		btnUpgma.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e2) {
				try{
					
					arg2 = textField_1.getText();
					
					if(arg1 == null || arg2 == null) System.out.println("Please check your input.");
					
					else new Main(arg1, arg2);
					
				}
				
				catch(Exception ex){
					System.out.println(ex.getMessage());
				}
				
				
			}
		});
		btnUpgma.setBounds(147, 223, 117, 25);
		getContentPane().add(btnUpgma);
		
		lblFileWithAligned = new JLabel("File with aligned sequences:");
		lblFileWithAligned.setBounds(26, 145, 252, 49);
		getContentPane().add(lblFileWithAligned);
		
		JLabel lblModelOfDna = new JLabel("Model of Dna Evolution:");
		lblModelOfDna.setBounds(22, 58, 218, 15);
		getContentPane().add(lblModelOfDna);
		
		JRadioButton rdbtnJukesCantor = new JRadioButton("Jukes Cantor");
		rdbtnJukesCantor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e1) {
				arg1 = "1";
				
				}
		});
		rdbtnJukesCantor.setBounds(51, 81, 149, 23);
		getContentPane().add(rdbtnJukesCantor);
		
		JRadioButton rdbtnOtherOne = new JRadioButton("Kimura");
		rdbtnOtherOne.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e2) {
				arg1 = "2";
			}
		});
		rdbtnOtherOne.setBounds(51, 108, 149, 23);
		getContentPane().add(rdbtnOtherOne);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnOtherOne);
		group.add(rdbtnJukesCantor);

	}
	

	void open(){
		setSize(500, 500);
		setVisible(true);
	
	}
	
	public static void main(String args[]){
		GUI gui = new GUI();
		
		try{
			gui.open();
		}
		catch(Exception e){
			System.out.println();
			
		}
	}
}
