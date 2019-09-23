package solve;

import comp.ProblemAndSolution;
import comp.RobotState.RobotStateOutPut;

public class Solver {

	public static void main(String[] args) {
		ProblemAndSolution probNSolt = new ProblemAndSolution();
		try {
			probNSolt.readProblemFromInput(args[0]);
			System.out.println(probNSolt.board.toString());
			probNSolt.readSolutionFromInput(args[1]);
			for (RobotStateOutPut out : probNSolt.robotStates)
				System.out.println(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
