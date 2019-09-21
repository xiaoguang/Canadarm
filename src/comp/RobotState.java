package comp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RobotState {

	int grapple;
	Coordinate start;
	List<Segment> segments;
	List<Coordinate> joints;

	private RobotState() {
		this.start = null;
		this.segments = null;
		this.joints = null;
	}

	public RobotState(int g, Coordinate s, List<Segment> seg) {
		this.grapple = g;
		this.start = s;
		this.segments = seg;
		this.joints = new ArrayList<Coordinate>(segments.size());
		calcJoints();
	}

	private void calcJoints() {
		this.joints.clear();
		Angle ang = new AngleInRadian(0);
		this.joints.add(new Coordinate(this.start.X, this.start.Y));
		for (Segment seg : this.segments) {
			Coordinate c = this.joints.get(this.joints.size() - 1);
			double x = c.X;
			double y = c.Y;

			ang.add(seg.angle);
			x += (seg.len * Math.cos(ang.radian));
			y += (seg.len * Math.sin(ang.radian));
			this.joints.add(new Coordinate(x, y));
		}
	}

	public boolean testLengthConstraint() {
		for (Segment seg : this.segments) {
			if (!seg.testLengthConstraint()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String str = "";
		str = str.concat(
				"State : Grapple -> " + this.grapple + System.lineSeparator());
		str = str.concat("Start Point : " + start + System.lineSeparator());
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
		state.grapple = this.grapple;
		state.start = this.start.clone();
		state.segments = this.segments.stream().map(e -> e.clone())
				.collect(Collectors.toList());
		state.joints = this.joints.stream().map(e -> e.clone())
				.collect(Collectors.toList());
		return state;
	}

}
