package comp;

import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProblemSpec {

	static class OutputFormat {
		String x1;
		String x2;
		String x3;

		Coordinate ee;
		

		OutputFormat(String[] input) {
			assertTrue(input.length == 3);
			x1 = input[0].trim();
			x2 = input[1].trim();
			x3 = input[2].trim();
		}

		public void transform() {
			String[] coord = x1.split(" ");
			ee = new Coordinate(Double.parseDouble(coord[0]),
					Double.parseDouble(coord[1]));
			
		}

		@Override
		public String toString() {
			String str = "";
			str = str.concat(x1).concat(" ; ");
			str = str.concat(x2).concat(" ; ");
			str = str.concat(x3).concat(" ; ");
			return str;
		}
	}

	public static void readOutput(String fileName) throws Exception {
		List<OutputFormat> list = new ArrayList<OutputFormat>();
		Stream<String> stream = Files.lines(Paths.get(fileName));
		list = (List<OutputFormat>) stream.map(p -> {
			return new OutputFormat(p.split(";"));
		}).collect(Collectors.toList());
		if (stream != null)
			stream.close();

		list.forEach(System.out::println);
	}

	public static Board readInput(String fileName) throws Exception {
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
				segs.add(new Segment(Double.parseDouble(mins[z]),
						Double.parseDouble(maxs[z]),
						Double.parseDouble(lengths[z]),
						new AngleInDegree(Double.parseDouble(degrees[z]))));
			}
			if (grappleIndex == 2) {
				Collections.reverse(segs);
			}
			state = new RobotState(new Coordinate(Double.parseDouble(coord[0]),
					Double.parseDouble(coord[1])), segs);
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
				segs.add(new Segment(Double.parseDouble(mins[z]),
						Double.parseDouble(maxs[z]),
						Double.parseDouble(lengths[z]),
						new AngleInDegree(Double.parseDouble(degrees[z]))));
			}
			if (grappleIndex == 2) {
				Collections.reverse(segs);
			}
			Board.goalRobotState = new RobotState(
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
			Board.obstacles.add(new Obstacle(bl, tr));
		}

		return new Board(state);
	}

}
