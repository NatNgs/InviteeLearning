package fr.unice.polytech.invitee.randomforest;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by nathael on 20/02/17.
 */
class DataSet implements Iterable<DataElement> {
	private final List<DataElement> elements = new ArrayList<>();

	DataSet(List<DataElement> list) {
		elements.addAll(list);
	}

	int getAmplitude() {
		int minX = elements.get(0).get(DataType.AccX);
		int maxX = elements.get(0).get(DataType.AccX);
		int minY = elements.get(0).get(DataType.AccY);
		int maxY = elements.get(0).get(DataType.AccY);
		int minZ = elements.get(0).get(DataType.AccZ);
		int maxZ = elements.get(0).get(DataType.AccZ);

		for(int i=elements.size()-1; i>=0; i--) {
			int accX = elements.get(i).get(DataType.AccX);
			int accY = elements.get(i).get(DataType.AccY);
			int accZ = elements.get(i).get(DataType.AccZ);

			if (minX > accX)
				minX = accX;
			if (maxX < accX)
				maxX = accX;

			if (minY > accY)
				minY = accY;
			if (maxY < accY)
				maxY = accY;

			if (minZ > accZ)
				minZ = accZ;
			if (maxZ < accZ)
				maxZ = accZ;
		}
		return maxX+maxY+maxZ -minX-minY-minZ;
	}

	@Override
	public Iterator<DataElement> iterator() {
		return elements.iterator();
	}

	public DataElement get(int i) {
		return elements.get(i);
	}

	public int size() {
		return elements.size();
	}
}
