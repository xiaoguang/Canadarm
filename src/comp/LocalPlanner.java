package comp;

import java.util.ArrayList;
import java.util.List;

import utils.GlobalCfg;

public class LocalPlanner {

	public LocalPlanner() {
	}

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
				if (!CollisionCheck.testBoundingBoxCollision(localFrom, localTo,
						b.bl, b.tr))
					continue;

				for (Line l : b.edges) {
					if (CollisionCheck.testLineCollision(localFrom, localTo,
							l.p, l.q))
						return false;
				}
			}
		}
		return true;
	}

	public double diffInRadian(Angle from, Angle to) {
		return to.radian - from.radian;
	}

	public List<RobotState> generateSteps(Board from, Board to) {
		if (!this.validate(from, to))
			return null;
		if (!this.reachable(from, to))
			return null;
		RobotState rsFrom = from.state.clone();
		RobotState rsTo = to.state.clone();
		List<RobotState> changes = new ArrayList<RobotState>();
		for (int i = 0; i < rsFrom.segments.size(); i++) {
			Segment sf = rsFrom.segments.get(i);
			Segment st = rsTo.segments.get(i);
			double angleDiff = this.diffInRadian(sf.angle, st.angle);
			if (angleDiff > 0 && angleDiff > GlobalCfg.deltaRadian) {
				angleDiff -= GlobalCfg.deltaRadian;
				sf.angle.addInRadian(GlobalCfg.deltaRadian);
				rsFrom.calcJoints();
			}
			if (angleDiff < 0 && Math.abs(angleDiff) > GlobalCfg.deltaRadian) {
				angleDiff += GlobalCfg.deltaRadian;
				sf.angle.minusInRadian(GlobalCfg.deltaRadian);
				rsFrom.calcJoints();
			}
			RobotState lrs = rsFrom.clone();
			changes.add(lrs);
		}
		return changes;
	}
}
