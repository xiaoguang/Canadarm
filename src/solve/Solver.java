package solve;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import comp.PathFinder;
import comp.ProblemAndSolution;
import comp.RobotState;
import comp.RobotState.RobotStateOutPut;

public class Solver {

	public static void main(String[] args) {
		ProblemAndSolution probNSolt = new ProblemAndSolution();

		try {
			Path p = Paths.get(args[0]);
			String fileName = p.getFileName().toString();
			String current = new java.io.File(".").getCanonicalPath();
			String outputPathFile = current.concat("/").concat("output")
					.concat("/").concat("output_path_").concat(fileName);
			String outputStepFile = current.concat("/").concat("output")
					.concat("/").concat("output_step_").concat(fileName);
			BufferedWriter writerPath = new BufferedWriter(
					new FileWriter(outputPathFile));
			BufferedWriter writerStep = new BufferedWriter(
					new FileWriter(outputStepFile));
			probNSolt.readProblemFromInput(args[0]);
			System.out.println(probNSolt.board.toString());

			PathFinder finder = new PathFinder(probNSolt.board);
			List<RobotState> path = finder.findPath();
			for (RobotState state : path) {
				RobotStateOutPut rso = new RobotState.RobotStateOutPut(state);
				writerPath.write(rso.toString().concat(System.lineSeparator()));
			}

			List<RobotState> steps = finder.smoothPath(path);
			for (RobotState state : steps) {
				RobotStateOutPut rso = new RobotState.RobotStateOutPut(state);
				writerStep.write(rso.toString().concat(System.lineSeparator()));
			}

			writerPath.close();
			writerStep.close();

			probNSolt.readSolutionFromInput(args[1]);
			for (RobotStateOutPut out : probNSolt.robotStates)
				System.out.println(out);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
