package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VirtualEdge extends Edge {

	List<Edge> path;

	public VirtualEdge(String id, Node source, Node destination, Float weight,
			List<Edge> crossedEdges) {
		super(id, source, destination, weight);
		this.path = crossedEdges;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String retval = "Path: ";
		for (Edge e : this.path) {
			retval += "(" + e.getSource() + "-" + e.getDestination() + ")"
					+ " ->";
		}
		retval += "\n";
		return retval;
	}

	@Override
	public Float getWeight() {
		// TODO Auto-generated method stub
		// return super.getWeight();
		// devo ritornare la somma dei pesi dei link sul percorso
		Float retval = new Float(0);
		if (this.path != null && !this.path.isEmpty()) {
			for (Edge e : this.path) {
				retval += e.getWeight();
			}
			return retval;
		} else
			return null;
	}

	public List<Edge> retrievePath(UnderlayGraph ug) {
		// find the edgesource in the underlaygraph
		Node source = null;
		for (Node n : ug.getNodes()) {
			if (n.equals(this.source)) {
				source = n;
			}
		}
		ArrayList<Node> path = new ArrayList<Node>();
		Node step = this.destination;
		Map<Node, Node> predecessors = source.getPredecessors();
		// check if a path exists
		if (predecessors != null) {
			if (predecessors.get(step) == null) {
				return null;
			} else {
				path.add(step);
				while (predecessors.get(step) != null) {
					step = predecessors.get(step);
					path.add(step);
				}
				// Put it into the correct order
				Collections.reverse(path);
				ArrayList<Edge> edgepath = new ArrayList<>();
				for (int i = 1; i < path.size(); i++) {
					Edge e = getEdgeFromSourceAndDestination(path.get(i - 1),
							path.get(i), ug.getEdges());
					edgepath.add(e);
				}
				return edgepath;
			}
		} else
			return null;

	}

	public List<Edge> getPath() {
		return path;
	}

	public void setPath(List<Edge> path) {
		this.path = path;
	}

	private Edge getEdgeFromSourceAndDestination(Node source, Node destination,
			List<Edge> edges) {
		for (Edge edge : edges) {
			Node edgesource = edge.getSource();
			Node edgedestination = edge.getDestination();
			// link bidirezionali non so mai se li salvo in direzione
			// source->dest o dest->source XD
			if (edgesource.equals(source)
					&& edgedestination.equals(destination)) {
				return edge;
			}
			if (edgesource.equals(destination)
					&& edgedestination.equals(source)) {
				return edge;
			}
		}
		return null;
	}

}
