package model_topoMan;

public class Edge implements Comparable<Edge>{
	String id;
	Node source;
	Node destination;
	Float weight;
	Integer tx_tries_counter;

	public Edge(String id, Node source, Node destination, Float weight) {
		this.id = id;
		this.source = source;
		this.destination = destination;
		this.weight = weight;
		this.tx_tries_counter = 0;
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

	public Float getWeight() {
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
		// if (getClass() != obj.getClass())
		// return false;
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

	public Integer getTx_tries_counter() {
		return tx_tries_counter;
	}

	public void setTx_tries_counter(Integer tx_tries_counter) {
		this.tx_tries_counter = tx_tries_counter;
	}

	public void setWeight(Float weight) {
		this.weight = weight;
	}

	public void resetEdge() {
		this.tx_tries_counter = 0;
	}

	@Override
	public int compareTo(Edge other) {
		// compareTo should return < 0 if this is supposed to be
		// less than other, > 0 if this is supposed to be greater than
		// other and 0 if they are supposed to be equal
//		int this_val = Integer.parseInt(this.source.getName());
//		int other_val = Integer.parseInt(other.getSource().getName());
		
		if (this.weight < other.weight)
			return -1;
		else if (this.weight > other.weight)
			return 1;
		else
			return 0;
	}

}