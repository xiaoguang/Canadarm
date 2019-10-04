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
		List<RobotState> steps = new ArrayList<RobotState>();

		List<Board> transition = this.planGrappleTransition();

		int idx = 0;
		for (Board bd : transition) {
			if (idx != 0) {
				System.out.println("\n\n\n");
				System.out.println(
						new RobotState.RobotStateOutPut(bd.initRobotState));
				System.out.println("\n\n\n");
				continue;
			}

			idx++;
			ConnectedPath found = this.findConnector(bd.initRobotState,
					bd.goalRobotState);

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
						.equals(bd.initRobotState)) {
					Collections.reverse(towardsA);
					steps.addAll(towardsA);
					steps.addAll(towardsB);
				} else {
					Collections.reverse(towardsB);
					steps.addAll(towardsB);
					steps.addAll(towardsA);
				}

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

	private RobotState generateTransitionRobotState(RobotState from,
			RobotState to) {
		Coordinate ee;
		if (to.ee1Grappled)
			ee = to.ee1;
		else
			ee = to.ee2;

		// check if is reachable by the very last segment
		TransitionState ts = planner.findTransition(from, ee);
		if (ts.found)
			return ts.state;

		return null;
	}

	private List<Board> planGrappleTransition() {

		List<Board> transition = new ArrayList<Board>();
		List<Coordinate> midway = new ArrayList<Coordinate>();

		// same grapple
		if (RobotUtils.comparable(this.board.goalRobotState,
				this.board.initRobotState)) {
			transition.add(this.board);
			return transition;
		}

		// return null if no transitions are possible
		if (Board.grapples.size() == 1)
			return null;

		if (Board.grapples.size() == 2) {
			if (this.board.initRobotState.ee1Grappled == this.board.goalRobotState.ee1Grappled
					|| this.board.initRobotState.ee2Grappled == this.board.goalRobotState.ee2Grappled)
				return null;

			RobotState finalGoalState = this.board.goalRobotState.clone();

			RobotState transitionState = this.generateTransitionRobotState(
					this.board.initRobotState, this.board.goalRobotState);
			Board b1 = new Board();
			b1.initRobotState = this.board.initRobotState;
			b1.goalRobotState = transitionState;
			b1.state = this.board.initRobotState;
			transition.add(b1);

			Board b2 = new Board();
			RobotState newInit = transitionState.clone();
			newInit.switchGrappledEE();
			b2.initRobotState = newInit;
			b2.goalRobotState = finalGoalState;
			b2.state = newInit;
			transition.add(b2);
		}

		// need midway

		return transition;

	}

}

class TransitionState {

	RobotState state;
	boolean found;

	@SuppressWarnings("unused")
	private TransitionState() {
	}

	public TransitionState(RobotState state, boolean found) {
		this.state = state;
		this.found = found;
	}

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
