package exercise;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import model_topoMan.Edge;
import model_topoMan.Network;
import model_topoMan.Node;
import model_topoMan.OverlayGraph;
import model_topoMan.Peer;
import model_topoMan.UnderlayGraph;
import model_topoMan.VirtualEdge;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.View;

import MyUtil.Results;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class Test {
	JTextField tf_valoremedio;
	JTextField tf_contatoreAggiornamenti;
	JTextField tf_ultimoValMedio;
	JTextField tf_diff_att_prec;
	JPanel central_panel;
	Network network;
	public static String metrica;
	JButton button_update;
	JButton button_saveOverlay;
	JButton button_startOverlayStreamingEvaluation;
	Graph underlaygraph;
	Graph overlaygraph;
	JButton btnSalvaStatsticheEvoluzione;
	JButton btn_restartRandomOverlay;
	JButton btn_40cicliDifila;
	private JTextField txf_hop;
	private JTextField txf_ampp;
	private JTextField txf_etx;
	private JLabel lab_metrica;

	public Test() {

	}

	public static void main(String[] args) {
		final Results results = new Results();
		final Test test = new Test();
		test.setLookAndFeel();
		File graphFile = test.importGraphFile();

		final UnderlayGraph underlayGraph = new UnderlayGraph(graphFile);
		underlayGraph.buildOLSRtables();

		int number = test.getNeighSize();
		final OverlayGraph overlayGraph = new OverlayGraph(null, null, number);
		overlayGraph.randomInit(underlayGraph);

		test.network = new Network(underlayGraph, overlayGraph, null);
		test.network.updateNetwork();

		test.displayall(test.network);

		// richiesta metrica da utilizzare
		Test.metrica = test.getMetricFromUser();
		test.button_update.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				OverlayEvSimul s = new OverlayEvSimul(test.network,
						Test.metrica);
				s.one_cicle_update();

				test.update_graphics_components(test.network, Test.metrica,
						results);
			}
		});
		test.button_saveOverlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(test.network.getOverlayGraph());
				test.saveOverlayGraphonFile(test.network.getOverlayGraph());
				test.saveStatstic();
			}
		});

		test.button_startOverlayStreamingEvaluation
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						Integer chunk_number;
						Float chunksize;
						Map<Double, Float> uploadClasses2percentage = null;

						String chunk_number_string = JOptionPane
								.showInputDialog(null,
										"Quanti chunk vuoi far trasmettere alla sorgente dello streaming?");
						chunk_number = Integer.parseInt(chunk_number_string);

						chunksize = new Float(0.01);

						StreamingSimul simul = new StreamingSimul(test.network,
								chunk_number, chunksize,
								uploadClasses2percentage);

						simul.startSimulation();

					}
				});
		test.btnSalvaStatsticheEvoluzione
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						results.printEvolutionResults();
						results.reset();
					}
				});
		test.btn_restartRandomOverlay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				OverlayGraph og = test.network.getOverlayGraph();
				for (Peer p : og.getPeers()) {
					p.setNeighbours(og.newscast_randomSample(p));
				}
				test.network.updateNetwork();
				test.update_graphics_components(test.network, Test.metrica,
						results);
				String nuova_metrica = test.getMetricFromUser();
				test.setMetrica(nuova_metrica);
				results.reset();
				test.tf_contatoreAggiornamenti.setText("" + 0);

			}
		});

		test.btn_40cicliDifila.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				final OverlayEvSimul s = new OverlayEvSimul(test.network,
						Test.metrica);
				SwingWorker<Void, Void> work = new SwingWorker<Void, Void>() {

					@Override
					protected Void doInBackground() throws Exception {
						for (int i = 0; i < 40; i++) {
							System.out
									.println("Ciclo aggiornamento rapido no: "
											+ (i + 1));
							s.one_cicle_update();
						}
						return null;
					}

					@Override
					protected void done() {
						// TODO Auto-generated method stub
						test.update_graphics_components(test.network,
								Test.metrica, results);
					}
				};
				work.execute();

			}
		});

	}

	int getNeighSize() {
		String neighs_size = JOptionPane
				.showInputDialog(
						null,
						"Dimensione vicinato dei peer?\n(Attenzione: con grandi numeri non funziona per grafi molto piccoli)");
		if (neighs_size == null) {
			System.exit(0);
		}

		return Integer.parseInt(neighs_size);
	}

	void saveStatstic() {

		int[] degreedistribution = Toolkit.degreeDistribution(overlaygraph);
		Double avgDegree = Toolkit.averageDegree(overlaygraph);
		Double densitiy = Toolkit.density(overlaygraph);
		Double diameter = Toolkit.diameter(overlaygraph);
		// double[] clusteringCoefficients = Toolkit
		// .clusteringCoefficients(overlaygraph);
		Double averageClusteringCoefficient = Toolkit
				.averageClusteringCoefficient(overlaygraph);

		// chidere all'utente se visualizzare le statistiche
		int response = JOptionPane.showConfirmDialog(null,
				"Vuoi vedere le statistiche del grafo di overlay?",
				"Mostrare statistiche", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.NO_OPTION) {
			// System.out.println("No button clicked");
		} else if (response == JOptionPane.YES_OPTION) {
			// System.out.println("Yes button clicked");
			try {

				// create a temp file

				final File temp = File.createTempFile(
						"temp-file-name_statistics", ".pdf");
				temp.deleteOnExit();
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							Files.delete(temp.toPath());
							// System.out.println("deleted file at "+path);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});

				Document document = new Document();
				PdfWriter.getInstance(document, new FileOutputStream(temp));
				document.open();

				PdfPTable table = new PdfPTable(2);

				// t.setBorderColor(BaseColor.GRAY);
				// t.setPadding(4);
				// t.setSpacing(4);
				// t.setBorderWidth(1);

				PdfPCell c1 = new PdfPCell(new Phrase("Variabile"));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);

				c1 = new PdfPCell(new Phrase("Valore"));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);

				table.setHeaderRows(1);
				switch (metrica) {
				case "HopCount":
					table.addCell("avg(" + metrica + ") [#hop]");
					table.addCell("" + tf_valoremedio.getText());
					break;
				case "Djkstra-ETX":
					table.addCell("avg(" + metrica + ") [etx]");
					table.addCell("" + tf_valoremedio.getText());
					break;
				case "AvoidMultiPeerPath":
					table.addCell("avg(" + metrica + ") [#Peer_on_path]");
					table.addCell("" + tf_valoremedio.getText());
					break;
				default:
					break;
				}

				table.addCell("nodes number[#]");
				table.addCell("" + overlaygraph.getNodeCount());

				table.addCell("edges number[#]");
				table.addCell("" + overlaygraph.getEdgeCount());

				table.addCell("avgDegree[#]");
				table.addCell("" + avgDegree);

				table.addCell("densitiy\n(the number of links in the graph divided\nby the total number of possible links)[#]");
				table.addCell("" + densitiy);

				table.addCell("diameter (*considering non weighted edges)[#]");
				table.addCell("" + diameter);

				table.addCell("averageClusteringCoefficient[#]");
				table.addCell("" + averageClusteringCoefficient);

				Paragraph tablepara = new Paragraph();
				tablepara.add(table);
				document.add(tablepara);

				Paragraph degmapPara = new Paragraph();
				PdfPTable table1 = new PdfPTable(2);

				// t.setBorderColor(BaseColor.GRAY);
				// t.setPadding(4);
				// t.setSpacing(4);
				// t.setBorderWidth(1);

				PdfPCell cell1 = new PdfPCell(new Phrase("Grado"));
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell1);

				cell1 = new PdfPCell(
						new Phrase("Numero di nodi con quel grado"));
				cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table1.addCell(cell1);

				table1.setHeaderRows(1);
				for (int i = 0; i < degreedistribution.length; i++) {
					if (degreedistribution[i] != 0) {
						table1.addCell("" + i);
						table1.addCell("" + degreedistribution[i]);
					}

				}
				degmapPara.add(table1);
				degmapPara.setSpacingBefore(50);
				document.add(degmapPara);
				document.close();
				// System.out.println("Temp file : " + temp.getAbsolutePath());
				// PrintWriter out = new PrintWriter(new BufferedWriter(
				// new FileWriter(temp.getAbsolutePath())));
				// out.println("avereageqlcs " + avgDegree);
				// out.close();
				Desktop.getDesktop().open(temp);

			} catch (IOException | DocumentException e) {

				e.printStackTrace();

			}
		} else if (response == JOptionPane.CLOSED_OPTION) {
			// System.out.println("JOptionPane closed");
		}

	}

	protected void saveOverlayGraphonFile(OverlayGraph overlayGraph) {
		// TODO Auto-generated method stub
		File selectedFile = null;
		JFileChooser fileDialog = new JFileChooser();
		int saveChoice = fileDialog.showSaveDialog(null);
		if (saveChoice == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileDialog.getSelectedFile();
		}
		if (selectedFile == null)
			return;

		try {

			File file = new File(selectedFile.getAbsolutePath() + ".edges");

			if (file.createNewFile()) {
				System.out.println("File is created!");
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(file.getAbsolutePath())));
				for (VirtualEdge ve : overlayGraph.getLinks()) {
					out.println("" + ve.getSource().getName() + " "
							+ ve.getDestination().getName() + " "
							+ ve.getWeight());
				}
				out.close();
			} else {
				System.out.println("File already exists.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void update_graphics_components(Network n, String metrica,
			Results results) {
		Integer num = Integer.parseInt(tf_contatoreAggiornamenti.getText());
		System.out
				.println("Esecuzione ciclo di aggiornamento no: " + (num + 1));
		tf_contatoreAggiornamenti.setText("" + (num + 1));
		// aggiornare i campi valore medio, ultimo valore medio e differenza tra
		// i due
		Float valore_medio_attuale;
		Float ultimo_valore_medio;
		Float differenza_attuale_precendente;
		Float total = new Float(0);

		// calcolo valore medio attuale
		List<Float> ranks = new ArrayList<>();
		switch (metrica) {
		case "HopCount":
			for (VirtualEdge ve : n.getOverlayGraph().getLinks()) {
				// ve.setPath(ve.retrievePath(n.getUnderlayGraph()));
				ranks.add(new Float(ve.getPath().size()));
			}
			break;
		case "Djkstra-ETX":
			for (VirtualEdge ve : n.getOverlayGraph().getLinks()) {
				// ve.setPath(ve.retrievePath(n.getUnderlayGraph()));
				ranks.add(new Float(ve.getWeight()));
			}
			break;
		case "AvoidMultiPeerPath":
			for (VirtualEdge ve : n.getOverlayGraph().getLinks()) {
				// ve.setPath(ve.retrievePath(n.getUnderlayGraph()));
				Set<Node> nodi_attraversati = new HashSet<>();
				for (Edge e : ve.getPath()) {
					nodi_attraversati.add(e.getSource());
					nodi_attraversati.add(e.getDestination());
				}
				// per ogni virtualink, nodiattrvarsati comprenderà ve.source e
				// ve.dest che sono dei peer! per questo faccio patrire il
				// contatore da - 2
				Float numeropeercoinvolti = new Float(-2);
				for (Node node : nodi_attraversati) {
					if (n.getOverlayGraph().getPeers().contains(node)) {
						numeropeercoinvolti++;
					}
				}
				ranks.add(numeropeercoinvolti);
			}
			break;
		default:
			break;
		}
		for (Float rank : ranks) {
			total += rank;
		}
		valore_medio_attuale = total / (n.getOverlayGraph().getLinks().size());

		// aggiornamento label ecc della grafica
		ultimo_valore_medio = Float.parseFloat(tf_valoremedio.getText());

		tf_valoremedio.setText("" + valore_medio_attuale);
		tf_ultimoValMedio.setText("" + ultimo_valore_medio);
		differenza_attuale_precendente = Math.abs(ultimo_valore_medio
				- valore_medio_attuale);
		tf_diff_att_prec.setText("" + differenza_attuale_precendente);

		// vediamo se da qui posso modificare la grafica dei grafi
		overlaygraph.getEdgeSet().clear();
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

		// aggiorna Results per vedere come evolvono i tre parametri nel corso
		// dell'evoluzione dell'overlay
		for (int i = 0; i < 3; i++) {
			List<Float> values = new ArrayList<>();
			if (i == 0) {
				// hop
				for (VirtualEdge ve : n.getOverlayGraph().getLinks()) {
					// ve.setPath(ve.retrievePath(n.getUnderlayGraph()));
					values.add(new Float(ve.getPath().size()));
				}
				float hoptot = 0;
				for (Float f : values) {
					hoptot += f;
				}
				float hopavg = hoptot / n.getOverlayGraph().getLinks().size();
				results.getHop_cicles2overallvalue().put(num + 1, hopavg);
				txf_hop.setText("" + hopavg);
				values.clear();
			}
			if (i == 1) {
				// djketx
				for (VirtualEdge ve : n.getOverlayGraph().getLinks()) {
					// ve.setPath(ve.retrievePath(n.getUnderlayGraph()));
					values.add(new Float(ve.getWeight()));
				}
				float djktot = 0;
				for (Float f : values) {
					djktot += f;
				}
				float djkavg = djktot / n.getOverlayGraph().getLinks().size();
				results.getDjketx_cicles2overallvalue().put(num + 1, djkavg);
				txf_etx.setText("" + djkavg);
				values.clear();
			}
			if (i == 2) {
				// ampp
				for (VirtualEdge ve : n.getOverlayGraph().getLinks()) {
					// ve.setPath(ve.retrievePath(n.getUnderlayGraph()));
					Set<Node> nodi_attraversati = new HashSet<>();
					for (Edge e : ve.getPath()) {
						nodi_attraversati.add(e.getSource());
						nodi_attraversati.add(e.getDestination());
					}
					// per ogni virtualink, nodiattrvarsati comprenderà
					// ve.source e
					// ve.dest che sono dei peer! per questo faccio patrire il
					// contatore da - 2
					Float numeropeercoinvolti = new Float(-2);
					for (Node node : nodi_attraversati) {
						if (n.getOverlayGraph().getPeers().contains(node)) {
							numeropeercoinvolti++;
						}
					}
					values.add(numeropeercoinvolti);
				}
				float ampptot = 0;
				for (Float f : values) {
					ampptot += f;
				}
				float amppavg = ampptot / n.getOverlayGraph().getLinks().size();
				results.getAmpp_cicles2overallvalue().put(num + 1, amppavg);
				txf_ampp.setText("" + amppavg);
				values.clear();
			}
		}
		// switch (metrica) {
		// case "HopCount":
		// results.getHop_cicles2overallvalue().put(num + 1,
		// valore_medio_attuale);
		// break;
		// case "Djkstra-ETX":
		// results.getDjketx_cicles2overallvalue().put(num + 1,
		// valore_medio_attuale);
		// case "AvoidMultiPeerPath":
		// results.getAmpp_cicles2overallvalue().put(num + 1,
		// valore_medio_attuale);
		// break;
		// default:
		// break;
		// }

	}

	public String getMetricFromUser() {
		Object[] selectionValues = { "HopCount", "Djkstra-ETX",
				"AvoidMultiPeerPath" };
		String initialSelection = "Djkstra-ETX";
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

	public File importGraphFile() {
		final JFileChooser fc = new JFileChooser();
		// FileNameExtensionFilter filter = new
		// FileNameExtensionFilter("edges");
		// fc.setFileFilter(filter);
		File retval = null;
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: "
					+ fc.getSelectedFile().getName());
			retval = fc.getSelectedFile();
			return retval;
		}
		if (retval == null) {
			JOptionPane
					.showMessageDialog(null,
							"Chiusura applicazione: impossibile procedere senza selezionare un file edges");
			System.exit(0);
		}
		return null;
	}

	void displayall(Network network) {
		// far partire la grafica con bottoni e disegnini dei grafi!

		// creazione vista grafo underlay
		underlaygraph = new SingleGraph("Graph");
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
			underlaygraph.addEdge(source + "<->" + destination, source,
					destination, false);
			underlaygraph.getEdge(source + "<->" + destination).setAttribute(
					"weight", edge.getWeight());
			underlaygraph.getEdge(source + "<->" + destination).setAttribute(
					"ui.label", edge.getWeight());
		}
		// prepare the peer-nodes to be displayed as peer
		for (Peer peer : network.getOverlayGraph().getPeers()) {
			underlaygraph.getNode(peer.getName()).setAttribute("isPeer", true);
			underlaygraph.getNode(peer.getName()).addAttribute("ui.class",
					"important");
		}

		// creazione vista grafo overlay

		overlaygraph = new SingleGraph("Graph");
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

		central_panel = new JPanel();
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
		button_saveOverlay = new JButton("Save overlay graph");
		button_startOverlayStreamingEvaluation = new JButton("Streaming Test");
		btnSalvaStatsticheEvoluzione = new JButton(
				"Salva statistiche evoluzione overlay");
		btn_restartRandomOverlay = new JButton("Restart Random Overlay");
		btn_40cicliDifila = new JButton(
				"40 cicli di aggiornamento in un colpo!");

		southern_panel.add(button_update);
		southern_panel.add(button_saveOverlay);
		southern_panel.add(button_startOverlayStreamingEvaluation);
		southern_panel.add(btnSalvaStatsticheEvoluzione);
		southern_panel.add(btn_restartRandomOverlay);
		southern_panel.add(btn_40cicliDifila);
		myJFrame.getContentPane().add(southern_panel, BorderLayout.SOUTH);

		// listeners for buttons

		// displaying label with statistics
		JPanel eastern_panel = new JPanel();
		eastern_panel.setLayout(new GridLayout(3, 1));
		// eastern_panel.setSize(new Dimension(100, 720));
		JPanel row1 = new JPanel();
		JPanel row2 = new JPanel();
		JPanel row3 = new JPanel();
		JLabel lab2 = new JLabel("Contatore aggiornamenti della topologia");
		row1.setLayout(new BoxLayout(row1, BoxLayout.Y_AXIS));
		row2.setLayout(new BoxLayout(row2, BoxLayout.Y_AXIS));
		row2.add(lab2);
		// row3.add(lab3);
		eastern_panel.add(row1);

		JPanel panel_1 = new JPanel();
		row1.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

		JPanel panel_8 = new JPanel();
		panel_1.add(panel_8);
		lab_metrica = new JLabel(
				"Valore medio metrica tenuta in considerazione:");
		panel_8.add(lab_metrica);

		tf_valoremedio = new JTextField();
		panel_8.add(tf_valoremedio);
		tf_valoremedio.setEditable(false);
		tf_valoremedio.setText("0");
		tf_valoremedio.setColumns(8);

		JPanel panel_5 = new JPanel();
		panel_1.add(panel_5);

		JLabel lblNewLabel = new JLabel("ETX");
		panel_5.add(lblNewLabel);

		txf_etx = new JTextField();
		txf_etx.setText("0");
		panel_5.add(txf_etx);
		txf_etx.setColumns(10);

		JPanel panel_6 = new JPanel();
		panel_1.add(panel_6);

		JLabel lblNewLabel_1 = new JLabel("HOP");
		panel_6.add(lblNewLabel_1);

		txf_hop = new JTextField();
		txf_hop.setText("0");
		panel_6.add(txf_hop);
		txf_hop.setColumns(10);

		JPanel panel_7 = new JPanel();
		panel_1.add(panel_7);

		JLabel lblNewLabel_2 = new JLabel("AMPP");
		panel_7.add(lblNewLabel_2);

		txf_ampp = new JTextField();
		txf_ampp.setText("0");
		panel_7.add(txf_ampp);
		txf_ampp.setColumns(10);
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
		// FlowLayout flowLayout = (FlowLayout) panel_4.getLayout();
		panel.add(panel_4);

		JLabel lblValoreMedioAttuale = new JLabel(
				"Differenza tra valore medio precendente ed attuale");
		panel_4.add(lblValoreMedioAttuale);

		tf_diff_att_prec = new JTextField();
		tf_diff_att_prec.setEditable(false);
		panel_4.add(tf_diff_att_prec);
		tf_diff_att_prec.setColumns(10);
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
		return tf_diff_att_prec;
	}

	public void setTf_ValMedioAttuale(JTextField tf_ValMedioAttuale) {
		this.tf_diff_att_prec = tf_ValMedioAttuale;
	}

	public String getMetrica() {
		return metrica;
	}

	public void setMetrica(String metrica) {
		Test.metrica = metrica;
	}

}
