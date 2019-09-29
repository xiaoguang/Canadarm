package comp;

import java.util.ArrayList;
import java.util.List;

public class Board {

	public static RobotState initRobotState;
	public static RobotState goalRobotState;

	public RobotState state;

	public static final List<Coordinate> grapples = new ArrayList<Coordinate>();
	public static final List<BoundingBox> obstacles = new ArrayList<BoundingBox>();

	@SuppressWarnings("unused")
	private Board() {
	}

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

	@Override
	public Board clone() {
		RobotState cpy = this.state.clone();
		return new Board(cpy);
	}

}
