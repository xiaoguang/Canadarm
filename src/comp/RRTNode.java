package comp;

import java.util.HashSet;
import java.util.Set;

public class RRTNode {

	public RRTNode parent;
	public RobotState rs;
	Set<RRTNode> children;

	@SuppressWarnings("unused")
	private RRTNode() {
	}

	public RRTNode(RobotState rs) {
		this.parent = null;
		this.rs = rs;
		this.children = new HashSet<RRTNode>();
	}

	public boolean addChildren(RRTNode child) {
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
