package fr.unice.polytech.invitee.randomforest;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by nathael on 20/02/17.
 */
public class DatasetBuilder {
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

	private final Stack<DataSet> dataSets = new Stack<>();

	private boolean isInitialised = false;
	private File cur_file = null;
	private Scanner cur_fileScanner = null;
	private final List<DataElement> cur_dataElementList = new ArrayList<>();
	private final Map<DataType, Double> cur_sum = new HashMap<>();

	public void reset() {
		if(cur_fileScanner != null) {
			cur_fileScanner.close();
			cur_fileScanner = null;
		}

		cur_sum.clear();
		cur_dataElementList.clear();
		isInitialised = false;
	}

	public void setFile(File f) throws FileNotFoundException {
		this.cur_fileScanner = new Scanner(f);
	}

	public void initImport() throws CannotInitializeException {
		try {
			reset();
			cur_fileScanner = new Scanner(cur_file);
			cur_sum.put(DataType.AccX, 0.);
			cur_sum.put(DataType.AccY, 0.);
			cur_sum.put(DataType.AccZ, 0.);
			while (cur_dataElementList.size() < MAX_SET_SIZE && hasNext())
				importNext();

			if (cur_dataElementList.size() < MAX_SET_SIZE)
				throw new CannotInitializeException();
		} catch (FileNotFoundException e) {
			throw new CannotInitializeException(e);
		}

		isInitialised = true;
	}

	public boolean hasNext() {
		if(!isInitialised)
			throw new NotInitializedException();

		return cur_fileScanner.hasNextLine();
	}
	public DataSet importNext() {
		try {
			initImport();
		} catch (CannotInitializeException cie) {
			throw new NotInitializedException(cie);
		}

		while(true) {
			if(!cur_fileScanner.hasNextLine())
				return null;

			try {
				String newline = cur_fileScanner.nextLine();
				String[] tab = newline.replaceAll(" ", "").split(",");

				DataElement element = new DataElement(TYPES_IN_FILE, tab, PRECISION_IN_FILE);

				cur_dataElementList.add(element);
				if (cur_dataElementList.size() >= MAX_SET_SIZE) {



					DataSet ret = new DataSet(cur_dataElementList);
					cur_dataElementList.remove(0);
					return ret;
				}
			} catch (NumberFormatException ignored) {}
		}
	}

	class NotInitializedException extends RuntimeException{
		NotInitializedException() {
			super("Builder is not initialised ! Call initImport() first.");
		}
		NotInitializedException(CannotInitializeException cie) {
			super("Builder was not initialised, tried to initialize but failed.", cie);
		}
	}
	class CannotInitializeException extends Exception{
		CannotInitializeException() {
			super("Cannot init import: not enough data");
		}
		CannotInitializeException(Exception e) {
			super("Cannot init import", e);
		}
	}
}
