package mazestormer.maze;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import mazestormer.barcode.Barcode;

public class Island {

	private final Set<Barcode> seesawBarcodes = new HashSet<Barcode>();

	public Set<Barcode> getSeesawBarcodes() {
		return Collections.unmodifiableSet(seesawBarcodes);
	}

	public boolean hasSeesawBarcode(Barcode seesawBarcode) {
		return seesawBarcodes.contains(checkNotNull(seesawBarcode));
	}

	public void addSeesawBarcode(Barcode seesawBarcode) {
		seesawBarcodes.add(checkNotNull(seesawBarcode));
	}

	public void addSeesawBarcodes(Collection<Barcode> seesawBarcodes) {
		seesawBarcodes.addAll(checkNotNull(seesawBarcodes));
	}

	public Barcode getSeesawBarcode(Seesaw seesaw) {
		if (hasSeesawBarcode(seesaw.getLowestBarcode())) {
			return seesaw.getLowestBarcode();
		} else if (hasSeesawBarcode(seesaw.getHighestBarcode())) {
			return seesaw.getHighestBarcode();
		} else {
			return null;
		}
	}

	public boolean hasSeesaw(Seesaw seesaw) {
		return getSeesawBarcode(seesaw) != null;
	}

	public void merge(Island otherIsland) {
		this.addSeesawBarcodes(otherIsland.getSeesawBarcodes());
		otherIsland.addSeesawBarcodes(this.getSeesawBarcodes());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seesawBarcodes == null) ? 0 : seesawBarcodes.hashCode());
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
		Island other = (Island) obj;
		if (seesawBarcodes == null) {
			if (other.seesawBarcodes != null)
				return false;
		} else if (!seesawBarcodes.equals(other.seesawBarcodes))
			return false;
		return true;
	}

}
