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
		List<Board> transition = new ArrayList<Board>();
		this.planGrappleTransition(this.board, transition);

		for (Board bd : transition) {
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
				if (dist > (GlbCfg.rrtMaxRadianDistance
						+ GlbCfg.rrtMaxLengthDistance)
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

	private boolean planGrappleTransition(Board targetBoard,
			List<Board> transition) {
		// same grapple
		if (RobotUtils.comparable(targetBoard.goalRobotState,
				targetBoard.initRobotState)) {
			transition.add(targetBoard);
			return true;
		}

		// return null if no transitions are possible
		if (targetBoard.grapples.size() == 1)
			return false;

		if (targetBoard.grapples.size() == 2) {
			if (targetBoard.initRobotState.ee1Grappled == targetBoard.goalRobotState.ee1Grappled
					|| targetBoard.initRobotState.ee2Grappled == targetBoard.goalRobotState.ee2Grappled)
				return true;

			RobotState finalGoalState = targetBoard.goalRobotState.clone();
			RobotState transitionState = this.generateTransitionRobotState(
					targetBoard.initRobotState, targetBoard.goalRobotState);

			Board b1 = targetBoard.clone();
			b1.initRobotState = targetBoard.initRobotState.clone();
			b1.goalRobotState = transitionState.clone();
			b1.state = targetBoard.initRobotState.clone();
			transition.add(b1);

			Board b2 = targetBoard.clone();
			RobotState newInit = transitionState.clone();
			newInit.switchGrappledEE();
			b2.initRobotState = newInit;
			b2.goalRobotState = finalGoalState;
			b2.state = newInit.clone();
			transition.add(b2);

			return true;
		}

		if (targetBoard.grapples.size() == 3) {
			if (targetBoard.initRobotState.ee1Grappled != targetBoard.goalRobotState.ee1Grappled
					|| targetBoard.initRobotState.ee2Grappled != targetBoard.goalRobotState.ee2Grappled)
				return false;

			Coordinate endEffector;
			if (targetBoard.initRobotState.ee1Grappled)
				endEffector = targetBoard.initRobotState.ee1;
			else
				endEffector = targetBoard.initRobotState.ee2;

			RobotState finalGoalState = targetBoard.goalRobotState.clone();

			double minDist = Double.MAX_VALUE;
			Coordinate min = null;
			for (Coordinate c : targetBoard.grapples) {
				if (!c.equals(endEffector)) {
					double dist = RobotUtils.euclideanDistance(endEffector, c);
					if (dist < minDist) {
						minDist = dist;
						min = c;
					}
				}
			}

			TransitionState transitionState = planner
					.findTransition(targetBoard.initRobotState, min);
			if (!transitionState.found)
				return false;

			Board b1 = targetBoard.clone();
			b1.initRobotState = targetBoard.initRobotState.clone();
			b1.goalRobotState = transitionState.state.clone();
			b1.state = targetBoard.initRobotState.clone();
			transition.add(b1);

			Board b2 = targetBoard.clone();
			RobotState newInit = transitionState.state.clone();
			newInit.switchGrappledEE();
			b2.initRobotState = newInit;
			b2.goalRobotState = finalGoalState;
			b2.state = newInit.clone();

			b2.removeGrapple(endEffector);
			planGrappleTransition(b2, transition);
		}

		if (targetBoard.grapples.size() == 4) {
			if (targetBoard.initRobotState.ee1Grappled != targetBoard.goalRobotState.ee1Grappled
					&& targetBoard.initRobotState.ee2Grappled != targetBoard.goalRobotState.ee2Grappled) {

				Coordinate endEffector;
				if (targetBoard.initRobotState.ee1Grappled)
					endEffector = targetBoard.initRobotState.ee1;
				else
					endEffector = targetBoard.initRobotState.ee2;

				RobotState finalGoalState = targetBoard.goalRobotState.clone();

				double minDist = Double.MAX_VALUE;
				Coordinate min = null;
				for (Coordinate c : targetBoard.grapples) {
					if (!c.equals(endEffector)) {
						double dist = RobotUtils.euclideanDistance(endEffector,
								c);
						if (dist < minDist) {
							minDist = dist;
							min = c;
						}
					}
				}

				TransitionState transitionState = planner
						.findTransition(targetBoard.initRobotState, min);
				if (!transitionState.found)
					return false;

				Board b1 = targetBoard.clone();
				b1.initRobotState = targetBoard.initRobotState.clone();
				b1.goalRobotState = transitionState.state.clone();
				b1.state = targetBoard.initRobotState.clone();
				transition.add(b1);

				Board b2 = targetBoard.clone();
				RobotState newInit = transitionState.state.clone();
				newInit.switchGrappledEE();
				b2.initRobotState = newInit;
				b2.goalRobotState = finalGoalState;
				b2.state = newInit.clone();

				b2.removeGrapple(endEffector);
				planGrappleTransition(b2, transition);
			}

			else if (targetBoard.initRobotState.ee1Grappled == targetBoard.goalRobotState.ee1Grappled
					&& targetBoard.initRobotState.ee2Grappled == targetBoard.goalRobotState.ee2Grappled) {

				Coordinate initEndEffector;
				if (targetBoard.initRobotState.ee1Grappled)
					initEndEffector = targetBoard.initRobotState.ee1;
				else
					initEndEffector = targetBoard.initRobotState.ee2;

				Coordinate goalEndEffector;
				if (targetBoard.goalRobotState.ee1Grappled)
					goalEndEffector = targetBoard.goalRobotState.ee1;
				else
					goalEndEffector = targetBoard.goalRobotState.ee2;

				double maxDist = Double.MIN_VALUE;
				Coordinate max = null;
				for (Coordinate c : targetBoard.grapples) {
					if (!c.equals(initEndEffector)
							&& !c.equals(goalEndEffector)) {
						double dist = RobotUtils
								.euclideanDistance(initEndEffector, c);
						if (dist > maxDist) {
							maxDist = dist;
							max = c;
						}
					}
				}

				targetBoard.removeGrapple(max);
				planGrappleTransition(targetBoard, transition);
			}

			else {
				System.exit(-1);
			}

		}

		return true;
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
