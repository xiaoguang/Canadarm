package comp;

public class CollisionCheck {

	public boolean testBoundingBoxCollision(Coordinate p1, Coordinate q1,
			Coordinate p2, Coordinate q2) {
		double p1x = p1.X;
		double p1y = p1.Y;
		double q1x = q1.X;
		double q1y = q1.Y;

		double p2x = p2.X;
		double p2y = p2.Y;
		double q2x = q2.X;
		double q2y = q2.Y;

		double x1Min = Math.min(p1x, q1x);
		double x1Max = Math.max(p1x, q1x);
		double x2Min = Math.min(p2x, q2x);
		double x2Max = Math.max(p2x, q2x);

		if (x1Max < x2Min || x2Max < x1Min)
			return false;

		double y1Min = Math.min(p1y, q1y);
		double y1Max = Math.max(p1y, q1y);
		double y2Min = Math.min(p2y, q2y);
		double y2Max = Math.max(p2y, q2y);

		if (y1Max < y2Min || y2Max < y1Min)
			return false;

		return true;
	}

	public boolean testBoundingBoxCollision(BoundingBox b1, BoundingBox b2) {
		return testBoundingBoxCollision(b1.bl, b1.tr, b2.bl, b2.tr);
	}

	public double determinant(Coordinate p, Coordinate q) {
		return this.determinant(p.X, p.Y, q.X, q.Y);
	}

	public double determinant(double px, double py, double qx, double qy) {
		return px * qy - py * qx;
	}

	public int triangleOrientation(Coordinate p, Coordinate q, Coordinate r) {
		double area = this.determinant(p, q) + this.determinant(q, r)
				- this.determinant(p, r);
		if (area > 0)
			return 1;
		else if (area < 0)
			return -1;
		return 0;
	}

	public boolean testOrientationCollision(Coordinate p1, Coordinate q1,
			Coordinate p2, Coordinate q2) {
		if (this.triangleOrientation(p1, q1, p2) == this.triangleOrientation(p1,
				q1, q2))
			return false;
		if (this.triangleOrientation(p2, q2, p1) == this.triangleOrientation(p2,
				q2, q1))
			return false;
		return true;
	}

	public boolean testLineCollision(Line l1, Line l2) {
		Coordinate p1 = l1.p;
		Coordinate q1 = l1.q;
		Coordinate p2 = l2.p;
		Coordinate q2 = l2.q;
		return this.testLineCollision(p1, q1, p2, q2);
	}

	public boolean testLineCollision(Coordinate p1, Coordinate q1,
			Coordinate p2, Coordinate q2) {
		if (!this.testBoundingBoxCollision(p1, q1, p2, q2))
			return false;
		return this.testOrientationCollision(p1, q1, p2, q2);
	}

}
