package exercise;

import java.util.Map;

import model_streamSim.DistributionGraph;
import model_streamSim.DistributionPeer;
import model_topoMan.Edge;
import model_topoMan.Network;
import model_topoMan.Node;
import model_topoMan.OverlayGraph;
import model_topoMan.Peer;
import model_topoMan.VirtualEdge;

public class StreamingSimul {

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

		DistributionGraph distributionGraph = new DistributionGraph();
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

		// inizializzazione archi grafo distribuzione (ma non dovrebbero
		// servire...)
		for (VirtualEdge ve : streaming_graph.getLinks()) {
			Node source = ve.getSource();
			Node dest = ve.getDestination();
			Edge e = new Edge(source.getName() + "<->" + dest.getName(),
					source, dest, ve.getWeight());
			distributionGraph.getEdges().add(e);
		}

	}

	void startSimulation() {

	}

}
