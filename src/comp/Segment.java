package comp;

public class Segment implements Cloneable {

	double min;
	double max;
	double len;
	Angle angle;

	@SuppressWarnings("unused")
	private Segment() {
	}

	public Segment(double min, double max, double len, Angle angle) {
		this.min = min;
		this.max = max;
		this.len = len;
		this.angle = angle;
	}

	@Override
	public String toString() {
		String str = "";
		str = str.concat("Segment :" + System.lineSeparator());
		str = str.concat(
				"ARM : " + "MIN " + min + " MAX " + max + " LEN " + len);
		str = str.concat(System.lineSeparator() + angle.toString());
		return str;
	}

	@Override
	public Segment clone() {
		Segment seg = new Segment(this.min, this.max, this.len,
				this.angle.clone());
		return seg;
	}

}
