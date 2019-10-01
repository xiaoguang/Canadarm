package solve;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import comp.Board;
import comp.PathFinder;
import comp.ProblemAndSolution;
import comp.RobotState;
import comp.RobotState.RobotStateOutPut;

public class Solver {

	public static void main(String[] args) {
		ProblemAndSolution probNSolt = new ProblemAndSolution();
		PathFinder finder = new PathFinder();

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

			List<RobotState> path = finder.findPath(Board.initRobotState,
					Board.goalRobotState);
			for (RobotState state : path) {
				RobotStateOutPut rso = new RobotState.RobotStateOutPut(state);
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
