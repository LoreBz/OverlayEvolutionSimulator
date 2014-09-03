package MyUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Results {

	Map<Integer, Float> hop_cicles2overallvalue;
	Map<Integer, Float> djketx_cicles2overallvalue;
	Map<Integer, Float> ampp_cicles2overallvalue;

	public Results() {
		this.hop_cicles2overallvalue = new HashMap<>();
		this.djketx_cicles2overallvalue = new HashMap<>();
		this.ampp_cicles2overallvalue = new HashMap<>();

	}

	public void printEvolutionResults() {
		// TODO Auto-generated method stub
		File selectedFile = null;
		JFileChooser fileDialog = new JFileChooser();
		int saveChoice = fileDialog.showSaveDialog(null);
		if (saveChoice == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileDialog.getSelectedFile();
		}
		if (selectedFile == null) {
			JOptionPane.showMessageDialog(null, "Errore!");
			return;
		}
		// se poi vale 1 vuol dire che ho i dati su hop, 2 djketx, 3 ampp
		int discr = 47;
		if (!this.hop_cicles2overallvalue.isEmpty())
			discr = 1;
		if (!this.djketx_cicles2overallvalue.isEmpty())
			discr = 2;
		if (!this.ampp_cicles2overallvalue.isEmpty())
			discr = 3;
		try {

			File file = new File(selectedFile.getAbsolutePath() + ".txt");

			if (file.createNewFile()) {
				System.out.println("File is created!");
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(file.getAbsolutePath())));
				// operazioni output
				switch (discr) {
				case 1:
					for (Entry<Integer,Float> entry : this.hop_cicles2overallvalue.entrySet()) {
						out.println(""+entry.getKey()+" "+entry.getValue());
					}
					break;
				case 2:
					for (Entry<Integer,Float> entry : this.djketx_cicles2overallvalue.entrySet()) {
						out.println(""+entry.getKey()+" "+entry.getValue());
					}
					break;
				case 3:
					for (Entry<Integer,Float> entry : this.ampp_cicles2overallvalue.entrySet()) {
						out.println(""+entry.getKey()+" "+entry.getValue());
					}
					break;
				default:
					break;
				}
				out.close();
			} else {
				System.out.println("File already exists.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Map<Integer, Float> getHop_cicles2overallvalue() {
		return hop_cicles2overallvalue;
	}

	public void setHop_cicles2overallvalue(
			Map<Integer, Float> hop_cicles2overallvalue) {
		this.hop_cicles2overallvalue = hop_cicles2overallvalue;
	}

	public Map<Integer, Float> getDjketx_cicles2overallvalue() {
		return djketx_cicles2overallvalue;
	}

	public void setDjketx_cicles2overallvalue(
			Map<Integer, Float> djketx_cicles2overallvalue) {
		this.djketx_cicles2overallvalue = djketx_cicles2overallvalue;
	}

	public Map<Integer, Float> getAmpp_cicles2overallvalue() {
		return ampp_cicles2overallvalue;
	}

	public void setAmpp_cicles2overallvalue(
			Map<Integer, Float> ampp_cicles2overallvalue) {
		this.ampp_cicles2overallvalue = ampp_cicles2overallvalue;
	}

}
