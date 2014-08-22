package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.graphstream.ui.swingViewer.View;

public class Network {

	UnderlayGraph underlayGraph;
	OverlayGraph overlayGraph;
	View graphview;

	public Network(UnderlayGraph underlayGraph, OverlayGraph overlayGraph,
			View graphview) {
		super();
		this.underlayGraph = underlayGraph;
		this.overlayGraph = overlayGraph;
		this.graphview = graphview;
	}

	public Network() {
	}

	public boolean updateNetwork() {
		// checkUnderlay();
		// chechOverlay();

		//rimuove i virtualedge attuali
		overlayGraph.getLinks().clear();

		//inizializza nuovi virtualedge in base al vicinato attuale dei peer
		List<VirtualEdge> actual_virtualedges = new ArrayList<>();
		for (Peer p : overlayGraph.getPeers()) {
			for (Peer neigh : p.getNeighbours()) {
				VirtualEdge ve = new VirtualEdge(p.getName() + neigh.getName(),
						p, neigh, null, null);
				if (!actual_virtualedges.contains(ve)) {
					actual_virtualedges.add(ve);
				}
			}
		}

		// finish initializing the actual_virtualedges initializing their path
		// and their weight
		for (VirtualEdge ve : actual_virtualedges) {
			
			ve.setPath(ve.retrievePath(underlayGraph));
		}

		//aggiorna l'overlaygrpah
		overlayGraph.setLinks(actual_virtualedges);

		return true;
	}

	void init(File f) {
	}

	public UnderlayGraph getUnderlayGraph() {
		return underlayGraph;
	}

	public void setUnderlayGraph(UnderlayGraph underlayGraph) {
		this.underlayGraph = underlayGraph;
	}

	public OverlayGraph getOverlayGraph() {
		return overlayGraph;
	}

	public void setOverlayGraph(OverlayGraph overlayGraph) {
		this.overlayGraph = overlayGraph;
	}

	public View getGraphview() {
		return graphview;
	}

	public void setGraphview(View graphview) {
		this.graphview = graphview;
	}
	
	

}
