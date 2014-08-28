package model_streamSim;

public class Transmission {
	Chunk payload;
	DistributionPeer destination_peer;

	public Transmission(Chunk payload, DistributionPeer destination_peer) {
		super();
		this.payload = payload;
		this.destination_peer = destination_peer;
	}

	public Chunk getPayload() {
		return payload;
	}

	public void setPayload(Chunk payload) {
		this.payload = payload;
	}

	public DistributionPeer getDestination_peer() {
		return destination_peer;
	}

	public void setDestination_peer(DistributionPeer destination_peer) {
		this.destination_peer = destination_peer;
	}

}
