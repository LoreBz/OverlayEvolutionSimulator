package exercise;

import java.util.List;

import model.Network;
import model.OverlayGraph;
import model.Peer;

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
