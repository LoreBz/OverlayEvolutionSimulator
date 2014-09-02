package exercise;

import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import model_streamSim.Chunk;
import model_streamSim.DistributionGraph;
import model_streamSim.DistributionPeer;
import model_topoMan.Edge;
import model_topoMan.Network;
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
		sorgente.setBuffer(getInitialBuffer());
		sorgente.setflag_received_requests(true);
		distributionGraph.distribuisci(sorgente);
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
		// startButton.setEnabled(true);
		// setCursor(null); // turn off the wait cursor
		JOptionPane.showMessageDialog(null, "Finito!");
	}
}
