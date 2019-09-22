package comp;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public String write() {
		return new RobotStateOutPut(this).toString();
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

	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object == this)
			return true;
		if (this.getClass() != object.getClass())
			return false;
		RobotState rs = (RobotState) object;
		if (this.grapple != rs.grapple)
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

		RobotStateOutPut(RobotState rs) {
			this.ee = rs.start;
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

	public static List<RobotStateOutPut> readRobotStateFromInput(
			String fileName) throws Exception {
		List<RobotStateOutPut> list = new ArrayList<RobotStateOutPut>();
		Stream<String> stream = Files.lines(Paths.get(fileName));
		list = (List<RobotStateOutPut>) stream.filter(p -> !p.trim().isEmpty())
				.map(p -> {
					return new RobotStateOutPut(p.split(";"));
				}).collect(Collectors.toList());

		if (stream != null)
			stream.close();

		return list;
	}

}
