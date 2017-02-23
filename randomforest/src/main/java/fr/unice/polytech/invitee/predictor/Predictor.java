package fr.unice.polytech.invitee.predictor;

import fr.unice.polytech.invitee.utils.*;

import java.io.*;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by nathael on 22/02/17.
 */
public class Predictor {
	private static final String thisPath = new File("").getAbsolutePath();

	private String pythonScriptPath = null;
	private String pklFilePath = null;

	/**
	 *
	 * @param pklFilePath
	 */
	@SuppressWarnings("WeakerAccess")
	public void setPklFilePath(String pklFilePath) {
		this.pklFilePath = pklFilePath;
	}

	/**
	 *
	 * @param pythonScriptPath
	 */
	@SuppressWarnings("WeakerAccess")
	public void setPythonScriptPath(String pythonScriptPath) {
		this.pythonScriptPath = pythonScriptPath;
	}


	/**
	 * @return Value corresponding to the prediction
	 * @param dataSet The dataset to use for predicting
	 * @throws InterruptedException When interrupted while Python running or if Python script was too long
	 * @throws IOException When python script not found or not readable
	 */
	@SuppressWarnings("WeakerAccess")
	public int treat(DataSet dataSet) throws InterruptedException, IOException {
		Process p = Runtime.getRuntime().exec(new String[]{"python", pythonScriptPath});

		// passing arguments
		Writer writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		// argument 1 is RandomForest save file path
		writer.write(pklFilePath+'\n');
		writer.flush();

		// argument 2 is path to the file to predict
		writer.write(createVectorFor(dataSet)+'\n');
		writer.flush();
		writer.close();

		// getting values
		p.waitFor(10, TimeUnit.SECONDS); // Maximum time to run, otherwise throw interrupted exception

		if(error.ready()) {
			String errStr = "";
			String line;
			while((line=error.readLine())!=null) {
				errStr += "\n"+line;
			}

			if (!errStr.isEmpty()) {
				throw new RuntimeException("Python error: " + errStr);
			}
		}

		String line = reader.readLine();
		error.close();
		reader.close();
		p.destroy();

		Scanner sc = new Scanner(line);

		if(sc.hasNext()) {
			return sc.next().charAt(0) - '0';
		} else {
			return -1;
		}
	}

	/** For an usage as console user (will prompt for inputs)
	 * @param args No argument needed
	 */
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		Predictor predictor = new Predictor();

		System.out.print("Type path to RF_Predict.py: " + thisPath + "/");
		predictor.setPythonScriptPath(sc.nextLine());

		System.out.print("Type path to randomforest.pkl: " + thisPath + "/");
		predictor.setPklFilePath(sc.nextLine());

		System.out.print("Input file or folder: " + thisPath + "/");
		final String inputFolder = sc.nextLine();
		File inF = new File(inputFolder);
		Set<File> inFs = Utils.listSubFiles(inF);
		if (inFs.isEmpty()) {
			System.err.println("Input file/folder is empty or does not exists.");
			return;
		}

		System.out.println();
		sc.close();

		for(File f : inFs) {
			try {
				DataSet dataSet = DataSetBuilder.extract(f);
				if(dataSet != null) {
					System.out.print("Treating " + f.getPath() + "...");
					int result = predictor.treat(dataSet);
					System.out.println(" Predicted result: " + result);
				}
			} catch (FileNotFoundException e) {
				System.err.println("Input file does not exists or cannot be read: "+e.toString());
			} catch (InterruptedException e) {
				System.err.println("Operation interrupted before was finished: "+e.toString());
			} catch (IOException e) {
				System.err.println("An error occurred: "+e.toString());
			}
		}
	}

	private String createVectorFor(DataSet dataSet) {
		// "rotX0;rotY0;rotZ0;accX0;accY0;accZ0;...;accZ499"
		StringBuilder result = new StringBuilder();

		int mid = dataSet.size();
		int lastQuarter = mid * 3 / 4;
		mid /= 2;

		int i = 0;
		while(i<dataSet.size()) {
			DataElement element = dataSet.get(i);

			result.append(" ")
					.append(String.valueOf(element.get(DataType.RotX)))
					.append(" ")
					.append(element.get(DataType.RotY))
					.append(" ")
					.append(element.get(DataType.RotZ))
					.append(" ")
					.append(element.get(DataType.AccX))
					.append(" ")
					.append(element.get(DataType.AccY))
					.append(" ")
					.append(element.get(DataType.AccZ));

			if(i>lastQuarter) {
				i++;
			} else if(i > mid) {
				i+=2;
			} else {
				i+=3;
			}
		}

		return result.toString();
	}
}
