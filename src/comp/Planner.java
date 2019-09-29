package comp;

import java.util.ArrayList;
import java.util.List;

import utils.GlobalCfg;

public class Planner {

	public static final GlobalPlanner gp = new GlobalPlanner();
	public static final localPlanner lp = new localPlanner();

	public RobotState randomSampling(RobotState state) {
		return gp.randomSampling(state);
	}

	public boolean validate(RobotState from, RobotState to) {
		return lp.validate(from, to);
	}

	public boolean reachable(RobotState from, RobotState to) {
		return lp.reachable(from, to);
	}

	public List<RobotState> generateSteps(RobotState from, RobotState to) {
		return lp.generateSteps(from, to);
	}

}

class GlobalPlanner {

	public RobotState randomSampling(RobotState state) {
		boolean found = false;
		RobotState sample = state.clone();
		List<Segment> local = sample.segments;

		while (!found) {
			for (Segment seg : local) {
				double r = RoboticUtilFunctions.uniformAngleSampling();
				seg.angle.radian = r;
				seg.angle.normalize();
			}

			sample.calcJoints();
			if (!sample.collision())
				found = true;
		}

		return sample;
	}

}

class localPlanner {

	public boolean validate(RobotState from, RobotState to) {
		if (from.ee1Grappled != to.ee1Grappled
				|| from.ee2Grappled != to.ee2Grappled
				|| from.segments.size() != to.segments.size()
				|| from.collision() || to.collision())
			return false;
		return true;
	}

	public boolean reachable(RobotState from, RobotState to) {
		for (int i = 0; i < from.joints.size(); i++) {
			Coordinate localFrom = from.joints.get(i);
			Coordinate localTo = to.joints.get(i);

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

	public List<RobotState> generateSteps(RobotState from, RobotState to) {
		if (!this.validate(from, to))
			return null;
		if (!this.reachable(from, to))
			return null;

		List<RobotState> changes = new ArrayList<RobotState>();
		RobotState rsFrom = from.clone();
		RobotState rsTo = to.clone();
		changes.add(rsFrom);

		for (int i = 0; i < rsFrom.segments.size(); i++) {
			Segment sf = rsFrom.segments.get(i);
			Segment st = rsTo.segments.get(i);

			double diff = RoboticUtilFunctions.diffInRadian(sf.angle, st.angle);

			if (diff > 0) {
				while (diff > GlobalCfg.deltaRadian) {
					diff -= GlobalCfg.deltaRadian;
					sf.angle.addInRadian(GlobalCfg.deltaRadian);
					rsFrom.calcJoints();

					RobotState lrs = rsFrom.clone();
					if (lrs.collision())
						return null;
					changes.add(lrs);
				}
			} else {
				while (Math.abs(diff) > GlobalCfg.deltaRadian) {
					diff += GlobalCfg.deltaRadian;
					sf.angle.minusInRadian(GlobalCfg.deltaRadian);
					rsFrom.calcJoints();

					RobotState lrs = rsFrom.clone();
					if (lrs.collision())
						return null;
					changes.add(lrs);
				}
			}

			if (Math.abs(diff) < GlobalCfg.epsilon)
				continue;

			sf.angle.addInRadian(diff);
			RobotState lrs = rsFrom.clone();
			if (lrs.collision())
				return null;
			changes.add(lrs);
		}

		return changes;
	}

}