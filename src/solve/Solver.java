package solve;

import java.util.List;

import comp.Board;
import comp.RobotState;
import comp.RobotState.RobotStateOutPut;

public class Solver {

	public static void main(String[] args) {
		try {
			Board board = Board.readBoardFromInput(args[0]);
			System.out.println(board.toString());
			List<RobotStateOutPut> rso = RobotState
					.readRobotStateFromInput(args[1]);
			for (RobotStateOutPut out : rso)
				System.out.println(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
