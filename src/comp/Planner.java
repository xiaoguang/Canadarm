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

	public TransitionState findTransition(RobotState state, Coordinate to) {
		if (state.segments.size() < 2)
			return new TransitionState(null, false);

		boolean found = false;
		int numberOfSamples = 0;
		RobotState sample = state.clone();
		List<Segment> local = sample.segments;

		while (!found && numberOfSamples < GlbCfg.maxNumberOfSamples) {
			for (Segment seg : local) {
				double r = RobotUtils.uniformAngleSampling();
				seg.angle.radian = r;
				seg.angle.normalize();

				double l = RobotUtils.uniformSample(seg.min, seg.max);
				seg.len = l;
			}
			sample.calcJoints();

			Segment seg = sample.segments.get(sample.segments.size() - 1);
			Coordinate prev = sample.joints.get(sample.segments.size() - 2);
			Coordinate from = sample.joints.get(sample.segments.size() - 1);
			double dist = RobotUtils.euclideanDistance(to, from);
			if (dist < seg.max) {
				seg.len = dist;
				seg.angle = RobotUtils.findAngle(to, from, from, prev);
				// check angle limitations ->
				// mainly about the last segment angles between robot and
				// obstacles
				if (!this.checkAngleLimitation(from, to))
					continue;
				sample.calcJoints();
				if (!sample.collision())
					found = true;
			}
		}

		return new TransitionState(sample, found);
	}

	private boolean checkAngleLimitation(Coordinate from, Coordinate to) {
		for (BoundingBox bb : Board.obstacles) {
			for (Line l : bb.edges) {
				Angle ang = RobotUtils.findAngle(to, from, l.p, l.q);
				if (Math.abs(ang.radian) < 2 * GlbCfg.deltaRadian)
					return false;
			}
		}

		return true;
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

	public List<RobotState> stepSmoother(RobotState from, RobotState to) {
		return this.generateSteps(from, to, GlbCfg.delta, GlbCfg.delta, false);
	}

	public List<RobotState> generateStepsFeatureTracking(RobotState from,
			RobotState to) {
		return this.generateSteps(from, to, GlbCfg.deltaLength,
				GlbCfg.deltaRadian, true);
	}

	public List<RobotState> generateSteps(RobotState from, RobotState to,
			double deltaLength, double deltaRadian, boolean check) {
		if (check) {
			if (!this.validate(from, to))
				return null;
			if (!this.reachable(from, to))
				return null;
		}

		List<RobotState> changes = new ArrayList<RobotState>();

		if (!check) {
			if (from.ee1Grappled == to.ee2Grappled) {
				changes.add(from);
				changes.add(to);
				return changes;
			}
		}

		RobotState rsFrom = from.clone();
		RobotState rsTo = to.clone();
		if (check) {
			changes.add(rsFrom);
		} else {
			changes.add(rsFrom.clone());
		}

		for (int i = 0; i < rsFrom.segments.size(); i++) {
			Segment sf = rsFrom.segments.get(i);
			Segment st = rsTo.segments.get(i);

			double angleDiff = Double.MAX_VALUE;
			if (i == 0) {
				angleDiff = RobotUtils.diffInRadianForFirstSegment(sf.angle,
						st.angle);
			} else {
				angleDiff = RobotUtils.diffInRadian(sf.angle, st.angle);
			}
			double lengthDiff = sf.len - st.len;

			// length shifts
			{
				if (lengthDiff > 0) {
					while (lengthDiff > deltaLength) {
						if (check) {
							lengthDiff -= deltaLength;
							sf.len -= deltaLength;
							rsFrom.calcJoints();

							RobotState lrs = rsFrom.clone();
							if (lrs.collision())
								return null;
							changes.add(lrs);
						} else {
							RobotState lrs = rsFrom.clone();
							lengthDiff -= deltaLength;
							lrs.segments.get(i).len -= deltaLength;
							lrs.calcJoints();
							changes.add(lrs);
							sf.len -= deltaLength;
							rsFrom.calcJoints();
						}
					}
				} else {
					while (Math.abs(lengthDiff) > deltaLength) {
						if (check) {
							lengthDiff += deltaLength;
							sf.len += deltaLength;
							rsFrom.calcJoints();

							RobotState lrs = rsFrom.clone();
							if (lrs.collision())
								return null;
							changes.add(lrs);
						} else {
							RobotState lrs = rsFrom.clone();
							lengthDiff += deltaLength;
							lrs.segments.get(i).len += deltaLength;
							lrs.calcJoints();
							changes.add(lrs);
							sf.len += deltaLength;
							rsFrom.calcJoints();
						}
					}
				}

				if (Math.abs(lengthDiff) < GlbCfg.epsilon)
					continue;

				if (check) {
					sf.len += lengthDiff;
					rsFrom.calcJoints();
					RobotState lrs = rsFrom.clone();
					if (lrs.collision())
						return null;
					changes.add(lrs);
				} else {
					RobotState lrs = rsFrom.clone();
					lrs.segments.get(i).len += lengthDiff;
					lrs.calcJoints();
					changes.add(lrs);
					sf.len += lengthDiff;
					rsFrom.calcJoints();
				}
			}

			// angle shifts
			{
				if (angleDiff > 0) {
					while (angleDiff > deltaRadian) {
						if (check) {
							angleDiff -= deltaRadian;
							sf.angle.addInRadian(deltaRadian);
							rsFrom.calcJoints();

							RobotState lrs = rsFrom.clone();
							if (lrs.collision())
								return null;
							changes.add(lrs);
						} else {
							angleDiff -= deltaRadian;
							RobotState lrs = rsFrom.clone();
							lrs.segments.get(i).angle.addInRadian(deltaRadian);
							lrs.calcJoints();
							changes.add(lrs);
							sf.angle.addInRadian(deltaRadian);
							rsFrom.calcJoints();
						}
					}
				} else {
					while (Math.abs(angleDiff) > deltaRadian) {
						if (check) {
							angleDiff += deltaRadian;
							sf.angle.minusInRadian(deltaRadian);
							rsFrom.calcJoints();

							RobotState lrs = rsFrom.clone();
							if (lrs.collision())
								return null;
							changes.add(lrs);
						} else {
							angleDiff += deltaRadian;
							RobotState lrs = rsFrom.clone();
							lrs.segments.get(i).angle
									.minusInRadian(deltaRadian);
							lrs.calcJoints();
							changes.add(lrs);
							sf.angle.minusInRadian(deltaRadian);
							rsFrom.calcJoints();
						}
					}
				}

				if (Math.abs(angleDiff) < GlbCfg.epsilon)
					continue;

				if (check) {
					sf.angle.addInRadian(angleDiff);
					rsFrom.calcJoints();
					RobotState lrs = rsFrom.clone();
					if (lrs.collision())
						return null;
					changes.add(lrs);
				} else {
					RobotState lrs = rsFrom.clone();
					lrs.segments.get(i).angle.addInRadian(angleDiff);
					lrs.calcJoints();
					changes.add(lrs);
					sf.angle.addInRadian(angleDiff);
					rsFrom.calcJoints();
				}
			}
		}

		return changes;
	}

}
