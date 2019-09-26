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
