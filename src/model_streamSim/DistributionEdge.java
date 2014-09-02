package model_streamSim;

import java.util.List;

import model_topoMan.Edge;
import model_topoMan.Node;
import model_topoMan.VirtualEdge;

public class DistributionEdge {
	Node source;
	Node destination;
	String id;
	Float weight;
	List<Edge> path;

	public DistributionEdge(VirtualEdge ve) {
		this.source = ve.getSource();
		this.destination = ve.getDestination();
		this.weight = ve.getWeight();
		this.path = ve.getPath();
	}
}
