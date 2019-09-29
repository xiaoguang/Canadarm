package comp;

import java.util.HashSet;
import java.util.Set;

import utils.GlobalCfg;

public class RapidExploringRandomTree {

	RRTNode root;
	Set<RRTNode> sampled;

	@SuppressWarnings("unused")
	private RapidExploringRandomTree() {
	}

	public RapidExploringRandomTree(RRTNode root) {
		this.root = root;
		this.sampled = new HashSet<RRTNode>();
		this.sampled.add(this.root);
	}

	public int size() {
		return this.sampled.size();
	}

	public boolean addNode(RRTNode node) {
		// return false, if already explored
		if (this.sampled.contains(node))
			return false;

		// find min node in the tree
		double min = Double.MAX_VALUE;
		RRTNode minNode = null;
		for (RRTNode s : this.sampled) {
			double local = s.rs.distance(node.rs);
			if (local < min) {
				min = local;
				minNode = s;
			}
		}

		if (minNode == null)
			return false;

		if (min < GlobalCfg.rrtMinRadianDistance * node.rs.segments.size())
			return false;

		if (min > GlobalCfg.rrtMaxRadianDistance * node.rs.segments.size()) {
			if (!minNode.rs.findSampleWithin(node.rs))
				return false;
		}

		if (!minNode.addChildren(node))
			System.exit(-1);
		this.sampled.add(node);

		return true;
	}

}
