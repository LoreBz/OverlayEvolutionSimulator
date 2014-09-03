package model_streamSim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model_topoMan.Edge;
import model_topoMan.Node;
import model_topoMan.VirtualEdge;

public class DistributionGraph {

	List<DistributionPeer> dpeers;
	Set<VirtualEdge> edges;
	Map<Edge, Integer> edge2TX_counter;
	Map<Edge, Integer> edge2Fail_TX_counter;
	Map<DistributionPeer, List<Double>> chunk_loss_ratio;

	// ArrayList<Chunk> streaming_buffer;

	public DistributionGraph() {
		this.dpeers = new ArrayList<>();
		this.edges = new HashSet<>();
		this.edge2Fail_TX_counter = new HashMap<>();
		this.edge2TX_counter = new HashMap<>();
		this.chunk_loss_ratio = new HashMap<>();
	}

	public VirtualEdge getEdge(String sourcename, String destname) {
		for (VirtualEdge e : this.getEdges()) {
			Node source = e.getSource();
			Node dest = e.getDestination();
			if ((source.getName() == sourcename && dest.getName() == destname)
					|| (dest.getName() == sourcename && source.getName() == destname)) {
				return e;
			}
		}
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

	// public Chunk getYoungestChunk() {
	// Collections.sort(this.streaming_buffer);
	// if (!this.streaming_buffer.isEmpty())
	// return this.streaming_buffer.get(this.streaming_buffer.size() - 1);
	// else
	// return null;
	// }

	public List<DistributionPeer> getDpeers() {
		return dpeers;
	}

	public void setDpeers(List<DistributionPeer> dpeers) {
		this.dpeers = dpeers;
	}

	public Set<VirtualEdge> getEdges() {
		return edges;
	}

	public void setEdges(Set<VirtualEdge> edges) {
		this.edges = edges;
	}

	public void distribuisci(DistributionPeer sorgente) {
		DistributionPeer.systemTime = new Long(0);
		// System.out.println("Cominciata la trasmissione dal peer_sorgete: "
		// + sorgente.getName());
		boolean completed = false;
		while (!completed) {

			// System.out.println("SENDING OFFERS...time="
			// + DistributionPeer.systemTime);
			for (DistributionPeer dp : this.getDpeers()) {
				dp.sendOffers();
			}
			// System.out.println("\nSCHEDULING REQUESTS");
			for (DistributionPeer dp : this.getDpeers()) {
				dp.scheduleRequests();
			}
			// System.out.println("\nSENDING REQUESTS");
			for (DistributionPeer dp : this.getDpeers()) {
				dp.sendRequests();
			}
			// System.out.println("\nTRANSMITTING CHUNKS");
			for (DistributionPeer dp : this.getDpeers()) {
				dp.transmit_requested_chunks();
			}
			// System.out.println("\nUPDATING BUFFERS");
			for (DistributionPeer dp : this.getDpeers()) {
				dp.updateBuffer();
			}
			// System.out.println("\nRESETTING");
			for (DistributionPeer dp : this.getDpeers()) {
				dp.reset();
			}

			// diciamo che abbiamo completato quando nessuno ha ricevuto
			// richieste
			// basta un or true per avere true e proseguire con il while
			boolean completed_update = false;
			for (DistributionPeer dp : this.getDpeers()) {

				completed_update = completed_update
						|| dp.isflag_received_requests();

			}
			completed = !completed_update;
			// System.out.println("\nRESTART...checking buffers\n");
			// if (completed) {
			// System.out.println("Buffer4Peer:");
			// for (DistributionPeer dp : this.getDpeers()) {
			// dp.printBuffer();
			// }
			// }

			DistributionPeer.systemTime++;
		}
		System.out.println("Terminata la trasmissione dal peer: "
				+ sorgente.getName());

	}

	// public ArrayList<Chunk> getStreaming_buffer() {
	// return streaming_buffer;
	// }
	//
	// public void setStreaming_buffer(ArrayList<Chunk> streaming_buffer) {
	// this.streaming_buffer = streaming_buffer;
	// }

	public void reset() {
		for (DistributionPeer dp : this.getDpeers()) {
			dp.getReceived_offers().clear();
			dp.getReceived_requests().clear();
			dp.getReceived_chunks().clear();
			dp.getRequests_queue().clear();
			dp.getTransmission_queue().clear();
			dp.getBuffer().clear();
			dp.setflag_received_requests(false);
			// aggiorniamo il tempo dei DP
			DistributionPeer.systemTime = new Long(0);
		}

	}

	public void total_reset() {
		for (DistributionPeer dp : this.getDpeers()) {
			dp.getReceived_offers().clear();
			dp.getReceived_requests().clear();
			dp.getReceived_chunks().clear();
			dp.getRequests_queue().clear();
			dp.getTransmission_queue().clear();
			dp.getBuffer().clear();
			dp.setflag_received_requests(false);
			// aggiorniamo il tempo dei DP
			DistributionPeer.systemTime = new Long(0);
		}
		for (Edge e : this.edge2TX_counter.keySet()) {
			this.edge2TX_counter.put(e, 0);
		}
		for (Edge e : this.edge2Fail_TX_counter.keySet()) {
			this.edge2Fail_TX_counter.put(e, 0);
		}
		for (DistributionPeer dp : chunk_loss_ratio.keySet()) {
			this.chunk_loss_ratio.get(dp).clear();
		}
	}

	public Map<Edge, Integer> getEdge2TX_counter() {
		return edge2TX_counter;
	}

	public void setEdge2TX_counter(Map<Edge, Integer> edge2tx_counter) {
		edge2TX_counter = edge2tx_counter;
	}

	public Map<Edge, Integer> getEdge2Fail_TX_counter() {
		return edge2Fail_TX_counter;
	}

	public void setEdge2Fail_TX_counter(Map<Edge, Integer> edge2Fail_TX_counter) {
		this.edge2Fail_TX_counter = edge2Fail_TX_counter;
	}

	public void saveChunkLossStatistic(int total_number_of_chunks) {
		for (DistributionPeer dp : this.getDpeers()) {
			int number_chunks_received = dp.getBuffer().size();
			int number_of_lost_chunks = total_number_of_chunks
					- number_chunks_received;
			Double lossratio = (new Double(number_of_lost_chunks))
					/ (new Double(total_number_of_chunks));
			this.chunk_loss_ratio.get(dp).add(lossratio);

		}
	}

	public Map<DistributionPeer, List<Double>> getChunk_loss_ratio() {
		return chunk_loss_ratio;
	}

	public void setChunk_loss_ratio(
			Map<DistributionPeer, List<Double>> chunk_loss_ratio) {
		this.chunk_loss_ratio = chunk_loss_ratio;
	}

	public void initOutNeighbours() {
		for (DistributionPeer dp : this.getDpeers()) {
			for (DistributionPeer n : dp.getNeighbours()) {
				n.getOut_neighbours().add(dp);
			}
		}
	}

}
