package fr.unice.polytech.invitee.randomforest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nathael on 20/02/17.
 */
public class DataSet extends ArrayList<DataElement> {
	public DataSet(List<DataElement> list) {
		this.addAll(list);
	}
}
