package model;

import java.util.List;

public class Peer extends Node {

	List<Peer> neighbours;
	String peer_id;

	public Peer(String id, String name, List<Peer> neighbours, String peer_id) {
		super(id, name);
		this.neighbours = neighbours;
		this.peer_id = peer_id;
	}

	public Peer(Node n, List<Peer> neighbours, String peer_id) {
		super(n.getId(), n.getName());
		this.neighbours = neighbours;
		this.peer_id = peer_id;
	}

	void updatePeer(List<Peer> newscastSample) {
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String retval = super.toString();
		retval += "\nneighbours: ( ";
		for (Peer n : this.neighbours) {
			retval += n.getName() + " ";
		}
		retval+=")\n";
		return retval;
	}

	public List<Peer> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(List<Peer> neighbours) {
		this.neighbours = neighbours;
	}

	public String getPeer_id() {
		return peer_id;
	}

	public void setPeer_id(String peer_id) {
		this.peer_id = peer_id;
	}
	
	

}
