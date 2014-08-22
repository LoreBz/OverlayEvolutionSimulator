package exercise;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

public class Prova {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File selectedFile=null;
		JFileChooser fileDialog = new JFileChooser();
		int saveChoice = fileDialog.showSaveDialog(null);
		if (saveChoice == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileDialog.getSelectedFile();
		}
		
		try {
			 
		      File file = new File(selectedFile.getAbsolutePath());
	 
		      if (file.createNewFile()){
		        System.out.println("File is created!");
		      }else{
		        System.out.println("File already exists.");
		      }
	 
	    	} catch (IOException e) {
		      e.printStackTrace();
		}
	}

}
