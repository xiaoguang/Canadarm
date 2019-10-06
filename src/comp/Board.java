package comp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Board {

	public RobotState initRobotState;
	public RobotState goalRobotState;

	RobotState state;
	List<Coordinate> grapples;

	public static final List<BoundingBox> obstacles = new ArrayList<BoundingBox>();

	public Board() {
		this.grapples = new ArrayList<Coordinate>();
	}

	public boolean removeGrapple(Coordinate g) {
		return this.grapples.remove(g);
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
	public Board clone() {
		Board cpy = new Board();
		cpy.initRobotState = this.initRobotState.clone();
		cpy.goalRobotState = this.goalRobotState.clone();
		cpy.state = this.state.clone();
		cpy.grapples = this.grapples.stream().map(e -> e.clone())
				.collect(Collectors.toList());
		return cpy;
	}

}
