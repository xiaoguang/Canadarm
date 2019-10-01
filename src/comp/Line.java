package comp;

public class Line {

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
