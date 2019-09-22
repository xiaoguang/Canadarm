package comp;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {

	static RobotState initRobotState;
	static RobotState goalRobotState;
	RobotState state;
	CollisionCheck checker;

	static final List<Coordinate> grapples = new ArrayList<Coordinate>();
	static final List<BoundingBox> obstacles = new ArrayList<BoundingBox>();

	private Board() {
	}

	private Board(RobotState state) {
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

	public static Board readBoardFromInput(String fileName) throws Exception {
		List<String> list = new ArrayList<String>();
		Stream<String> stream = Files.lines(Paths.get(fileName));
		list = (List<String>) stream.filter(p -> !p.startsWith("#"))
				.filter(p -> !p.trim().isEmpty()).collect(Collectors.toList());

		if (stream != null)
			stream.close();

		RobotState state;

		int idx = 0;
		int numberOfSegments = Integer.parseInt(list.get(idx));

		idx += 1;
		String[] mins = list.get(idx).split(" ");
		idx += 1;
		String[] maxs = list.get(idx).split(" ");

		{
			List<Segment> segs = new ArrayList<Segment>(numberOfSegments);
			idx += 1;
			int grappleIndex = Integer.parseInt(list.get(idx));
			idx += 1;
			String[] coord = list.get(idx).split(" ");
			idx += 1;
			String[] degrees = list.get(idx).split(" ");
			idx += 1;
			String[] lengths = list.get(idx).split(" ");

			for (int z = 0; z < numberOfSegments; z++) {
				double degree = Double.parseDouble(degrees[z]);
				if (grappleIndex == 2) {
					degree = Double
							.parseDouble(degrees[numberOfSegments - 1 - z]);
				}
				segs.add(new Segment(Double.parseDouble(mins[z]),
						Double.parseDouble(maxs[z]),
						Double.parseDouble(lengths[z]),
						new AngleInDegree(degree)));
			}
			if (grappleIndex == 2) {
				Collections.reverse(segs);
			}

			state = new RobotState(grappleIndex,
					new Coordinate(Double.parseDouble(coord[0]),
							Double.parseDouble(coord[1])),
					segs);
			Board.initRobotState = state.clone();
		}

		{
			List<Segment> segs = new ArrayList<Segment>(numberOfSegments);
			idx += 1;
			int grappleIndex = Integer.parseInt(list.get(idx));
			idx += 1;
			String[] coord = list.get(idx).split(" ");
			idx += 1;
			String[] degrees = list.get(idx).split(" ");
			idx += 1;
			String[] lengths = list.get(idx).split(" ");

			for (int z = 0; z < numberOfSegments; z++) {
				double degree = Double.parseDouble(degrees[z]);
				if (grappleIndex == 2) {
					degree = Double
							.parseDouble(degrees[numberOfSegments - 1 - z]);
				}
				segs.add(new Segment(Double.parseDouble(mins[z]),
						Double.parseDouble(maxs[z]),
						Double.parseDouble(lengths[z]),
						new AngleInDegree(degree)));
			}
			if (grappleIndex == 2) {
				Collections.reverse(segs);
			}

			Board.goalRobotState = new RobotState(grappleIndex,
					new Coordinate(Double.parseDouble(coord[0]),
							Double.parseDouble(coord[1])),
					segs);
		}

		idx += 1;
		int numberOfGrapples = Integer.parseInt(list.get(idx));

		idx += 1;
		int endGrapples = idx + numberOfGrapples;

		for (; idx < endGrapples; idx++) {
			String[] coord = list.get(idx).split(" ");
			Board.grapples.add(new Coordinate(Double.parseDouble(coord[0]),
					Double.parseDouble(coord[1])));
		}

		int numberOfObstacles = Integer.parseInt(list.get(idx));

		idx += 1;
		int endObstacles = idx + numberOfObstacles;

		for (; idx < endObstacles; idx++) {
			String[] coord = list.get(idx).split(" ");
			Coordinate bl = new Coordinate(Double.parseDouble(coord[0]),
					Double.parseDouble(coord[1]));
			Coordinate tr = new Coordinate(Double.parseDouble(coord[2]),
					Double.parseDouble(coord[3]));
			Board.obstacles.add(new BoundingBox(bl, tr));
		}

		return new Board(state);
	}

}
