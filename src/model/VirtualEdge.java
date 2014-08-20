package model;

import java.util.List;

public class VirtualEdge extends Edge {

	List<Edge> crossedEdges;

	public VirtualEdge(String id, Node source, Node destination, Float weight,
			List<Edge> crossedEdges) {
		super(id, source, destination, weight);
		this.crossedEdges = crossedEdges;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return source.getName() + " " + destination.getName() + ": (" + weight + ")";
	}
	
	@Override
	public Float getWeight() {
		// TODO Auto-generated method stub
		//return super.getWeight();
		//devo ritornare la somma dei pesi dei link sul percorso
		return null;
	}

}
