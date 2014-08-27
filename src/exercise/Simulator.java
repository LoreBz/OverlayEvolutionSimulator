package exercise;

import java.util.List;

import model_topoMan.Network;
import model_topoMan.OverlayGraph;
import model_topoMan.Peer;

public class Simulator {
	Network network;
	String metrica;

	public Simulator(Network network, String metrica) {
		super();
		this.network = network;
		this.metrica = metrica;
	}

	public void one_cicle_update() {
		OverlayGraph overlayGraph = network.getOverlayGraph();
		for (Peer p : overlayGraph.getPeers()) {
			List<Peer> newscastSample = overlayGraph.newscast_randomSample(p);
			p.updatePeer(newscastSample, metrica,network.getUnderlayGraph());
		}
		network.updateNetwork();

	}

}
