package comp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utils.GlbCfg;

public class RobotState {

	List<Segment> segments;
	List<Coordinate> joints;

	List<Segment> ee1Segments;
	List<Segment> ee2Segments;

	boolean ee1Grappled;
	boolean ee2Grappled;

	Coordinate ee1;
	Coordinate ee2;

	private RobotState() {
		this.ee1 = null;
		this.ee2 = null;
		this.segments = null;
		this.ee1Segments = null;
		this.ee2Segments = null;
		this.joints = null;
	}

	private RobotState(Coordinate ee1, Coordinate ee2, boolean ee1Grappled,
			boolean ee2Grappled, List<Segment> segments) {
		this.ee1 = ee1;
		this.ee2 = ee2;
		this.ee1Grappled = ee1Grappled;
		this.ee2Grappled = ee2Grappled;
		this.segments = segments;
		this.joints = new ArrayList<Coordinate>();
		this.calcJoints();
	}

	public static RobotState createRobotStateFromEE1(Coordinate ee1,
			List<Segment> segments) {
		RobotState rs = new RobotState(ee1, null, true, false, segments);
		return rs;
	}

	public static RobotState createRobotStateFromEE2(Coordinate ee2,
			List<Segment> segments) {
		RobotState rs = new RobotState(null, ee2, false, true, segments);
		return rs;
	}

	protected void calcJoints() {
		this.joints.clear();

		if (this.ee1Grappled) {
			this.joints.add(this.ee1);
			this.ee1Segments = this.segments.stream().map(e -> e.clone())
					.collect(Collectors.toList());
			this.ee2Segments = new ArrayList<Segment>();
		} else {
			this.joints.add(this.ee2);
			this.ee1Segments = new ArrayList<Segment>();
			this.ee2Segments = this.segments.stream().map(e -> e.clone())
					.collect(Collectors.toList());
		}

		Angle ang = new AngleInRadian(0);
		for (Segment seg : this.segments) {
			Coordinate c = this.joints.get(this.joints.size() - 1);
			double x = c.X;
			double y = c.Y;

			ang.add(seg.angle);
			x += (seg.len * Math.cos(ang.radian));
			y += (seg.len * Math.sin(ang.radian));
			this.joints.add(new Coordinate(x, y));
		}

		ang.add(new AngleInRadian(Math.PI));
		Segment first = this.segments.get(this.segments.size() - 1).clone();
		first.angle = ang;
		if (this.ee1Grappled) {
			this.ee2 = this.joints.get(this.joints.size() - 1);
			this.ee2Segments.add(first);
		} else {
			this.ee1 = this.joints.get(this.joints.size() - 1);
			this.ee1Segments.add(first);
		}
		for (int i = this.segments.size() - 2; i > -1; i--) {
			Segment local = this.segments.get(i).clone();
			Angle localAngle = this.segments.get(i + 1).angle.clone();
			local.angle = localAngle.negative();

			if (this.ee1Grappled) {
				this.ee2Segments.add(local);
			} else {
				this.ee1Segments.add(local);
			}
		}
	}

	public void switchGrappledEE() {
		this.calcJoints();
		List<Segment> newEnd;

		if (this.ee1Grappled) {
			this.ee1Grappled = false;
			this.ee2Grappled = true;
			newEnd = this.ee2Segments;
		} else {
			this.ee2Grappled = false;
			this.ee1Grappled = true;
			newEnd = this.ee1Segments;
		}

		this.segments.clear();
		this.segments = newEnd.stream().map(e -> e.clone())
				.collect(Collectors.toList());
		this.calcJoints();
	}

	public String write() {
		return new RobotStateOutPut(this).toString();
	}

	public boolean violateLengthAndAngleConstraint() {
		for (int i = 0; i < this.segments.size(); i++) {
			Segment seg = this.segments.get(i);
			if (!seg.testLengthConstraint())
				return true;
			if (i > 0 && !seg.testAngleConstraint())
				return true;
		}
		return false;
	}

	public boolean environmentCollision() {
		for (Coordinate j : this.joints) {
			double x = j.X;
			double y = j.Y;
			if (x > 1.0 || x < 0.0)
				return true;
			if (y > 1.0 || y < 0.0)
				return true;
		}
		return false;
	}

	public boolean selfCollision() {
		if (this.segments.size() < 3)
			return false;
		for (int i = 0; i < this.joints.size() - 2; i++) {
			Coordinate c1 = this.joints.get(i);
			Coordinate c2 = this.joints.get(i + 1);

			for (int j = i + 2; j < this.joints.size() - 1; j++) {
				Coordinate c3 = this.joints.get(j);
				Coordinate c4 = this.joints.get(j + 1);

				if (RobotUtils.testLineCollision(c1, c2, c3, c4))
					return true;
			}
		}
		return false;
	}

	public boolean obstacleCollision() {
		for (int i = 0; i < this.joints.size() - 1; i++) {
			Coordinate c1 = this.joints.get(i);
			Coordinate c2 = this.joints.get(i + 1);
			for (BoundingBox b : Board.obstacles) {
				if (!RobotUtils.testBoundingBoxCollision(c1, c2, b.bl, b.tr))
					continue;

				for (Line l : b.edges) {
					if (RobotUtils.testLineCollision(c1, c2, l.p, l.q))
						return true;
				}
			}
		}
		return false;
	}

	public boolean collision() {
		boolean collide = false;
		collide = collide || this.violateLengthAndAngleConstraint();
		collide = collide || this.selfCollision();
		collide = collide || this.obstacleCollision();
		collide = collide || this.environmentCollision();
		return collide;
	}

	// sample in between this and state
	boolean findSampleWithin(RobotState state) {
		// precautionary test, can be removed after stabilized
		// return false if not comparable
		if (!RobotUtils.comparable(this, state))
			return false;

		double dist = 0.0;
		for (int i = 0; i < this.segments.size(); i++) {
			Segment from = this.segments.get(i);
			Segment to = state.segments.get(i);
			to.angle.radian = RobotUtils.uniformSample(from.angle.radian,
					to.angle.radian);
			to.angle.normalize();
			dist += Math.abs(RobotUtils.diffInRadian(from.angle, to.angle));
			to.len = RobotUtils.uniformSample(from.len, to.len);
			dist += Math.abs(from.len - to.len);
		}
		state.calcJoints();

		// return false, if sampled step is too small
		if (dist < GlbCfg.epsilon * state.segments.size()) {
			return false;
		}

		// re-sample if needed
		if (dist > (GlbCfg.rrtMaxRadianDistance + GlbCfg.rrtMaxLengthDistance)
				* state.segments.size()) {
			return this.findSampleWithin(state);
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 0;

		for (Coordinate j : this.joints) {
			if (this.ee1Grappled)
				hash += GlbCfg.prime2;
			else
				hash += GlbCfg.prime3;
			hash += j.hashCode();
		}

		return hash;
	}

	@Override
	public String toString() {
		String str = "";
		if (this.ee1Grappled) {
			str = str.concat("State : Grapple -> ee1" + System.lineSeparator());
			str = str.concat(
					"Start Point : " + this.ee1 + System.lineSeparator());
		} else {
			str = str.concat("State : Grapple -> ee2" + System.lineSeparator());
			str = str.concat(
					"Start Point : " + this.ee2 + System.lineSeparator());
		}
		for (Segment seg : this.segments) {
			str = str.concat(seg.toString() + System.lineSeparator());
		}
		for (Coordinate c : this.joints) {
			str = str.concat("JNT : " + c.toString() + System.lineSeparator());
		}
		return str;
	}

	@Override
	public RobotState clone() {
		RobotState state = new RobotState();
		state.ee1 = this.ee1.clone();
		state.ee2 = this.ee2.clone();
		state.ee1Grappled = this.ee1Grappled;
		state.ee2Grappled = this.ee2Grappled;
		state.ee1Segments = this.ee1Segments.stream().map(e -> e.clone())
				.collect(Collectors.toList());
		state.ee2Segments = this.ee2Segments.stream().map(e -> e.clone())
				.collect(Collectors.toList());
		state.segments = this.segments.stream().map(e -> e.clone())
				.collect(Collectors.toList());
		state.joints = this.joints.stream().map(e -> e.clone())
				.collect(Collectors.toList());
		return state;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object == this)
			return true;
		if (this.getClass() != object.getClass())
			return false;
		RobotState rs = (RobotState) object;
		if (this.ee1Grappled != rs.ee1Grappled)
			return false;
		if (this.ee2Grappled != rs.ee2Grappled)
			return false;
		if (this.joints.size() != rs.joints.size())
			return false;
		for (int i = 0; i < this.joints.size(); i++) {
			Coordinate c1 = this.joints.get(i);
			Coordinate c2 = rs.joints.get(i);
			if (!c1.equals(c2))
				return false;
		}
		return true;
	}

	public static class RobotStateOutPut {

		String x1;
		String x2;
		String x3;

		Coordinate ee;
		List<Angle> angles;
		List<Double> lengths;
		List<Segment> segments;

		@SuppressWarnings("unused")
		private RobotStateOutPut() {
		}

		RobotStateOutPut(String[] input) {
			x1 = input[0].trim();
			x2 = input[1].trim();
			x3 = input[2].trim();
			this.transform();
		}

		public RobotStateOutPut(RobotState rs) {
			if (rs.ee1Grappled) {
				this.ee = rs.ee1;
			} else {
				this.ee = rs.ee2;
			}
			this.segments = rs.segments;
		}

		private void transform() {
			String[] coord = x1.split(" ");
			this.ee = new Coordinate(Double.parseDouble(coord[0]),
					Double.parseDouble(coord[1]));

			this.angles = new ArrayList<Angle>();
			String[] angs = x2.split(" ");
			for (String ang : angs) {
				this.angles.add(new AngleInDegree(Double.parseDouble(ang)));
			}

			this.lengths = new ArrayList<Double>();
			String[] lens = x3.split(" ");
			for (String len : lens) {
				this.lengths.add(Double.parseDouble(len));
			}
		}

		@Override
		public String toString() {
			String str = "";
			str = str
					.concat(Double.toString(ee.X) + " " + Double.toString(ee.Y))
					.concat(";");

			String anglesInDegree = "";
			String segmentsLength = "";

			if (this.segments != null) {
				for (Segment seg : this.segments) {
					anglesInDegree = anglesInDegree
							.concat(" " + Double.toString(seg.angle.degree));
					segmentsLength = segmentsLength
							.concat(" " + Double.toString(seg.len));
				}
				anglesInDegree = anglesInDegree.concat(";");
			} else {
				for (Angle ang : angles) {
					anglesInDegree = anglesInDegree
							.concat(" " + Double.toString(ang.degree));
				}
				anglesInDegree = anglesInDegree.concat(";");

				for (Double len : lengths) {
					segmentsLength = segmentsLength
							.concat(" " + Double.toString(len));
				}
			}

			str = str.concat(anglesInDegree).concat(segmentsLength);
			return str;
		}
	}

}

class Segment implements Cloneable {

	double min;
	double max;
	double len;
	Angle angle;

	@SuppressWarnings("unused")
	private Segment() {
	}

	public Segment(double min, double max, double len, Angle angle) {
		this.min = min;
		this.max = max;
		this.len = len;
		this.angle = angle;
	}

	public boolean testLengthConstraint() {
		return (this.len <= this.max) && (this.min <= this.len);
	}

	public boolean testAngleConstraint() {
		if (GlbCfg.angleLowerBound < this.angle.radian
				&& this.angle.radian < GlbCfg.angleUpperBound)
			return true;
		return false;
	}

	@Override
	public String toString() {
		String str = "";
		str = str.concat("Segment :" + System.lineSeparator());
		str = str.concat(
				"ARM : " + "MIN " + min + " MAX " + max + " LEN " + len);
		str = str.concat(System.lineSeparator() + angle.toString());
		return str;
	}

	@Override
	public Segment clone() {
		Segment seg = new Segment(this.min, this.max, this.len,
				this.angle.clone());
		return seg;
	}

}
