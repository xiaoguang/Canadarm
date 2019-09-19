package comp;

public class AngleInDegree extends Angle {

	public AngleInDegree(double degree) {
		super.radian = degree * Math.PI / 180;
		super.normalize();
	}

}
