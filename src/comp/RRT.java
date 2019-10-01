package comp;

import java.util.HashSet;
import java.util.Set;

import utils.GlobalCfg;

public class RRT {

	Node root;
	Set<Node> sampled;
	Planner planner;

	@SuppressWarnings("unused")
	private RRT() {
	}

	public RRT(Node root) {
		this.root = root;
		this.sampled = new HashSet<Node>();
		this.sampled.add(this.root);
		this.planner = new Planner();
	}

	public int size() {
		return this.sampled.size();
	}

	public Node addNode(RobotState state) {
		// return false, if already explored
		if (this.sampled.contains(new Node(state)))
			return null;

		// find min node in the tree
		double min = Double.MAX_VALUE;
		Node minNode = null;
		for (Node node : this.sampled) {
			double local = node.rs.distance(state);
			if (local < min) {
				min = local;
				minNode = node;
			}
		}

		// step is too small
		if (min < GlobalCfg.epsilon * minNode.rs.segments.size())
			return null;

		if (min > GlobalCfg.rrtMaxRadianDistance * minNode.rs.segments.size()) {
			if (!minNode.rs.findSampleWithin(state))
				return null;
		}

		if (planner.generateSteps(minNode.rs, state) == null)
			return null;

		Node newNode = minNode.addChildren(state);
		this.sampled.add(newNode);

		return newNode;
	}

}

class Node {

	Node parent;
	RobotState rs;

	@SuppressWarnings("unused")
	private Node() {
	}

	public Node(RobotState rs) {
		this.parent = null;
		this.rs = rs;
	}

	public Node addChildren(RobotState child) {
		Node node = new Node(child);
		node.parent = this;
		return node;
	}

	@Override
	public int hashCode() {
		return rs.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object == this)
			return true;
		if (this.getClass() != object.getClass())
			return false;
		Node n = (Node) object;
		if (!this.rs.equals(n.rs))
			return false;

		return true;
	}

}
