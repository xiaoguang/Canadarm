package solve;

import comp.Board;
import comp.LocalPlanner;
import comp.ProblemAndSolution;
import comp.RobotState;
import comp.RobotState.RobotStateOutPut;

public class Solver {

	public static void main(String[] args) {
		ProblemAndSolution probNSolt = new ProblemAndSolution();
		LocalPlanner planner = new LocalPlanner();
		try {
			probNSolt.readProblemFromInput(args[0]);
			System.out.println(probNSolt.board.toString());
			probNSolt.readSolutionFromInput(args[1]);
			for (RobotStateOutPut out : probNSolt.robotStates)
				System.out.println(out);

			{
				RobotState from_state = Board.initRobotState.clone();
				RobotState to_state = Board.goalRobotState.clone();
				Board from = new Board(from_state);
				Board to = new Board(to_state);
				System.out.println(planner.validate(from, to));
				System.out.println(planner.reachable(from, to));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
