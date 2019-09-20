package comp;

public class Obstacle {
	Coordinate bl;
	Coordinate tr;

	@SuppressWarnings("unused")
	private Obstacle() {
	}

	public Obstacle(Coordinate bl, Coordinate tr) {
		this.bl = bl;
		this.tr = tr;
	}

	@Override
	public String toString() {
		String str = "";
		str = str.concat("BL : " + this.bl + "TR : " + this.tr);
		return str;
	}
}
