package comp;

import java.util.ArrayList;
import java.util.List;

public class Board {

	public RobotState initRobotState;
	public RobotState goalRobotState;

	public RobotState state;

	public static List<RobotState> transition = new ArrayList<RobotState>();
	public static final List<Coordinate> grapples = new ArrayList<Coordinate>();
	public static final List<BoundingBox> obstacles = new ArrayList<BoundingBox>();

	public Board() {
	}

	@Override
	public String toString() {
		String str = "";
		str = str.concat(
				"Board :" + System.lineSeparator() + System.lineSeparator());
		str = str
				.concat("Init " + this.initRobotState + System.lineSeparator());
		str = str
				.concat("Goal " + this.goalRobotState + System.lineSeparator());
		str = str.concat("Current " + this.state + System.lineSeparator());
		str = str.concat("Grapple " + grapples + System.lineSeparator());
		str = str.concat("Grapple " + obstacles + System.lineSeparator());
		return str;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object == this)
			return true;
		if (this.getClass() != object.getClass())
			return false;

		Board b = (Board) object;
		return this.equals(b);
	}

}
