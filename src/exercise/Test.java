package exercise;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import model.Edge;
import model.Network;
import model.Node;
import model.OverlayGraph;
import model.Peer;
import model.UnderlayGraph;
import model.VirtualEdge;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;

public class Test {
	JTextField tf_valoremedio;
	JTextField tf_contatoreAggiornamenti;
	JTextField tf_ultimoValMedio;
	JTextField tf_ValMedioAttuale;
	Network network;
	String metrica;
	JButton button_update;

	public Test() {

	}

	public static void main(String[] args) {
		final Test test = new Test();
		test.setLookAndFeel();
		File graphFile = test.importGraphFile();
		if (graphFile == null) {
			JOptionPane.showMessageDialog(null,
					"Chiusura applicazione: impossibile procedere senza selezionare un file edges");
			System.exit(0);
		}

		UnderlayGraph underlayGraph = new UnderlayGraph(graphFile);
		underlayGraph.buildOLSRtables();

		OverlayGraph overlayGraph = new OverlayGraph(null, null);
		overlayGraph.randomInit(underlayGraph);

		test.network = new Network(underlayGraph, overlayGraph, null);
		test.network.updateNetwork();

		test.displayall(test.network);

		// richiesta metrica da utilizzare
		test.metrica = test.getMetricFromUser();
		test.button_update.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Integer n = Integer.parseInt(test.tf_contatoreAggiornamenti
						.getText());
				System.out.println("Esecuzione ciclo di aggiornamento no: "
						+ (n + 1));
				Simulator s = new Simulator(test.network, test.metrica);
				s.one_cicle_update();
				test.tf_contatoreAggiornamenti.setText("" + (n + 1));
				test.update_graphics_components();
			}
		});

	}

	protected void update_graphics_components() {
		// aggiornare i campi valore medio, ultimo valore medio e differenza tra
		// i due

	}

	private String getMetricFromUser() {
		Object[] selectionValues = { "HopCount", "Djkstra-ETX",
				"AvoidMultiPeerPath" };
		String initialSelection = "HopCount";
		Object selection = JOptionPane.showInputDialog(null,
				"Scegli il criterio di aggiornamento dei peer",
				"Metriche/criteri", JOptionPane.QUESTION_MESSAGE, null,
				selectionValues, initialSelection);
		System.out.println(selection);
		return (String) selection;
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
		// far partire la grafica con bottoni e disegnini dei grafi!

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
		// URL stylesheeturl1 = getClass().getResource(
		// "/resources/overlay_stylesheet.css");
		overlaygraph.addAttribute("ui.stylesheet", "url('" + stylesheeturl
				+ "')");
		for (Node node : network.getOverlayGraph().getPeers()) {
			overlaygraph.addNode(node.getName());
			overlaygraph.getNode(node.getName()).addAttribute("ui.label",
					node.getName());
			overlaygraph.getNode(node.getName()).addAttribute("ui.class",
					"important");
		}
		for (VirtualEdge edge : network.getOverlayGraph().getLinks()) {
			// aggiunge archi non direzionati tra edge.source e edge.destination
			String source = edge.getSource().getName();
			String destination = edge.getDestination().getName();
			overlaygraph.addEdge(source + "<->" + destination, source,
					destination, false);
			// overlaygraph.getEdge(source + destination).setAttribute(
			// "ui.label", edge.toString());
			overlaygraph.getEdge(source + "<->" + destination).setAttribute(
					"ui.class", "virtual");
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
		myJFrame.getContentPane().add(central_panel, BorderLayout.CENTER);

		// providing buttons
		JPanel southern_panel = new JPanel();
		southern_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		button_update = new JButton("Update Peers");

		JButton button_saveOverlay = new JButton("Save overlay graph");
		southern_panel.add(button_update);
		southern_panel.add(button_saveOverlay);
		myJFrame.getContentPane().add(southern_panel, BorderLayout.SOUTH);

		// listeners for buttons

		// displaying label with statistics
		JPanel eastern_panel = new JPanel();
		eastern_panel.setLayout(new GridLayout(3, 1));
		// eastern_panel.setSize(new Dimension(100, 720));
		JPanel row1 = new JPanel();
		JPanel row2 = new JPanel();
		JPanel row3 = new JPanel();
		JLabel lab1 = new JLabel(
				"Valore medio metrica tenuta in considerazionei:");
		JLabel lab2 = new JLabel("Contatore aggiornamenti della topologia");
		row1.setLayout(new BoxLayout(row1, BoxLayout.Y_AXIS));
		// JLabel lab3 = new JLabel("asd");
		row1.add(lab1);
		row2.setLayout(new BoxLayout(row2, BoxLayout.Y_AXIS));
		row2.add(lab2);
		// row3.add(lab3);
		eastern_panel.add(row1);

		JPanel panel_1 = new JPanel();
		row1.add(panel_1);

		tf_valoremedio = new JTextField();
		panel_1.add(tf_valoremedio);
		tf_valoremedio.setEditable(false);
		tf_valoremedio.setText("0000");
		tf_valoremedio.setColumns(8);
		eastern_panel.add(row2);

		JPanel panel_2 = new JPanel();
		row2.add(panel_2);

		tf_contatoreAggiornamenti = new JTextField();
		panel_2.add(tf_contatoreAggiornamenti);
		tf_contatoreAggiornamenti.setText("0");
		tf_contatoreAggiornamenti.setEditable(false);
		tf_contatoreAggiornamenti.setColumns(10);
		eastern_panel.add(row3);
		row3.setLayout(new BoxLayout(row3, BoxLayout.Y_AXIS));

		JLabel lblOsservatorioConvergenzaA = new JLabel(
				"Osservatorio convergenza a regime");
		row3.add(lblOsservatorioConvergenzaA);

		JPanel panel = new JPanel();
		row3.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel panel_3 = new JPanel();
		panel.add(panel_3);

		JLabel lblUltimoValoreMedio = new JLabel("Ultimo valore medio");
		panel_3.add(lblUltimoValoreMedio);

		tf_ultimoValMedio = new JTextField();
		tf_ultimoValMedio.setEditable(false);
		panel_3.add(tf_ultimoValMedio);
		tf_ultimoValMedio.setColumns(10);

		JPanel panel_4 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
		panel.add(panel_4);

		JLabel lblValoreMedioAttuale = new JLabel(
				"Differenza tra valore medio precendente ed attuale");
		panel_4.add(lblValoreMedioAttuale);

		tf_ValMedioAttuale = new JTextField();
		tf_ValMedioAttuale.setEditable(false);
		panel_4.add(tf_ValMedioAttuale);
		tf_ValMedioAttuale.setColumns(10);
		myJFrame.getContentPane().add(eastern_panel, BorderLayout.EAST);

		myJFrame.setVisible(true);
	}

	public JTextField getTf_valoremedio() {
		return tf_valoremedio;
	}

	public void setTf_valoremedio(JTextField tf_valoremedio) {
		this.tf_valoremedio = tf_valoremedio;
	}

	public JTextField getTf_contatoreAggiornamenti() {
		return tf_contatoreAggiornamenti;
	}

	public void setTf_contatoreAggiornamenti(
			JTextField tf_contatoreAggiornamenti) {
		this.tf_contatoreAggiornamenti = tf_contatoreAggiornamenti;
	}

	public JTextField getTf_ultimoValMedio() {
		return tf_ultimoValMedio;
	}

	public void setTf_ultimoValMedio(JTextField tf_ultimoValMedio) {
		this.tf_ultimoValMedio = tf_ultimoValMedio;
	}

	public JTextField getTf_ValMedioAttuale() {
		return tf_ValMedioAttuale;
	}

	public void setTf_ValMedioAttuale(JTextField tf_ValMedioAttuale) {
		this.tf_ValMedioAttuale = tf_ValMedioAttuale;
	}

}
