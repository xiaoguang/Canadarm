package comp;

import java.util.List;

public class GlobalPlanner {

	public Board randomSampling(Board board) {
		boolean found = false;
		Board sample = board.clone();
		List<Segment> local = sample.state.segments;

		while (!found) {
			for (Segment seg : local) {
				Angle ang = new AngleInRadian(
						RoboticUtilFunctions.uniformAngleSampling());
				seg.angle = ang;
			}

			sample.state.calcJoints();
			if (!sample.collision())
				found = true;
		}

		return sample;
	}

}
