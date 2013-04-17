package mazestormer.geom;

public final class FractionSegment {

	private final double leftFraction;
	private final double rightFraction;

	public FractionSegment(double left, double right) {
		super();
		this.leftFraction = left;
		this.rightFraction = right;
	}

	public double left() {
		return leftFraction;
	}

	public double right() {
		return rightFraction;
	}

	public boolean contains(double fraction) {
		return left() <= fraction && fraction <= right();
	}

	public boolean contains(FractionSegment otherSegment) {
		return left() <= otherSegment.left() && otherSegment.right() <= right();
	}

	public boolean overlaps(FractionSegment otherSegment) {
		return otherSegment.left() <= right() && left() <= otherSegment.right();
	}

	public FractionSegment merge(FractionSegment otherSegment) {
		if (!overlaps(otherSegment)) {
			throw new IllegalArgumentException("Segments must overlap.");
		}
		double left = Math.min(left(), otherSegment.left());
		double right = Math.max(right(), otherSegment.right());
		return new FractionSegment(left, right);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(leftFraction);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(rightFraction);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FractionSegment other = (FractionSegment) obj;
		if (Double.doubleToLongBits(leftFraction) != Double.doubleToLongBits(other.leftFraction))
			return false;
		if (Double.doubleToLongBits(rightFraction) != Double.doubleToLongBits(other.rightFraction))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + left() + ", " + right() + "]";
	}

}