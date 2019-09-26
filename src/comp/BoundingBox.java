package comp;

import java.util.ArrayList;
import java.util.List;

public class BoundingBox {

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
