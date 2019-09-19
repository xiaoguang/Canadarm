package comp;

import utils.GlobalCfg;

public abstract class Angle {

	double degree;
	double radian;

	protected void normalize() {
		this.radian = (radian + Math.PI) % (2 * Math.PI) - Math.PI;
		this.degree = this.radian * 180 / Math.PI;
	}

	public double getDegree() {
		return this.degree;
	}

	public double getRadian() {
		return this.radian;
	}

	public Angle add(Angle ang) {
		this.radian += ang.radian;
		this.normalize();
		return this;
	}

	public Angle minus(Angle ang) {
		this.radian -= ang.radian;
		this.normalize();
		return this;
	}

	public Angle multiply(Angle ang) {
		this.radian *= ang.radian;
		this.normalize();
		return this;
	}

	public Angle divide(Angle ang) {
		this.radian /= ang.radian;
		this.normalize();
		return this;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null)
			return false;
		if (object == this)
			return true;
		if (this.getClass() != object.getClass())
			return false;
		Angle a = (Angle) object;
		return Math.abs(this.radian - a.radian) < GlobalCfg.epsilon;
	}
}
