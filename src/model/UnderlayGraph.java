package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
				//(olsr provides neigh sensing)
				n1.getOnehop_neighs().add(n2);
				n2.getOnehop_neighs().add(n1);

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
		retval += "\nNodi:\n";
		for (Node n : nodes) {
			retval += n.getName() + " - ";
		}
		retval += "\nArchi:\n";
		for (Edge e : edges) {
			retval += "" + e + "\n";
		}
		return retval + "\n";
	}
}
