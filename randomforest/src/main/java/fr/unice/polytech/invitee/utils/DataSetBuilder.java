package fr.unice.polytech.invitee.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by nathael on 20/02/17.
 */
public class DataSetBuilder {
	private static final int PRECISION_IN_FILE = 100_000;
	private static final DataType[] TYPES_IN_FILE =
			// timer,roll,pitch,yaw,accel_x,accel_y,accel_z,posx,posy,posz
			new DataType[] {
				DataType.Timer,
				DataType.RotX,
				DataType.RotY,
				DataType.RotZ,
				DataType.AccX,
				DataType.AccY,
				DataType.AccZ,
				DataType.PosX,
				DataType.PosY,
				DataType.PosZ};
	private static final int MAX_SET_SIZE = 500;

	private Scanner cur_fileScanner = null;
	private final List<DataElement> cur_dataElementList = new ArrayList<>();

	private DataSetBuilder() {}

	private void reset() {
		if(cur_fileScanner != null) {
			cur_fileScanner.close();
			cur_fileScanner = null;
		}

		cur_dataElementList.clear();
	}

	private void initImport(File f) throws FileNotFoundException {
		reset();
		cur_fileScanner = new Scanner(f);
	}

	private boolean hasNext() {
		return cur_fileScanner.hasNextLine();
	}
	private DataSet importNext() {
		while(true) {
			if(!cur_fileScanner.hasNextLine()) // End of File
				return null;

			try {
				String[] tab = cur_fileScanner.nextLine().replaceAll(" ", "").split(",");

				DataElement element = new DataElement(TYPES_IN_FILE, tab, PRECISION_IN_FILE);

				cur_dataElementList.add(element);

				if (cur_dataElementList.size() >= MAX_SET_SIZE) {
					DataSet ret = new DataSet(cur_dataElementList);
					cur_dataElementList.remove(0);
					return ret;
				}
			} catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {} // ignore lines that does not math the correct format (header line & comments)
		}
	}
	public static DataSet extract(File f) throws FileNotFoundException {
		DataSetBuilder dsb = new DataSetBuilder();

		dsb.initImport(f);
		DataSet bestDataSet = dsb.importNext();

		if(bestDataSet == null)
			return null;

		int bestDataSetAmplitude = bestDataSet.getAmplitude();

		while(dsb.hasNext()) {
			DataSet nextDataSet = dsb.importNext();
			assert nextDataSet != null;

			int nextDataSetAmplitude = nextDataSet.getAmplitude();
			if(nextDataSetAmplitude > bestDataSetAmplitude) {
				bestDataSet = nextDataSet;
				bestDataSetAmplitude = nextDataSetAmplitude;
			}
		}
		return bestDataSet;
	}
}
