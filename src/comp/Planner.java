package comp;

import java.util.ArrayList;
import java.util.List;

import utils.GlobalCfg;

public class Planner {

	public static final GlobalPlanner gp = new GlobalPlanner();
	public static final localPlanner lp = new localPlanner();

	public Board randomSampling(Board board) {
		return gp.randomSampling(board);
	}

	public boolean validate(Board from, Board to) {
		return lp.validate(from, to);
	}

	public boolean reachable(Board from, Board to) {
		return lp.reachable(from, to);
	}

	public List<Board> generateSteps(Board from, Board to) {
		return lp.generateSteps(from, to);
	}

}

class GlobalPlanner {

	public Board randomSampling(Board board) {
		boolean found = false;
		Board sample = board.clone();
		List<Segment> local = sample.state.segments;

		while (!found) {
			for (Segment seg : local) {
				Angle ang = new AngleInRadian(
						RoboticUtilFunctions.uniformAngleSampling());
				seg.angle = ang;
			}

			sample.state.calcJoints();
			if (!sample.collision())
				found = true;
		}

		return sample;
	}

}

class localPlanner {

	public boolean validate(Board from, Board to) {
		if (from.state.ee1Grappled != to.state.ee1Grappled
				|| from.state.ee2Grappled != to.state.ee2Grappled
				|| from.state.segments.size() != to.state.segments.size()
				|| from.collision() || to.collision())
			return false;
		return true;
	}

	public boolean reachable(Board from, Board to) {
		for (int i = 0; i < from.state.joints.size(); i++) {
			Coordinate localFrom = from.state.joints.get(i);
			Coordinate localTo = to.state.joints.get(i);

			for (BoundingBox b : Board.obstacles) {
				if (!RoboticUtilFunctions.testBoundingBoxCollision(localFrom,
						localTo, b.bl, b.tr))
					continue;

				for (Line l : b.edges) {
					if (RoboticUtilFunctions.testLineCollision(localFrom,
							localTo, l.p, l.q))
						return false;
				}
			}
		}
		return true;
	}

	public List<Board> generateSteps(Board from, Board to) {
		if (!this.validate(from, to))
			return null;
		if (!this.reachable(from, to))
			return null;

		List<Board> changes = new ArrayList<Board>();
		RobotState rsFrom = from.state.clone();
		RobotState rsTo = to.state.clone();
		changes.add(new Board(rsFrom.clone()));

		for (int i = 0; i < rsFrom.segments.size(); i++) {
			Segment sf = rsFrom.segments.get(i);
			Segment st = rsTo.segments.get(i);

			double angleDiff = RoboticUtilFunctions.diffInRadian(sf.angle,
					st.angle);

			if (angleDiff > 0) {
				while (angleDiff > GlobalCfg.deltaRadian) {
					angleDiff -= GlobalCfg.deltaRadian;
					sf.angle.addInRadian(GlobalCfg.deltaRadian);
					rsFrom.calcJoints();

					RobotState lrs = rsFrom.clone();
					Board lb = new Board(lrs);
					if (lb.collision())
						return null;
					changes.add(lb);
				}
			} else {
				while (Math.abs(angleDiff) > GlobalCfg.deltaRadian) {
					angleDiff += GlobalCfg.deltaRadian;
					sf.angle.minusInRadian(GlobalCfg.deltaRadian);
					rsFrom.calcJoints();

					RobotState lrs = rsFrom.clone();
					Board lb = new Board(lrs);
					if (lb.collision())
						return null;
					changes.add(lb);
				}
			}

			if (Math.abs(angleDiff) < GlobalCfg.epsilon)
				continue;

			sf.angle.addInRadian(angleDiff);
			RobotState lrs = rsFrom.clone();
			Board lb = new Board(lrs);
			if (lb.collision())
				return null;
			changes.add(lb);
		}

		return changes;
	}

}
