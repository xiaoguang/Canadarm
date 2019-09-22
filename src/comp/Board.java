package comp;

import java.util.ArrayList;
import java.util.List;

public class Board {

	static RobotState initRobotState;
	static RobotState goalRobotState;
	RobotState state;
	CollisionCheck checker;

	static final List<Coordinate> grapples = new ArrayList<Coordinate>();
	static final List<BoundingBox> obstacles = new ArrayList<BoundingBox>();

	@SuppressWarnings("unused")
	private Board() {
	}

	public Board(RobotState state) {
		this.state = state;
		this.checker = new CollisionCheck();
	}

	public boolean testLengthConstraint() {
		return this.state.testLengthConstraint();
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

	public boolean obstacleCollision() {
		for (int i = 0; i < this.state.joints.size() - 1; i++) {
			Coordinate c1 = this.state.joints.get(i);
			Coordinate c2 = this.state.joints.get(i + 1);
			for (BoundingBox b : obstacles) {
				if (!checker.testBoundingBoxCollision(c1, c2, b.bl, b.tr))
					continue;

				for (Line l : b.edges) {
					if (checker.testLineCollision(c1, c2, l.p, l.q))
						return true;
				}
			}
		}
		return false;
	}

	public boolean selfCollision() {
		if (this.state.segments.size() < 3)
			return false;
		for (int i = 0; i < this.state.joints.size() - 2; i++) {
			Coordinate c1 = this.state.joints.get(i);
			Coordinate c2 = this.state.joints.get(i + 1);

			for (int j = i + 2; j < this.state.joints.size() - 1; j++) {
				Coordinate c3 = this.state.joints.get(j);
				Coordinate c4 = this.state.joints.get(j + 1);

				if (checker.testLineCollision(c1, c2, c3, c4))
					return true;
			}
		}
		return false;
	}

	public boolean collision() {
		boolean collide = false;
		collide = collide || this.selfCollision();
		collide = collide || this.obstacleCollision();
		return collide;
	}

}
