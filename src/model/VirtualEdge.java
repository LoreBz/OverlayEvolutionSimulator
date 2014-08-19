package model;

import java.util.List;

public class VirtualEdge extends Edge {

	List<Edge> crossedEdges;

	public VirtualEdge(String id, Node source, Node destination, int weight,
			List<Edge> crossedEdges) {
		super(id, source, destination, weight);
		this.crossedEdges = crossedEdges;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
