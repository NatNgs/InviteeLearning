package fr.unice.polytech.invitee.feeder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nathael on 20/02/17.
 */
class DataElement {
	private final Map<DataType, Integer> values = new HashMap<>();

	DataElement(DataType[] types, String[] values, int precision) {
		assert types.length == values.length;

		for(int i=types.length-1; i>=0; i--) {
			this.values.put(types[i], (int)(Float.valueOf(values[i])*precision));
		}
	}

	int get(DataType type) {
		return values.get(type);
	}
}
