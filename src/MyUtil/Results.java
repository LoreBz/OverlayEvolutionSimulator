package MyUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		Set<Integer> keys = hop_cicles2overallvalue.keySet();
		List<Integer> sortedkeys = new ArrayList<>();
		sortedkeys.addAll(keys);
		Collections.sort(sortedkeys);
		File file;
		try {

			file = new File(selectedFile.getAbsolutePath() + "hop_series.txt");

			if (file.createNewFile()) {
				System.out.println("File is created!");
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(file.getAbsolutePath())));
				// operazioni output hop
				for (Integer i : sortedkeys) {
					out.println(hop_cicles2overallvalue.get(i)+"");
				}
				out.close();
			} else {
				System.out.println("File already exists.");
			}
			file = new File(selectedFile.getAbsolutePath()
					+ "djketx_series.txt");

			if (file.createNewFile()) {
				System.out.println("File is created!");
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(file.getAbsolutePath())));
				// operazioni output djk
				for (Integer i : sortedkeys) {
					out.println(djketx_cicles2overallvalue.get(i)+"");
				}
				out.close();
			} else {
				System.out.println("File already exists.");
			}

			file = new File(selectedFile.getAbsolutePath() + "ampp_series.txt");

			if (file.createNewFile()) {
				System.out.println("File is created!");
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(file.getAbsolutePath())));
				// operazioni output ampp
				for (Integer i : sortedkeys) {
					out.println(ampp_cicles2overallvalue.get(i)+"");
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

	public void reset() {
		this.ampp_cicles2overallvalue.clear();
		this.djketx_cicles2overallvalue.clear();
		this.hop_cicles2overallvalue.clear();

	}

}
