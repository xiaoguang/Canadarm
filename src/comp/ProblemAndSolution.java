package comp;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import comp.RobotState.RobotStateOutPut;

public class ProblemAndSolution {

	private boolean problemLoaded;
	private boolean solutionLoaded;

	public Board board;
	public List<RobotStateOutPut> robotStates;

	public ProblemAndSolution() {
		this.problemLoaded = false;
		this.solutionLoaded = false;
		this.board = new Board();
		this.robotStates = null;
	}

	public boolean isProblemLoaded() {
		return problemLoaded;
	}

	public boolean isSolutionLoaded() {
		return solutionLoaded;
	}

	public void readSolutionFromInput(String fileName) throws Exception {
		if (this.isSolutionLoaded())
			return;
		List<RobotStateOutPut> list = new ArrayList<RobotStateOutPut>();
		Stream<String> stream = Files.lines(Paths.get(fileName));
		list = (List<RobotStateOutPut>) stream.filter(p -> !p.trim().isEmpty())
				.map(p -> {
					return new RobotStateOutPut(p.split(";"));
				}).collect(Collectors.toList());

		if (stream != null)
			stream.close();

		this.robotStates = list;
		this.solutionLoaded = true;
	}

	public void readProblemFromInput(String fileName) throws Exception {
		if (this.isProblemLoaded())
			return;
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
				state = RobotState.createRobotStateFromEE2(
						new Coordinate(Double.parseDouble(coord[0]),
								Double.parseDouble(coord[1])),
						segs);
			} else {
				state = RobotState.createRobotStateFromEE1(
						new Coordinate(Double.parseDouble(coord[0]),
								Double.parseDouble(coord[1])),
						segs);

			}
			this.board.initRobotState = state.clone();
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
				this.board.goalRobotState = RobotState.createRobotStateFromEE2(
						new Coordinate(Double.parseDouble(coord[0]),
								Double.parseDouble(coord[1])),
						segs);
			} else {
				this.board.goalRobotState = RobotState.createRobotStateFromEE1(
						new Coordinate(Double.parseDouble(coord[0]),
								Double.parseDouble(coord[1])),
						segs);
			}

		}

		idx += 1;
		int numberOfGrapples = Integer.parseInt(list.get(idx));

		idx += 1;
		int endGrapples = idx + numberOfGrapples;

		for (; idx < endGrapples; idx++) {
			String[] coord = list.get(idx).split(" ");
			this.board.grapples.add(new Coordinate(Double.parseDouble(coord[0]),
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

		this.board.state = state;
		this.problemLoaded = true;
	}

}
