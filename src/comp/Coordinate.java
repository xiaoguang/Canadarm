package comp;

import utils.GlobalConfig;

public class Coordinate {
	double X;
	double Y;

	public Coordinate(double x, double y) {
		this.X = x;
		this.Y = y;
	}

	@Override
	public int hashCode() {
		return GlobalConfig.prime1 * Double.valueOf(X).hashCode()
				+ GlobalConfig.prime5 * Double.valueOf(Y).hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object == this)
			return true;
		if (this.getClass() != object.getClass())
			return false;
		Coordinate c = (Coordinate) object;
		return ((this.X == c.X) && (this.Y == c.Y));
	}

}
