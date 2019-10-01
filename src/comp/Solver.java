package comp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import comp.RobotState.RobotStateOutPut;

public class Solver {

	public static void main(String[] args) {
		ProblemAndSolution probNSolt = new ProblemAndSolution();
		Planner planner = new Planner();

		try {
			Path p = Paths.get(args[0]);
			String fileName = p.getFileName().toString();
			String current = new java.io.File(".").getCanonicalPath();
			String outputFile = current.concat("/").concat("output").concat("/")
					.concat("output_").concat(fileName);
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(outputFile));

			probNSolt.readProblemFromInput(args[0]);
			System.out.println(probNSolt.board.toString());

			RobotState from_state = Board.initRobotState.clone();
			RobotState to_state = Board.goalRobotState.clone();

			System.out.println(planner.validate(from_state, to_state));
			System.out.println(planner.reachable(from_state, to_state));

			/*-
			List<Board> steps = planner.generateSteps(from, to);
			for (Board b : steps) {
				RobotStateOutPut rso = new RobotState.RobotStateOutPut(b.state);
				System.out.println(rso);
				writer.write(rso.toString().concat(System.lineSeparator()));
			}
			*/

			RRT rrt = new RRT(new Node(Board.initRobotState));

			while (rrt.size() < 50000) {
				RobotState s = planner.randomSampling(from_state);
				rrt.addNode(new Node(s));
				System.out.println(rrt.size());
				// RobotStateOutPut rso = new RobotState.RobotStateOutPut(s);
				// writer.write(rso.toString().concat(System.lineSeparator()));
			}

			double dist = Double.MAX_VALUE;
			Node minDistNode = null;
			for (Node node : rrt.sampled) {
				double localDist = node.rs.distance(Board.goalRobotState);
				List<RobotState> moves = planner
						.generateSteps(Board.goalRobotState, node.rs);
				if (moves != null && localDist < dist) {
					dist = localDist;
					minDistNode = node;
				}
			}

			Node goal = new Node(Board.goalRobotState);
			if (minDistNode != null) {
				List<RobotState> moves = planner.generateSteps(minDistNode.rs,
						Board.goalRobotState);
				if (moves != null) {
					minDistNode.addChildren(goal);
				}
			}

			Node curr = goal;
			while (curr != null) {
				RobotStateOutPut rso = new RobotState.RobotStateOutPut(curr.rs);
				writer.write(rso.toString().concat(System.lineSeparator()));
				curr = curr.parent;
			}

			writer.close();

			probNSolt.readSolutionFromInput(args[1]);
			for (RobotStateOutPut out : probNSolt.robotStates)
				System.out.println(out);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
