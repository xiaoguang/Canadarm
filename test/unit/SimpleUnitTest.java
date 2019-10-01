package unit;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import comp.Board;
import comp.Planner;
import comp.ProblemAndSolution;
import comp.RobotState;

public class SimpleUnitTest {

	@Test
	public void CollisionTest() throws Exception {
		Planner planner = new Planner();
		ProblemAndSolution probNSolt = new ProblemAndSolution();
		probNSolt.readProblemFromInput("input/3g1_m0.txt");
		RobotState from_state = Board.initRobotState.clone();
		RobotState to_state = Board.goalRobotState.clone();
		assertTrue(planner.validate(from_state, to_state));
		assertTrue(planner.reachable(from_state, to_state));
	}

}
