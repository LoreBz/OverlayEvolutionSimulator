package exercise;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import model.Edge;
import model.Network;
import model.Node;
import model.OverlayGraph;
import model.Peer;
import model.UnderlayGraph;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;

public class Test {

	public Test() {

	}

	public static void main(String[] args) {
		Test test = new Test();
		test.setLookAndFeel();
		File graphFile = test.importGraphFile();

		UnderlayGraph underlayGraph = new UnderlayGraph(graphFile);
		underlayGraph.buildOLSRtables();

		OverlayGraph overlayGraph = new OverlayGraph(null, null);
		overlayGraph.randomInit(underlayGraph);

		Network network = new Network(underlayGraph, overlayGraph, null);
		network.updateNetwork();

		System.out.println(network.getUnderlayGraph());
		System.out.println(network.getOverlayGraph());
		test.displayall(network);
	}

	final boolean setLookAndFeel() {
		String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	File importGraphFile() {
		final JFileChooser fc = new JFileChooser();
		// FileNameExtensionFilter filter = new
		// FileNameExtensionFilter("edges");
		// fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: "
					+ fc.getSelectedFile().getName());
			return fc.getSelectedFile();
		}
		return null;
	}

	void displayall(Network network) {
		// far partire la grafica con bottoni e disegnino del grafo!

		// creazione vista grafo underlay
		Graph underlaygraph = new SingleGraph("Graph");
		underlaygraph.addAttribute("ui.quality");
		underlaygraph.addAttribute("ui.antialias");
		URL stylesheeturl = getClass().getResource(
				"/resources/underlay_stylesheet.css");
		underlaygraph.addAttribute("ui.stylesheet", "url('" + stylesheeturl
				+ "')");
		System.out.println(stylesheeturl);
		for (Node node : network.getUnderlayGraph().getNodes()) {
			underlaygraph.addNode(node.getName());
			underlaygraph.getNode(node.getName()).addAttribute("ui.label",
					node.getName());
		}
		for (Edge edge : network.getUnderlayGraph().getEdges()) {
			// aggiunge archi non direzionati tra edge.source e edge.destination
			String source = edge.getSource().getName();
			String destination = edge.getDestination().getName();
			underlaygraph.addEdge(source + destination, source, destination,
					false);
			underlaygraph.getEdge(source + destination).setAttribute("weight",
					edge.getWeight());
			underlaygraph.getEdge(source + destination).setAttribute(
					"ui.label", edge.getWeight());
		}
		// prepare the peer-nodes to be displayed as peer
		for (Peer peer : network.getOverlayGraph().getPeers()) {
			underlaygraph.getNode(peer.getName()).setAttribute("isPeer", true);
			underlaygraph.getNode(peer.getName()).addAttribute("ui.class",
					"important");
		}

		// creazione vista grafo overlay

		Graph overlaygraph = new SingleGraph("Graph");
		overlaygraph.addAttribute("ui.quality");
		overlaygraph.addAttribute("ui.antialias");
		URL stylesheeturl1 = getClass().getResource(
				"/resources/overlay_stylesheet.css");
		overlaygraph.addAttribute("ui.stylesheet", "url('" + stylesheeturl
				+ "')");
		for (Node node : network.getOverlayGraph().getPeers()) {
			overlaygraph.addNode(node.getName());
			overlaygraph.getNode(node.getName()).addAttribute("ui.label",
					node.getName());
			overlaygraph.getNode(node.getName()).addAttribute("ui.class",
					"important");
		}
		for (Edge edge : network.getOverlayGraph().getLinks()) {
			// aggiunge archi non direzionati tra edge.source e edge.destination
			String source = edge.getSource().getName();
			String destination = edge.getDestination().getName();
			overlaygraph.addEdge(source + destination, source, destination,
					false);
			// underlaygraph.getEdge(source +
			// destination).setAttribute("weight",
			// edge.getWeight());
			// underlaygraph.getEdge(source + destination).setAttribute(
			// "ui.label", edge.getWeight());
			overlaygraph.getEdge(source + destination).setAttribute("ui.class",
					"virtual");
		}

		// Preparing the frame

		JFrame myJFrame = new JFrame();
		myJFrame.setTitle("TopologyManagerSim");
		myJFrame.setSize(1280, 720);
		myJFrame.setResizable(true);
		myJFrame.setLocationRelativeTo(null);
		myJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myJFrame.getContentPane().setLayout(new BorderLayout());

		JPanel central_panel = new JPanel();
		central_panel.setLayout(new GridLayout(1, 2));

		// displaying the underlaygraph
		View underlaygraphview = underlaygraph.display().getDefaultView();
		underlaygraphview.openInAFrame(false);
		central_panel.add(underlaygraphview);

		// displaying the overlaygraph
		View overlaygraphview = overlaygraph.display().getDefaultView();
		overlaygraphview.openInAFrame(false);
		central_panel.add(overlaygraphview);
		myJFrame.add(central_panel, BorderLayout.CENTER);

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
