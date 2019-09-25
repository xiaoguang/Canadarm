package unit;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import comp.Angle;
import comp.AngleInDegree;
import comp.Board;
import comp.LocalPlanner;
import comp.ProblemAndSolution;
import comp.RobotState;
import utils.GlobalCfg;

public class SimpleUnitTest {

	@Test
	public void AngleTest0() {
		Angle a = new AngleInDegree(180.0);
		assertTrue(Math.abs(a.getDegree() - (-180.0)) <= GlobalCfg.epsilon);
		assertTrue(Math.abs(a.getRadian() - (-Math.PI)) <= GlobalCfg.epsilon);
	}

	@Test
	public void AngleTest1() {
		Angle a = new AngleInDegree(181.0);
		assertTrue(Math.abs(a.getDegree() - (-179.0)) <= GlobalCfg.epsilon);
		assertTrue(Math.abs(a.getRadian() - (-3.12414)) <= GlobalCfg.epsilon);
	}

	@Test
	public void AngleTest2() {
		Angle a = new AngleInDegree(90.0);
		assertTrue(Math.abs(a.getDegree() - (90.0)) <= GlobalCfg.epsilon);
		assertTrue(Math.abs(a.getRadian() - (1.570796)) <= GlobalCfg.epsilon);
	}

	@Test
	public void AngleTest3() {
		Angle a = new AngleInDegree(-90.0);
		assertTrue(Math.abs(a.getDegree() - (-90.0)) <= GlobalCfg.epsilon);
		assertTrue(Math.abs(a.getRadian() - (-1.570796)) <= GlobalCfg.epsilon);
	}

	@Test
	public void AngleTest4() {
		Angle a = new AngleInDegree(270.0);
		assertTrue(Math.abs(a.getDegree() - (-90.0)) <= GlobalCfg.epsilon);
		assertTrue(Math.abs(a.getRadian() - (-1.570796)) <= GlobalCfg.epsilon);
	}

	@Test
	public void AngleTest5() {
		Angle a = new AngleInDegree(360.0);
		assertTrue(Math.abs(a.getDegree() - (0.0)) <= GlobalCfg.epsilon);
		assertTrue(Math.abs(a.getRadian() - (0.0)) <= GlobalCfg.epsilon);
	}

	@Test
	public void AngleTest6() {
		Angle a = new AngleInDegree(179.0);
		assertTrue(Math.abs(a.getDegree() - (179.0)) <= GlobalCfg.epsilon);
		assertTrue(Math.abs(a.getRadian() - (3.12414)) <= GlobalCfg.epsilon);
	}

	@Test
	public void CollisionTest() throws Exception {
		ProblemAndSolution probNSolt = new ProblemAndSolution();
		LocalPlanner planner = new LocalPlanner();
		probNSolt.readProblemFromInput("input/3g1_m0.txt");
		RobotState from_state = Board.initRobotState.clone();
		RobotState to_state = Board.goalRobotState.clone();
		Board from = new Board(from_state);
		Board to = new Board(to_state);
		assertTrue(planner.validate(from, to));
		assertTrue(planner.reachable(from, to));
	}
}
