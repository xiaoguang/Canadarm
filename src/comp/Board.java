package comp;

import java.util.ArrayList;
import java.util.List;

public class Board {

	static RobotState initRobotState;
	static RobotState goalRobotState;
	RobotState state;

	static final List<Coordinate> grapples = new ArrayList<Coordinate>();
	static final List<BoundingBox> obstacles = new ArrayList<BoundingBox>();

	public Board(RobotState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		String str = "";
		str = str.concat(
				"Board :" + System.lineSeparator() + System.lineSeparator());
		str = str.concat(
				"Init " + Board.initRobotState + System.lineSeparator());
		str = str.concat(
				"Goal " + Board.goalRobotState + System.lineSeparator());
		str = str.concat("Current " + this.state + System.lineSeparator());
		str = str.concat("Grapple " + grapples + System.lineSeparator());
		str = str.concat("Grapple " + obstacles + System.lineSeparator());
		return str;
	}

}
