package model_streamSim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import model_topoMan.Edge;

public class DistributionGraph {

	List<DistributionPeer> dpeers;
	Set<Edge> edges;
	ArrayList<Chunk> streaming_buffer;

	public DistributionGraph() {

	}

	public Edge getEdge(String sourcename, String destname) {
		// not impl
		return null;
	}

	public Edge getEdge(String edgename) {
		// not impl
		return null;
	}

	public DistributionPeer getDistributionPeer(String name) {
		for (DistributionPeer dp : this.dpeers) {
			if (dp.getName() == name)
				return dp;
		}
		return null;
	}

	public Chunk getYoungestChunk() {
		Collections.sort(this.streaming_buffer);
		return this.streaming_buffer.get(this.streaming_buffer.size() - 1);
	}

	public List<DistributionPeer> getDpeers() {
		return dpeers;
	}

	public void setDpeers(List<DistributionPeer> dpeers) {
		this.dpeers = dpeers;
	}

	public Set<Edge> getEdges() {
		return edges;
	}

	public void setEdges(Set<Edge> edges) {
		this.edges = edges;
	}

}
