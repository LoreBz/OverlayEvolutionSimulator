package exercise;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;

public class Exercise {

	public static void main(String args[]) {

		TreeSet<String> nodes = new TreeSet<String>();
		TreeSet<String> edges = new TreeSet<String>();

		Graph graph = new SingleGraph("Graph");
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		graph.addAttribute(
				"ui.stylesheet",
				"edge { fill-color: grey; } edge.virtual {fill-color: blue; } node {fill-color: black; size: 10px; stroke-mode: plain; stroke-color: black; stroke-width: 1px;} node.important {fill-color: red; size: 15px;}");

		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("edges");
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(null);
		File file = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: "
					+ fc.getSelectedFile().getName());
			file = fc.getSelectedFile();
		}

		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(file));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
				String[] result = sCurrentLine.split("\\s");
				String n1 = result[0];
				String n2 = result[1];
				// String w = result[2];

				if (!nodes.contains(n1)) {
					nodes.add(n1);
					graph.addNode(n1);
				}

				if (!nodes.contains(n2)) {
					nodes.add(n2);
					graph.addNode(n2);
				}

				if (!edges.contains(n1 + n2)) {
					edges.add(n1 + n2);
					graph.addEdge("" + n1 + n2, n1, n2);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		int n = graph.getNodeCount();

		for (int i = 0; i < n; i++) {
			System.out.println(graph.getNode(i).getId());
			if (i % 2 == 0) {
				graph.getNode(i).setAttribute("isPeer", true);
				graph.getNode(i).addAttribute("ui.class", "important");
				if (i % 8 == 0 && i >= 8) {
					String s1, s2;
					s1 = graph.getNode(i).toString();
					s2 = graph.getNode(i - 8).toString();
					graph.addEdge("" + s1 + s2, s1, s2);
					graph.getEdge(s1 + s2).setAttribute("ui.class", "virtual");
				}
			}
		}

		// Preparing the frame

		JFrame myJFrame = new JFrame();
		myJFrame.setTitle(file.getName());
		myJFrame.setSize(1280, 720);
		myJFrame.setResizable(true);
		myJFrame.setLocationRelativeTo(null);
		myJFrame.setDefaultCloseOperation(1);
		myJFrame.getContentPane().setLayout(new BorderLayout());

		// displaying the graph
		View view = graph.display().getDefaultView();
		view.openInAFrame(false);
		myJFrame.add(view, BorderLayout.CENTER);

		// providing buttons
		JPanel southern_panel = new JPanel();
		southern_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JButton button_update = new JButton("Update Peers");
		JButton button_restart = new JButton("Restart");
		southern_panel.add(button_update);
		southern_panel.add(button_restart);
		myJFrame.add(southern_panel, BorderLayout.SOUTH);

		// displaying label with statistics
		JPanel eastern_panel = new JPanel();
		eastern_panel.setLayout(new GridLayout(3, 1));
		// eastern_panel.setSize(new Dimension(100, 720));
		JPanel row1 = new JPanel();
		JPanel row2 = new JPanel();
		JPanel row3 = new JPanel();
		JLabel lab1 = new JLabel(
				"Valore medio etx calcolato sull'inisieme dei link virtuali:");
		JLabel lab2 = new JLabel("Contatore aggiornamenti della topologia");
		// JLabel lab3 = new JLabel("asd");
		row1.add(lab1);
		row2.add(lab2);
		// row3.add(lab3);
		eastern_panel.add(row1);
		eastern_panel.add(row2);
		eastern_panel.add(row3);
		myJFrame.add(eastern_panel, BorderLayout.EAST);

		myJFrame.setVisible(true);

	}
}