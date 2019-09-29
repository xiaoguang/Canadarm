package solve;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import comp.Board;
import comp.Planner;
import comp.ProblemAndSolution;
import comp.RobotState;
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

			for (int i = 0; i < 100; i++) {
				RobotState s = planner.randomSampling(from_state);
				RobotStateOutPut rso = new RobotState.RobotStateOutPut(s);
				writer.write(rso.toString().concat(System.lineSeparator()));
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
