package comp;

import java.util.ArrayList;
import java.util.List;

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

class Line {

	public Coordinate p;
	public Coordinate q;

	@SuppressWarnings("unused")
	private Line() {
	}

	public Line(Coordinate p, Coordinate q) {
		this.p = p;
		this.q = q;
	}

	@Override
	public String toString() {
		String str = "";
		str = str.concat("P : " + this.p + "  " + "Q : " + this.q);
		return str;
	}

}

class BoundingBox {

	Coordinate bl;
	Coordinate tr;
	List<Coordinate> corners;
	public List<Line> edges;

	@SuppressWarnings("unused")
	private BoundingBox() {
		this.bl = null;
		this.tr = null;
		this.corners = null;
		this.edges = null;
	}

	public BoundingBox(Coordinate bl, Coordinate tr) {
		if (tr.X <= bl.X)
			System.exit(-1);
		if (tr.Y <= bl.Y)
			System.exit(-1);
		this.bl = bl;
		this.tr = tr;

		this.corners = new ArrayList<Coordinate>();
		this.corners.add(this.bl);
		this.corners.add(new Coordinate(this.bl.X, this.tr.Y));
		this.corners.add(this.tr);
		this.corners.add(new Coordinate(this.tr.X, this.bl.Y));

		this.edges = new ArrayList<Line>();
		for (int i = 0; i < this.corners.size(); i++) {
			int e = (i + 1) % this.corners.size();
			this.edges.add(new Line(this.corners.get(i), this.corners.get(e)));
		}
	}

	@Override
	public String toString() {
		String str = "";
		str = str.concat("BL : " + this.bl + "  " + "TR : " + this.tr);
		return str;
	}

}
