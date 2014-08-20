package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Node implements Comparable<Node> {
	String id;
	String name;
	Map<Node, Node> predecessors;
	Map<Node, Float> distance;

	// Set<Node> onehop_neighs;

	public Node(String id, String name) {
		this.id = id;
		this.name = name;
		this.predecessors = null;
		this.distance = null;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		// if (getClass() != obj.getClass())
		// return false;
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (id.equals(other.id))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	public List<Node> getOnehop_neighs(List<Edge> edges) {
		ArrayList<Node> neighbours = new ArrayList<>();
		for (Edge edge : edges) {
			if (edge.getSource().equals(this)) {
				neighbours.add(edge.getDestination());
			}
			if (edge.getDestination().equals(this)) {
				neighbours.add(edge.getSource());
			}
		}
		HashSet<Node> removeduplicate = new HashSet<>();
		removeduplicate.addAll(neighbours);
		neighbours.clear();
		neighbours.addAll(removeduplicate);
		return neighbours;
	}

	@Override
	public int compareTo(Node other) {
		// compareTo should return < 0 if this is supposed to be
		// less than other, > 0 if this is supposed to be greater than
		// other and 0 if they are supposed to be equal
		int this_val = Integer.parseInt(this.name);
		int other_val = Integer.parseInt(other.getName());
		if (this_val < other_val)
			return -1;
		else if (this_val > other_val)
			return 1;
		else
			return 0;
	}

	public Map<Node, Node> getPredecessors() {
		return predecessors;
	}

	public void setPredecessors(Map<Node, Node> predecessors) {
		this.predecessors = predecessors;
	}

	public Map<Node, Float> getDistance() {
		return distance;
	}

	public void setDistance(Map<Node, Float> distance) {
		this.distance = distance;
	}

}
