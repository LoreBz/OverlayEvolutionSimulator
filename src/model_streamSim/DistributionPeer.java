package model_streamSim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import model_topoMan.Edge;

public class DistributionPeer implements Comparable<DistributionPeer> {

	static Long systemTime = new Long(0);
	private String name;
	private List<DistributionPeer> neighbours;
	private boolean flag_received_requests = false;
	private ArrayList<Chunk> buffer;
	private Double uploadBandwidht;
	private Map<Chunk, List<DistributionPeer>> received_offers;
	private Map<DistributionPeer, Chunk> requests_queue;
	private Map<Chunk, List<DistributionPeer>> received_requests;
	private Map<Chunk, List<DistributionPeer>> transmission_queue;
	private ArrayList<Chunk> received_chunks;
	private DistributionGraph known_topology_graph;

	public DistributionPeer() {
		// TODO Auto-generated constructor stub
	}

	public DistributionPeer(String name, Double uploadBandwidht,
			DistributionGraph known_topology_graph) {
		super();
		this.name = name;
		this.uploadBandwidht = uploadBandwidht;
		this.known_topology_graph = known_topology_graph;
		// questi li inizializzo vuoti a mano non si sa mai
		this.neighbours = new ArrayList<>();
		this.flag_received_requests = false;
		this.buffer = new ArrayList<>();
		this.received_offers = new HashMap<>();
		this.requests_queue = new HashMap<>();
		this.received_requests = new HashMap<>();
		this.transmission_queue = new HashMap<>();
		this.received_chunks = new ArrayList<>();
	}

	public DistributionPeer(String name, List<DistributionPeer> neighbours,
			boolean flag_received_requests, ArrayList<Chunk> buffer,
			Double uploadBandwidht, DistributionGraph known_topology_graph) {
		super();
		this.name = name;
		this.neighbours = neighbours;
		this.flag_received_requests = flag_received_requests;
		this.buffer = buffer;
		this.uploadBandwidht = uploadBandwidht;
		this.received_offers = new HashMap<>();
		this.requests_queue = new HashMap<>();
		this.received_requests = new HashMap<>();
		this.transmission_queue = new HashMap<>();
		this.received_chunks = new ArrayList<>();
		this.known_topology_graph = known_topology_graph;
	}

	public void sendOffers() {
		// invia ad ogni vicino i chunk più recenti e un descriptor del
		// mittente...già che ci siamo gli mando il mittente tanto siamo in un
		// simulatore eheh
		ArrayList<Chunk> offers_window = new ArrayList<>();
		for (Chunk chunk : this.buffer) {
			if (chunk.getGeneration_time() <= DistributionPeer.systemTime
					&& chunk.getGeneration_time() >= (DistributionPeer.systemTime - 20)) {
				offers_window.add(chunk);
			}
		}

		if (!offers_window.isEmpty()) {
			Map<Chunk, List<DistributionPeer>> local_received_offers;
			for (DistributionPeer neigh : this.neighbours) {
				local_received_offers = neigh.getReceived_offers();
				for (Chunk chunk : offers_window) {
					List<DistributionPeer> updated_local_providers = local_received_offers
							.get(chunk);
					if (updated_local_providers != null) {
						updated_local_providers.add(this);
					} else {
						updated_local_providers = new ArrayList<>();
						updated_local_providers.add(this);
					}
//					System.out.println("OFFERTA: da" + this.getName() + " a "
//							+ neigh.getName() + "; chunk #"
//							+ chunk.getChunk_Seq_number());
					local_received_offers.put(chunk, updated_local_providers);
					// neigh.setReceived_offers(local_received_offers);
				}
			}
		}

	}

	public void scheduleRequests() {
		// scorrendo received_offers imposta correttamente la request_queue con
		// criterio latest useful

		if (!this.received_offers.isEmpty()) {

			// System.out.println(this.getName() + " schedula le richieste...");
			// annotiamoci chi sono i peer che si sono offerti come provider per
			// chidergli solo un chunk alla volta
			Set<DistributionPeer> providers = new HashSet<>();
			for (Entry<Chunk, List<DistributionPeer>> entry : this.received_offers
					.entrySet()) {
				List<DistributionPeer> entry_providers = entry.getValue();
				for (DistributionPeer dp : entry_providers) {
					providers.add(dp);
				}
			}
			// ora ordiniamo i chunk dal più recente al più vecchio e poi
			// cominciamo
			// a fare le request
			Set<Chunk> set_offered_chunks = this.received_offers.keySet();
			ArrayList<Chunk> offered_chunks = new ArrayList<>();
			offered_chunks.addAll(set_offered_chunks);
			Collections.sort(offered_chunks);
			Collections.reverse(offered_chunks);

			// per ogni chunk offerto che ci manca prendiamo il provider più
			// desiderabile ancora
			// disponibile e prepariamo una richiesta
			offered_chunks.removeAll(this.buffer);
			this.requests_queue.clear();
			for (Chunk chunk : offered_chunks) {
				List<DistributionPeer> chunk_providers = this.received_offers
						.get(chunk);
				boolean flag = false;
				// occio che sono fuso in testa e non riesco a impostare una
				// condizione while. Finchè la lista non è vuota oppure non
				// ho
				// positivamente aggiunto una richiesta
				while (!flag && !chunk_providers.isEmpty()) {
					DistributionPeer best_peer = getMostDesiderablePeer(chunk_providers);
					if (providers.contains(best_peer)) {
						requests_queue.put(best_peer, chunk);
						providers.remove(best_peer);
						flag = true;
//						System.out.println("RICHIESTA PIANIFICATA: "
//								+ this.getName() + " chiede a "
//								+ best_peer.getName() + " chunk #"
//								+ chunk.getChunk_Seq_number());
					}
				}
			}
		} else {
			this.setflag_received_requests(false);
		}

	}

	public void sendRequests() {
		// consuma la coda di richieste inviando le richieste ai peer che si
		// sono offerti di collaborare
		if (this.requests_queue == null)
			return;
		// finchè ci sono richieste
		if (!this.requests_queue.isEmpty()) {
			for (Entry<DistributionPeer, Chunk> entry : this.requests_queue
					.entrySet()) {
				// prendi il peer a cui inoltrare la richiesta e il chunk da
				// richiedere
				DistributionPeer selected_peer = entry.getKey();
				Chunk requested_chunk = entry.getValue();

				// aggiungi questa richiesta alle richieste ricevute del
				// selected_peer
				List<DistributionPeer> chunk_requesters = selected_peer
						.getReceived_requests().get(requested_chunk);

				if (chunk_requesters == null) {
					chunk_requesters = new ArrayList<>();
				}
				chunk_requesters.add(this);
				selected_peer.getReceived_requests().put(requested_chunk,
						chunk_requesters);
//				System.out.println("RICHIESTA INVIATA: da" + this.getName()
//						+ " a " + selected_peer.getName() + " chunk# "
//						+ requested_chunk.getChunk_Seq_number());

			}
		}
	}

	public void transmit_requested_chunks() {
		if (!received_requests.isEmpty()) {
			this.setflag_received_requests(true);
		} else {
			this.setflag_received_requests(false);
		}
		Double residual_upBand = this.getUploadBandwidht();
		// occio condizione difficile di un while
		while (residual_upBand > 0) {
			// consuma le richieste ricevute in ordine di chunk
			// estrai una richiesta;
			Transmission transmission = popRequest();
			// se poprequest mi ritorna null vuol dire che non ci sono
			// trasmissioni da fare
			if (transmission == null)
				return;
			// se c'è una trasmissione da fare calcola la banda richiesta per
			// effettuarla
			Chunk chunkToTX = transmission.getPayload();
			Edge TX_link = this.known_topology_graph.getEdge(this.name,
					transmission.getDestination_peer().getName());
			Float requestedBand = TX_link.getWeight();
			requestedBand *= chunkToTX.getChunk_size();

			// se hai banda residua sufficiente fai la trasmissione e decrementa
			// la banda
			if (residual_upBand > requestedBand) {
				// effettua la trasmissione
				DistributionPeer receiver = transmission.getDestination_peer();
				receiver.getReceived_chunks().add(chunkToTX);
				// e decrementa la banda residua
				residual_upBand -= requestedBand;
//				System.out.println("TRASMISSIONE: da " + this.getName() + " a "
//						+ receiver.getName() + " chunk# "
//						+ chunkToTX.getChunk_Seq_number());
			} else {
				// altrimenti...o usciamo oppure controlliamo quanta banda ci è
				// rimasta...se ce n'è ancora un po' almeno per provare una
				// trasmissione su un link molto buono andiamo ancora avanti
				// sennò
				// usciamo del tutto
				// JOptionPane
				// .showConfirmDialog(
				// null,
				// "Qui siamo nel caso in cui abbiamo poca banda per trasmettere il dato chunk ma non abbiamo finito di trasmettere tutto");
				// if (residual_upBand < 3.0)
				return;
			}

		}
	}

	private Transmission popRequest() {
		if (!this.received_requests.isEmpty()) {
			// trasmettiamo in ordine di chunk più utile (contrario dell'ordine
			// naturale dei chunk)
			Set<Chunk> chunks = this.received_requests.keySet();
			ArrayList<Chunk> sorted_chunks = new ArrayList<>();
			sorted_chunks.addAll(chunks);
			Collections.sort(sorted_chunks);
			Collections.reverse(sorted_chunks);
			// prendiamo il primo chunk per recuperare i suoi richidenti
			Chunk veryFirstChunk = sorted_chunks.get(0);
			List<DistributionPeer> requesters = this.received_requests
					.get(veryFirstChunk);
			// estraiamo il primo richiedente e aggiorniamo la mappa delle
			// richieste
			DistributionPeer popDP = requesters.get(0);
			requesters.remove(popDP);
			if (requesters.isEmpty())
				this.received_requests.remove(veryFirstChunk);
			else
				this.received_requests.put(veryFirstChunk, requesters);
			Transmission t = new Transmission(veryFirstChunk, popDP);
			return t;

		}

		return null;
	}

	public void updateBuffer() {
		// controlla i received_chunks e aggiungili al buffer
		if (this.received_chunks.isEmpty()) {
			return;
		} else {
			//System.out.println(this.getName() + " ha ricevuto chunks!");
			for (Chunk c : this.received_chunks) {
				this.buffer.add(c);
				//System.out.println(c.getChunk_Seq_number() + ", ");
			}
			this.received_chunks.clear();
		}
	}

	public void reset() {
		// azzera le struttre dati per ricominciare il ciclo:
		// offri-seleziona-trasmetti-aggiorna
		this.received_offers.clear();
		this.received_requests.clear();
		this.received_chunks.clear();
		this.requests_queue.clear();
		this.transmission_queue.clear();
		// Chunk youngestchunk = getYoungestChunk();
		// if (youngestchunk == known_topology_graph.getYoungestChunk())
		// this.flag_received_requests = true;
		// else
		// this.flag_received_requests = false;
//		System.out.println("RESET: Peer " + this.getName()
//				+ " ha ricevuto richieste in questo ciclo?="
//				+ this.isflag_received_requests());
	}

	// Chunk getYoungestChunk() {
	// Chunk youngest_c = null;
	// Collections.sort(this.buffer);
	// if (!this.buffer.isEmpty())
	// youngest_c = this.buffer.get(this.buffer.size() - 1);
	// return youngest_c;
	// }

	DistributionPeer getMostDesiderablePeer(List<DistributionPeer> list) {
		DistributionPeer maxdes = null;
		for (DistributionPeer dp : list) {
			if (maxdes == null
					|| maxdes.getUploadBandwidht() < dp.getUploadBandwidht())
				maxdes = dp;
		}
		return maxdes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DistributionPeer> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(List<DistributionPeer> neighbours) {
		this.neighbours = neighbours;
	}

	public boolean isflag_received_requests() {
		return flag_received_requests;
	}

	public void setflag_received_requests(boolean flag_received_requests) {
		this.flag_received_requests = flag_received_requests;
	}

	public ArrayList<Chunk> getBuffer() {
		return buffer;
	}

	public void setBuffer(ArrayList<Chunk> buffer) {
		this.buffer = buffer;
	}

	public Double getUploadBandwidht() {
		return uploadBandwidht;
	}

	public void setUploadBandwidht(Double uploadBandwidht) {
		this.uploadBandwidht = uploadBandwidht;
	}

	public Map<Chunk, List<DistributionPeer>> getReceived_offers() {
		return received_offers;
	}

	public void setReceived_offers(
			Map<Chunk, List<DistributionPeer>> received_offers) {
		this.received_offers = received_offers;
	}

	public Map<DistributionPeer, Chunk> getRequests_queue() {
		return requests_queue;
	}

	public void setRequests_queue(Map<DistributionPeer, Chunk> requests_queue) {
		this.requests_queue = requests_queue;
	}

	public Map<Chunk, List<DistributionPeer>> getReceived_requests() {
		return received_requests;
	}

	public void setReceived_requests(
			Map<Chunk, List<DistributionPeer>> received_requests) {
		this.received_requests = received_requests;
	}

	public Map<Chunk, List<DistributionPeer>> getTransmission_queue() {
		return transmission_queue;
	}

	public void setTransmission_queue(
			Map<Chunk, List<DistributionPeer>> transmission_queue) {
		this.transmission_queue = transmission_queue;
	}

	public ArrayList<Chunk> getReceived_chunks() {
		return received_chunks;
	}

	public void setReceived_chunks(ArrayList<Chunk> received_chunks) {
		this.received_chunks = received_chunks;
	}

	@Override
	public int compareTo(DistributionPeer other) {
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

	public void printBuffer() {
		Collections.sort(this.getBuffer());
		System.out.println(this.getName() + ":");
		for (Chunk c : this.getBuffer()) {
			System.out.print("" + c + ",");
		}
		System.out.println("");
	}

}
