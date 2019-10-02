package comp;

import java.util.ArrayList;
import java.util.List;

import utils.GlbCfg;

public class Planner {

	public Planner() {
	}

	public RobotState randomSampling(RobotState state) {
		boolean found = false;
		RobotState sample = state.clone();
		List<Segment> local = sample.segments;

		while (!found) {
			for (Segment seg : local) {
				double r = RobotUtils.uniformAngleSampling();
				seg.angle.radian = r;
				seg.angle.normalize();

				double l = RobotUtils.uniformSample(seg.min, seg.max);
				seg.len = l;
			}

			sample.calcJoints();
			if (!sample.collision())
				found = true;
		}

		return sample;
	}

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
				if (!RobotUtils.testBoundingBoxCollision(localFrom, localTo,
						b.bl, b.tr))
					continue;

				for (Line l : b.edges) {
					if (RobotUtils.testLineCollision(localFrom, localTo, l.p,
							l.q))
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

			double angleDiff = RobotUtils.diffInRadian(sf.angle, st.angle);
			double lengthDiff = sf.len - st.len;

			// length shifts
			{
				if (lengthDiff > 0) {
					while (lengthDiff > GlbCfg.deltaLength) {
						lengthDiff -= GlbCfg.deltaLength;
						sf.len -= GlbCfg.deltaLength;
						rsFrom.calcJoints();

						RobotState lrs = rsFrom.clone();
						if (lrs.collision())
							return null;
						changes.add(lrs);
					}
				} else {
					while (Math.abs(lengthDiff) > GlbCfg.deltaLength) {
						lengthDiff += GlbCfg.deltaLength;
						sf.len += GlbCfg.deltaLength;
						rsFrom.calcJoints();

						RobotState lrs = rsFrom.clone();
						if (lrs.collision())
							return null;
						changes.add(lrs);
					}
				}

				if (Math.abs(lengthDiff) < GlbCfg.epsilon)
					continue;

				sf.len += lengthDiff;
				RobotState lrs = rsFrom.clone();
				if (lrs.collision())
					return null;
				changes.add(lrs);
			}

			// angle shifts
			{
				if (angleDiff > 0) {
					while (angleDiff > GlbCfg.deltaRadian) {
						angleDiff -= GlbCfg.deltaRadian;
						sf.angle.addInRadian(GlbCfg.deltaRadian);
						rsFrom.calcJoints();

						RobotState lrs = rsFrom.clone();
						if (lrs.collision())
							return null;
						changes.add(lrs);
					}
				} else {
					while (Math.abs(angleDiff) > GlbCfg.deltaRadian) {
						angleDiff += GlbCfg.deltaRadian;
						sf.angle.minusInRadian(GlbCfg.deltaRadian);
						rsFrom.calcJoints();

						RobotState lrs = rsFrom.clone();
						if (lrs.collision())
							return null;
						changes.add(lrs);
					}
				}

				if (Math.abs(angleDiff) < GlbCfg.epsilon)
					continue;

				sf.angle.addInRadian(angleDiff);
				RobotState lrs = rsFrom.clone();
				if (lrs.collision())
					return null;
				changes.add(lrs);
			}
		}

		return changes;
	}

}
