package comp;

import java.util.List;
import java.util.stream.Collectors;

public class RobotState {

	Coordinate start;
	List<Segment> segments;

	private RobotState() {
		this.start = null;
		this.segments = null;
	}

	public RobotState(Coordinate s, List<Segment> seg) {
		this.start = s;
		this.segments = seg;
	}

	@Override
	public String toString() {
		String str = "";
		str = str.concat("State :" + System.lineSeparator());
		str = str.concat("Start Point : " + start + System.lineSeparator());
		for (Segment seg : this.segments) {
			str = str.concat(seg.toString() + System.lineSeparator());
		}
		return str;
	}

	@Override
	public RobotState clone() {
		RobotState state = new RobotState();
		state.start = this.start.clone();
		state.segments = this.segments.stream().map(e -> e.clone())
				.collect(Collectors.toList());
		return state;
	}

}
