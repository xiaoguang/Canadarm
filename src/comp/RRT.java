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

	public Node addNode(Node node) {
		// return false, if already explored
		if (this.sampled.contains(node))
			return null;

		// find min node in the tree
		double min = Double.MAX_VALUE;
		Node minNode = null;
		for (Node s : this.sampled) {
			double local = s.rs.distance(node.rs);
			if (local < min) {
				min = local;
				minNode = s;
			}
		}

		if (minNode == null)
			return null;

		if (min < GlobalCfg.epsilon * node.rs.segments.size())
			return null;

		if (min > GlobalCfg.rrtMaxRadianDistance * node.rs.segments.size()) {
			if (!minNode.rs.findSampleWithin(node.rs))
				return null;
		}

		if (planner.generateSteps(minNode.rs, node.rs) == null)
			return null;

		if (!minNode.addChildren(node))
			System.exit(-1);
		this.sampled.add(node);

		return node;
	}

}

class Node {

	Node parent;
	RobotState rs;
	Set<Node> children;

	@SuppressWarnings("unused")
	private Node() {
	}

	public Node(RobotState rs) {
		this.parent = null;
		this.rs = rs;
		this.children = new HashSet<Node>();
	}

	public boolean addChildren(Node child) {
		if (!this.children.add(child))
			return false;
		child.parent = this;
		return true;
	}

	@Override
	public int hashCode() {
		return rs.hashCode();
	}

}
