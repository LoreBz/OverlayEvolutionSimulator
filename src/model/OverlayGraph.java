package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OverlayGraph {

	List<Peer> peers;
	List<VirtualEdge> links;
	final int NEWSCASTSAMPLE_SIZE = 2;

	public OverlayGraph(List<Peer> peers, List<VirtualEdge> links) {
		super();
		this.peers = peers;
		this.links = links;
	}

	public void randomInit(UnderlayGraph ug) {
		this.peers = new ArrayList<>();
		this.links = new ArrayList<>();
		int hostnumber = ug.getNodes().size();
		List<Node> nodes = ug.getNodes();
		int successful_peers_addition = 0;

		// getting hostnumber/2 nodes and initializing them as peers
		while (successful_peers_addition < (hostnumber / 2)) {
			int randomInt = new Random().nextInt(hostnumber);
			Node n = nodes.get(randomInt);
			Peer p = new Peer(n, null, n.getId());
			if (!peers.contains(p)) {
				peers.add(p);
				successful_peers_addition++;
			}
		}// end while

		// generate a random neighbourhood for each peer
		for (Peer p : peers) {
			List<Peer> neighbours = newscast_randomSample(p);
			p.neighbours = neighbours;
		}

		// the generation of virtualedges is demanded to Network.updateNetwork()

	}

	public List<Peer> newscast_randomSample(Peer caller) {
		List<Peer> randomSample = new ArrayList<>();
		int addition_counter = 0;

		while (addition_counter <= NEWSCASTSAMPLE_SIZE) {
			int randomInt = new Random().nextInt(peers.size());
			Peer p = peers.get(randomInt);
			if (!randomSample.contains(p) && !caller.equals(p)) {
				randomSample.add(p);
				addition_counter++;
			}
		}// end while

		return randomSample;
	}

	public void save_on_file(File f) {
	}

	public List<Peer> getPeers() {
		return peers;
	}

	public void setPeers(List<Peer> peers) {
		this.peers = peers;
	}

	public List<VirtualEdge> getLinks() {
		return links;
	}

	public void setLinks(List<VirtualEdge> links) {
		this.links = links;
	}

	@Override
	public String toString() {
		String retval = "";
		retval += "\nOVERLAY\nPeer:\n";
		Collections.sort(peers);
		for (Peer p : peers) {
			retval += p.getName() + " - ";
		}
		retval += "\nArchi:\n";
		for (VirtualEdge v : links) {
			retval += "" + v.toString() + "\n";
		}
		return retval + "\n";
	}

}
