package exercise;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import model_streamSim.Chunk;
import model_streamSim.DistributionGraph;
import model_streamSim.DistributionPeer;
import model_topoMan.Edge;
import model_topoMan.Network;
import model_topoMan.Node;
import model_topoMan.OverlayGraph;
import model_topoMan.Peer;
import model_topoMan.VirtualEdge;

public class StreamingSimul extends SwingWorker<Void, Void> {

	Network network;
	Integer chunk_number;
	Float chunksize;
	Map<Double, Float> uploadClasses2percentage;
	DistributionGraph distributionGraph;

	public StreamingSimul(Network network, Integer chunk_number,
			Float chunksize, Map<Double, Float> uploadClasses2percentage) {
		super();
		this.network = network;
		this.chunk_number = chunk_number;
		this.chunksize = chunksize;
		this.uploadClasses2percentage = uploadClasses2percentage;
		init();
	}

	private void init() {

		distributionGraph = new DistributionGraph();
		OverlayGraph streaming_graph = network.getOverlayGraph();

		for (Peer p : streaming_graph.getPeers()) {
			DistributionPeer dp = new DistributionPeer(p.getName(), new Double(
					1), distributionGraph);
			distributionGraph.getDpeers().add(dp);
		}

		// update the neighbours of every distrbitutionpeer
		for (Peer p : streaming_graph.getPeers()) {
			DistributionPeer retrievedDP = distributionGraph
					.getDistributionPeer(p.getName());
			for (Peer neigh : p.getNeighbours()) {
				DistributionPeer dp_neigh = distributionGraph
						.getDistributionPeer(neigh.getName());
				retrievedDP.getNeighbours().add(dp_neigh);
			}
		}

		// inizializzazione archi grafo distribuzione
		// for (VirtualEdge ve : streaming_graph.getLinks()) {
		// Node source = ve.getSource();
		// Node dest = ve.getDestination();
		// Edge e = new Edge(source.getName() + "<->" + dest.getName(),
		// source, dest, ve.getWeight());
		// distributionGraph.getEdges().add(e);
		// }
		Set<VirtualEdge> virtualedges = new HashSet<>();
		virtualedges.addAll(streaming_graph.getLinks());
		distributionGraph.setEdges(virtualedges);
		// ArrayList<Chunk> initial_buffer = getInitialBuffer();
		// distributionGraph.setStreaming_buffer(initial_buffer);
		List<Edge> underlay_edges = network.getUnderlayGraph().getEdges();
		for (Edge edge : underlay_edges) {
			distributionGraph.getEdge2TX_counter().put(edge, 0);
			distributionGraph.getEdge2Fail_TX_counter().put(edge, 0);
		}
		for (DistributionPeer dp : distributionGraph.getDpeers()) {
			List<Double> loss_list = new ArrayList<>();
			distributionGraph.getChunk_loss_ratio().put(dp, loss_list);
		}

	}

	void startSimulation() {
		final JProgressBar progressBar = new JProgressBar(0, distributionGraph
				.getDpeers().size());
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		// progressBar.setVisible(true);
		final JFrame frame = new JFrame("Streaming test");
		frame.add(progressBar);
		frame.setSize(700, 150);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		this.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// TODO Auto-generated method stub
				try {
					if ("progress" == evt.getPropertyName()) {
						int progress = (Integer) evt.getNewValue();
						progressBar.setValue(progress);
						// taskOutput.append(String.format(
						// "Completed %d%% of task.\n",
						// task.getProgress()));
					}
					switch ((StateValue) evt.getNewValue()) {
					case DONE:
						frame.dispatchEvent(new WindowEvent(frame,
								WindowEvent.WINDOW_CLOSING));
						break;
					case PENDING:
						break;
					case STARTED:
						break;
					default:
						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		});
		this.execute();
		return;

	}

	void runSimulation(DistributionPeer sorgente) {
		ArrayList<Chunk> initialBuffer = getInitialBuffer();
		sorgente.setBuffer(initialBuffer);
		sorgente.setflag_received_requests(true);
		distributionGraph.distribuisci(sorgente);
		distributionGraph.saveChunkLossStatistic(initialBuffer.size());
		distributionGraph.reset();
	}

	ArrayList<Chunk> getInitialBuffer() {
		ArrayList<Chunk> retval = new ArrayList<>();
		for (int i = 0; i < chunk_number; i++) {
			Chunk c = new Chunk(i, chunksize, i);
			retval.add(c);
		}
		return retval;
	}

	@Override
	protected Void doInBackground() throws Exception {
		Collections.sort(distributionGraph.getDpeers());
		int progress = 0;

		for (DistributionPeer source : distributionGraph.getDpeers()) {

			progress++;
			Double peersize = new Double(distributionGraph.getDpeers().size());
			Double num = (progress) / peersize * 100.0;
			setProgress(num.intValue());
			// JOptionPane.showConfirmDialog(
			// null,
			// "Vuoi lanciare uno streaming dalla sorgente: "
			// + source.getName() + "?");
			runSimulation(source);
			// JOptionPane.showConfirmDialog(
			// null,
			// "Fine dello streaming test dalla sorgente: "
			// + source.getName() + ". Continuare?");
		}
		return null;
	}

	@Override
	protected void done() {
		Toolkit.getDefaultToolkit().beep();

		JOptionPane.showMessageDialog(null, "Finito!");
		int response = JOptionPane.showConfirmDialog(null,
				"Vuoi salvare le statistiche relative al chunkloss?",
				"Mostrare statistiche", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.NO_OPTION) {
			// System.out.println("No button clicked");
		} else if (response == JOptionPane.YES_OPTION) {
			// System.out.println("Yes button clicked");
			saveStreamingChunkLossStatistic();
		}

		int response2 = JOptionPane
				.showConfirmDialog(
						null,
						"Vuoi salvare le statistiche relative al numero di trasmissioni per link?",
						"Mostrare statistiche", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
		if (response2 == JOptionPane.NO_OPTION) {
			// System.out.println("No button clicked");
		} else if (response2 == JOptionPane.YES_OPTION) {
			// System.out.println("Yes button clicked");
			saveTXstatstic();
		}

		int response3 = JOptionPane
				.showConfirmDialog(
						null,
						"Vuoi salvare le statistiche relative al numero di trasmissioni fallite?",
						"Mostrare statistiche", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
		if (response3 == JOptionPane.NO_OPTION) {
			// System.out.println("No button clicked");
		} else if (response3 == JOptionPane.YES_OPTION) {
			// System.out.println("Yes button clicked");
			saveFailTXstatstic();
		}

		distributionGraph.total_reset();
	}

	private void saveFailTXstatstic() {
		File selectedFile = null;
		JFileChooser fileDialog = new JFileChooser();
		int saveChoice = fileDialog.showSaveDialog(null);
		if (saveChoice == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileDialog.getSelectedFile();
		}
		if (selectedFile == null)
			return;

		try {

			File file = new File(selectedFile.getAbsolutePath() + ".txt");

			if (file.createNewFile()) {
				System.out.println("File is created!");
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(file.getAbsolutePath())));
				// codice per buttare fuori roba
				Map<Edge, Integer> edge2TXcount_map = distributionGraph
						.getEdge2Fail_TX_counter();
				for (Edge e : edge2TXcount_map.keySet()) {
					Node source = e.getSource();
					Node dest = e.getDestination();
					out.println(source.getName() + "<->" + dest.getName() + " "
							+ edge2TXcount_map.get(e));
				}
				out.close();
				// Desktop.getDesktop().open(file);
			} else {
				System.out.println("File already exists.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void saveTXstatstic() {
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

			File file = new File(selectedFile.getAbsolutePath() + ".txt");

			if (file.createNewFile()) {
				System.out.println("File is created!");
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(file.getAbsolutePath())));
				// codice per buttare fuori roba
				Map<Edge, Integer> edge2TXcount_map = distributionGraph
						.getEdge2TX_counter();
				for (Edge e : edge2TXcount_map.keySet()) {
					Node source = e.getSource();
					Node dest = e.getDestination();
					out.println(source.getName() + dest.getName() + " "
							+ edge2TXcount_map.get(e));
				}
				out.close();
				// Desktop.getDesktop().open(file);
			} else {
				System.out.println("File already exists.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void saveStreamingChunkLossStatistic() {
		File selectedFile = null;
		JFileChooser fileDialog = new JFileChooser();
		int saveChoice = fileDialog.showSaveDialog(null);
		if (saveChoice == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileDialog.getSelectedFile();
		}
		if (selectedFile == null)
			return;

		try {

			File file = new File(selectedFile.getAbsolutePath() + ".txt");

			if (file.createNewFile()) {
				System.out.println("File is created!");
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(file.getAbsolutePath())));
				// codice per buttare fuori roba
				Map<DistributionPeer, List<Double>> chunkloss_map = distributionGraph
						.getChunk_loss_ratio();
				for (DistributionPeer dp : distributionGraph.getDpeers()) {
					float total = 0;
					for (double n : chunkloss_map.get(dp)) {
						total += n;
					}
					double avg = total / distributionGraph.getDpeers().size();
					out.println(dp.getName() + " " + avg);
				}
				out.close();
				// Desktop.getDesktop().open(file);
			} else {
				System.out.println("File already exists.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
