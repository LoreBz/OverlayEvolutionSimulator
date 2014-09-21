package model_topoMan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import MyUtil.DjkstraUtil;

public class UnderlayGraph {

	List<Node> nodes;
	List<Edge> edges;

	public UnderlayGraph(List<Node> nodes, List<Edge> edges) {
		super();
		this.nodes = nodes;
		this.edges = edges;
	}

	public UnderlayGraph(File file) {
		super();
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(file));

			while ((sCurrentLine = br.readLine()) != null) {
				// System.out.println(sCurrentLine);
				String[] result = sCurrentLine.split("\\s");
				String s1 = result[0];
				String s2 = result[1];
				String sw = result[2];

				Node n1 = new Node(s1, s1);
				Node n2 = new Node(s2, s2);
				Edge e = new Edge(s1 + s2, n1, n2, Float.parseFloat(sw));

				if (!nodes.contains(n1)) {
					nodes.add(n1);
				}
				if (!nodes.contains(n2)) {
					nodes.add(n2);
				}
				if (!edges.contains(e)) {
					edges.add(e);
				}

				// initializing onehopneighbourhood for each peer so that
				// graph-visiting algo will works
				// (olsr provides neigh sensing)
				// n1.getOnehop_neighs().add(n2);
				// n2.getOnehop_neighs().add(n1);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public boolean buildOLSRtables() {
		System.out.println("BUILD OLSR TABLES\n\n");
		for (Node n : nodes) {
			DjkstraUtil djkstraUtil = new DjkstraUtil();
			djkstraUtil.runDjkstra_OLSR(this, n);
		}
		System.out.println("END BUILD OLSR TABLES\n\n");
		return true;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	@Override
	public String toString() {
		String retval = "";
		retval += "\nUNDERLAY\nNodi:\n";
		for (Node n : nodes) {
			retval += n.getName() + " - ";
		}
		retval += "\nArchi:\n";
		for (Edge e : edges) {
			retval += "" + e + "\n";
		}
		return retval + "\n";
	}
	
	public List<Edge> retrievePath(String source_id, String dest_id) {
		// find the edgesource in the underlaygraph
		Node source = null;
		Node dest=null;
		for (Node n : this.getNodes()) {
			if (n.getName().equals(source_id)) {
				source = n;
			}
			if (n.getName().equals(dest_id)) {
				dest = n;
			}
			
		}
		
		ArrayList<Node> path = new ArrayList<Node>();
		Node step = dest;
		Map<Node, Node> predecessors = source.getPredecessors();
		// check if a path exists
		if (predecessors != null) {
			if (predecessors.get(step) == null) {
				return null;
			} else {
				path.add(step);
				while (predecessors.get(step) != null) {
					step = predecessors.get(step);
					path.add(step);
				}
				// Put it into the correct order
				Collections.reverse(path);
				ArrayList<Edge> edgepath = new ArrayList<>();
				for (int i = 1; i < path.size(); i++) {
					Edge e = getEdgeFromSourceAndDestination(path.get(i - 1),
							path.get(i), this.getEdges());
					edgepath.add(e);
				}
				return edgepath;
			}
		} else
			return null;

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
