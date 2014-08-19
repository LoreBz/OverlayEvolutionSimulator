package model;

import java.util.Set;

public class Node implements Comparable<Node> {
	final private String id;
	final private String name;
	Set<Node> onehop_neighs;

	public Node(String id, String name, Set<Node> onehop_neighs) {
		this.id = id;
		this.name = name;
		this.onehop_neighs = onehop_neighs;
	}

	public Node(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Set<Node> getOnehop_neighs() {
		return onehop_neighs;
	}

	public void setOnehop_neighs(Set<Node> onehop_neighs) {
		this.onehop_neighs = onehop_neighs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (id.equals(other.id))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Node other) {
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

}
