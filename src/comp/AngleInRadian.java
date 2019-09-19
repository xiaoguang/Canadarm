package comp;

public class AngleInRadian extends Angle {

	public AngleInRadian(double radian) {
		super.radian = radian;
		super.normalize();
	}

}
