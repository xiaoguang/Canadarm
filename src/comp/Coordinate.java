package comp;

import utils.GlobalCfg;

public class Coordinate {

	double X;
	double Y;

	private Coordinate() {
	}

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
	public String toString() {
		String str = "";
		str = str.concat(" X " + this.X + " Y " + this.Y);
		return str;
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
		return ((Math.abs(this.X - c.X) < GlobalCfg.epsilon)
				&& (Math.abs(this.Y - c.Y) < GlobalCfg.epsilon));
	}

	@Override
	public Coordinate clone() {
		Coordinate coord = new Coordinate();
		coord.X = this.X;
		coord.Y = this.Y;
		return coord;
	}

}
