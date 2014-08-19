package model;

public class Edge {
	String id;
	Node source;
	Node destination;
	Float weight;

	public Edge(String id, Node source, Node destination, Float weight) {
		this.id = id;
		this.source = source;
		this.destination = destination;
		this.weight = weight;
	}

	public String getId() {
		return id;
	}

	public Node getDestination() {
		return destination;
	}

	public Node getSource() {
		return source;
	}

	public float getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return source + " " + destination + ": (" + weight + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (this.source == other.source
				&& this.destination == other.destination
				&& this.weight == other.weight)
			return true;
		if (this.source == other.destination
				&& this.destination == other.source
				&& this.weight == other.weight)
			return true;
		return false;
	}

}