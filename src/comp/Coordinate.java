package comp;

import utils.GlobalCfg;

public class Coordinate {
	double X;
	double Y;

	public Coordinate(double x, double y) {
		this.X = x;
		this.Y = y;
	}

	@Override
	public int hashCode() {
		return GlobalCfg.prime1 * Double.valueOf(X).hashCode()
				+ GlobalCfg.prime5 * Double.valueOf(Y).hashCode();
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
