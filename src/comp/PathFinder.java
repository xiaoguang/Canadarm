package comp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import utils.GlbCfg;

public class PathFinder {

	private Board board;
	private Planner planner;

	@SuppressWarnings("unused")
	private PathFinder() {
	}

	public PathFinder(Board board) {
		this.board = board;
		this.planner = new Planner();
	}

	public List<RobotState> findPath() {
		Node connect2A = null;
		Node connect2B = null;

		List<RobotState> towardsA = null;
		List<RobotState> towardsB = null;
		List<RobotState> steps = null;

		ConnectedPath found = this.findConnector(this.board.initRobotState,
				this.board.goalRobotState);

		if (found.connected) {
			towardsA = new ArrayList<RobotState>();
			towardsB = new ArrayList<RobotState>();

			connect2A = found.connect2A;
			while (connect2A != null) {
				towardsA.add(connect2A.rs);
				connect2A = connect2A.parent;
			}

			connect2B = found.connect2B.parent;
			while (connect2B != null) {
				towardsB.add(connect2B.rs);
				connect2B = connect2B.parent;
			}

			if (towardsA.get(towardsA.size() - 1)
					.equals(board.initRobotState)) {
				steps = towardsA;
				Collections.reverse(steps);
				steps.addAll(towardsB);
			} else {
				steps = towardsB;
				Collections.reverse(steps);
				steps.addAll(towardsA);
			}

		}

		return steps;
	}

	private void connect(RRT rrtA, RRT rrtB, ConnectedPath conn) {

		boolean growTree = false;

		// build rrtFromA
		while (!growTree) {
			Node sampleFromA = rrtA
					.addNode(planner.randomSampling(rrtA.root.rs));
			if (sampleFromA == null)
				continue;

			growTree = true;
			Node minNode = null;

			for (Node goal : rrtB.sampled) {
				double dist = RobotUtils.distance(goal.rs, sampleFromA.rs);
				if (dist > GlbCfg.rrtMaxRadianDistance
						* sampleFromA.rs.segments.size())
					continue;

				minNode = goal;
				List<RobotState> steps = planner.generateSteps(sampleFromA.rs,
						minNode.rs);
				if (steps == null)
					continue;

				conn.connect2A = sampleFromA;
				conn.connect2B = minNode.addChildren(sampleFromA.rs.clone());
				conn.connected = true;
				break; // break out of for loop
			}
		}

	}

	private ConnectedPath findConnector(RobotState initState,
			RobotState goalState) {

		RRT rrtFromInit = new RRT(new Node(initState));
		RRT rrtFromGoal = new RRT(new Node(goalState));
		RRT rrtTemp = null;

		Node connect2I = null;
		Node connect2G = null;
		Boolean connected = false;
		ConnectedPath conn = new ConnectedPath(connect2I, connect2G, connected);

		while (!conn.connected && rrtFromInit.size() < GlbCfg.maxNumberOfSamples
				&& rrtFromGoal.size() < GlbCfg.maxNumberOfSamples) {
			System.out.println("Tree Size: " + rrtFromInit.size());
			System.out.println("Tree Size: " + rrtFromGoal.size());

			this.connect(rrtFromInit, rrtFromGoal, conn);
			rrtTemp = rrtFromInit;
			rrtFromInit = rrtFromGoal;
			rrtFromGoal = rrtTemp;
		}

		return conn;
	}

	/*-
	private List<Board> planTransition() {
		this.board.goalRobotState
	}
	*/

}

class ConnectedPath {

	Node connect2A;
	Node connect2B;
	boolean connected;

	@SuppressWarnings("unused")
	private ConnectedPath() {
	}

	public ConnectedPath(Node connect2A, Node connect2B, boolean connected) {
		this.connect2A = connect2A;
		this.connect2B = connect2B;
		this.connected = connected;
	}

}
