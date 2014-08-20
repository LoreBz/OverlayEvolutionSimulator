package MyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Edge;
import model.Node;
import model.UnderlayGraph;

public class DjkstraUtil {
	Set<Node> settledNodes;
	Set<Node> unSettledNodes;
	Map<Node, Node> predecessors;
	Map<Node, Float> distance;
	List<Node> nodes;
	List<Edge> edges;

	public DjkstraUtil() {
		// TODO Auto-generated constructor stub
	}

	public void runDjkstra_OLSR(UnderlayGraph graph, Node root) {
		System.out.println("DjkstraExecution for node: " + root.getName() + "");
		settledNodes = new HashSet<>(); // nodi scoperti
		unSettledNodes = new HashSet<>(); // nodi da esaminare
		predecessors = new HashMap<>(); // albero dei padri
		distance = new HashMap<>(); // vettore delle distanze

		nodes = graph.getNodes(); // handle locale ai nodi del grafo
		edges = graph.getEdges(); // handle locale agli archi del
									// grafo

		// inizializzazione distanze
		for (Node n : nodes) {
			distance.put(n, new Float(1000000.0));
		}
		distance.put(root, new Float(0));
		unSettledNodes.add(root);

		// DJKSTRA ALGO!
		while (!unSettledNodes.isEmpty()) {
			Node node = getclosest(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);
		}// end while
		root.setDistance(distance);
		root.setPredecessors(predecessors);
		System.out
				.println("Fine Djkstra per il nodo: " + root.getName() + "\n");
	}

	private void findMinimalDistances(Node node) {
		List<Node> adjacentNodes = getNeighbors(node);// recupera i nodi
														// adiacenti non ancora
														// scoperti del nodo
														// "node"
		for (Node target : adjacentNodes) {
			Edge node_target_edge = getEdgeFromSourceAndDestination(node,
					target, edges);
			if (distance.get(target) > distance.get(node)// occio qua che sto
															// facendo somma tra
															// float e
															// Integer...
					+ node_target_edge.getWeight()) {
				distance.put(target,
						distance.get(node) + node_target_edge.getWeight());
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}

	}

	private List<Node> getNeighbors(Node node) {
		List<Node> neighbors = new ArrayList<Node>();
		for (Node neigh : node.getOnehop_neighs(edges)) {
			if (!settledNodes.contains(neigh)) {
				neighbors.add(neigh);
			}
		}
		return neighbors;
	}

	private Node getclosest(Set<Node> nodes) {
		Node minimum = null;
		for (Node n : nodes) {
			if (minimum == null) {
				minimum = n;
			} else {
				if (distance.get(n) < distance.get(minimum)) {
					minimum = n;
				}
			}
		}
		return minimum;
	}

	private Edge getEdgeFromSourceAndDestination(Node source, Node destination,
			List<Edge> edges) {
		for (Edge edge : edges) {
			Node edgesource = edge.getSource();
			Node edgedestination = edge.getDestination();
			// link bidirezionali non so mai se li salvo in direzione
			// source->dest o dest->source XD
			if (edgesource.equals(source)
					&& edgedestination.equals(destination)) {
				return edge;
			}
			if (edgesource.equals(destination)
					&& edgedestination.equals(source)) {
				return edge;
			}
		}
		return null;
	}
}
