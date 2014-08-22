package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

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

	public void updatePeer(List<Peer> newscastSample, String metrica,
			UnderlayGraph underlayGraph) {
		System.out.println("Updating peer: " + this.name);

		List<Peer> new_selected_neighs = new ArrayList<>();
		// marcare tutti i peer noti come non classificati
		Map<Peer, Float> rankedPeers = new TreeMap<>();
		for (Peer p : newscastSample) {
			rankedPeers.put(p, null);
		}
		for (Peer p : this.neighbours) {
			rankedPeers.put(p, null);
		}

		// in base alla metrica classificarli e selezionare i migliori
		int neigh_size = this.neighbours.size();
		switch (metrica) {
		case "HopCount":
			// classificazione
			for (Peer peer : rankedPeers.keySet()) {
				VirtualEdge ve = new VirtualEdge(this.name + peer.getName(),
						this, peer, null, null);
				ve.setPath(ve.retrievePath(underlayGraph));
				rankedPeers.put(peer, new Float(ve.getPath().size()));
			}
			// stampa del ranking
			for (Entry<Peer, Float> entry : rankedPeers.entrySet()) {
				System.out.println("Peer: " + entry.getKey().getName()
						+ ", rank=" + entry.getValue() + " - ");
			}
			System.out.println("\n");
			// selezione
			while (new_selected_neighs.size() < neigh_size) {
				Peer selectedP = getMinimumFromMap(rankedPeers);
				if (selectedP != null) {
					rankedPeers.remove(selectedP);
					new_selected_neighs.add(selectedP);
				}
			}

			break;
		case "Djkstra-ETX":
			for (Peer peer : rankedPeers.keySet()) {
				VirtualEdge ve = new VirtualEdge(this.name + peer.getName(),
						this, peer, null, null);
				ve.setPath(ve.retrievePath(underlayGraph));
				rankedPeers.put(peer, ve.getWeight());
			}
			// stampa del ranking
			for (Entry<Peer, Float> entry : rankedPeers.entrySet()) {
				System.out.println("Peer: " + entry.getKey().getName()
						+ ", rank=" + entry.getValue() + " - ");
			}
			System.out.println("\n");
			// selezione
			while (new_selected_neighs.size() < neigh_size) {
				Peer selectedP = getMinimumFromMap(rankedPeers);
				if (selectedP != null) {
					rankedPeers.remove(selectedP);
					new_selected_neighs.add(selectedP);
				}
			}
			break;
		case "AvoidMultiPeerPath":
			for (Peer peer : rankedPeers.keySet()) {
				VirtualEdge ve = new VirtualEdge(this.name + peer.getName(),
						this, peer, null, null);
				ve.setPath(ve.retrievePath(underlayGraph));

				Set<Node> nodi_attraversati = new HashSet<>();
				for (Edge e : ve.getPath()) {
					nodi_attraversati.add(e.getSource());
					nodi_attraversati.add(e.getDestination());
				}
				// per ogni virtualink, nodiattrvarsati comprenderà ve.source e
				// ve.dest che sono dei peer! per questo faccio patrire il
				// contatore da - 2
				Float numeropeercoinvolti = new Float(-2);
				for (Node node : nodi_attraversati) {
					if (this.neighbours.contains(node)
							|| newscastSample.contains(node)
							|| this.equals(node)) {
						numeropeercoinvolti++;
					}
				}
				rankedPeers.put(peer, numeropeercoinvolti);
			}
			// stampa del ranking
			for (Entry<Peer, Float> entry : rankedPeers.entrySet()) {
				System.out.println("Peer: " + entry.getKey().getName()
						+ ", rank=" + entry.getValue() + " - ");
			}
			System.out.println("\n");
			// selezione
			while (new_selected_neighs.size() < neigh_size) {
				Peer selectedP = getMinimumFromMap(rankedPeers);
				if (selectedP != null) {
					rankedPeers.remove(selectedP);
					new_selected_neighs.add(selectedP);
				}
			}
			break;
		default:
			break;
		}

		// assegnamento nuovo vicinato al peer
		this.neighbours.clear();
		this.neighbours = new_selected_neighs;
		System.out.println("Selected neighs: ");
		for (Peer n : this.neighbours) {
			System.out.println(n.getName() + " ");
		}
		System.out.println("\n end peer update\n");

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String retval = super.toString();
		retval += "\nneighbours: ( ";
		for (Peer n : this.neighbours) {
			retval += n.getName() + " ";
		}
		retval += ")\n";
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

	private Peer getMinimumFromMap(Map<Peer, Float> map) {
		if (map.isEmpty())
			return null;
		Entry<Peer, Float> min = null;
		for (Entry<Peer, Float> entry : map.entrySet()) {
			if (min == null || min.getValue() > entry.getValue()) {
				min = entry;
			}
		}
		return min.getKey();
	}

}
