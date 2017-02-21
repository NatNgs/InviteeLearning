package fr.unice.polytech.invitee.randomforest;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by nathael on 20/02/17.
 */
public class Main {
	private static final String thisPath = new File("").getAbsolutePath();
	private static final String OUTPUT_SEPARATOR = ";";

	/**
	 * @param args (-c) outputFile [inputFile grade] ([inputFile grade])*
	 *  -c (-continue): add this parameter before outputFile to continue feeding in already existing file
	 * 	outputFile: './the/path/to/outputFile.csv'
	 * 	inputFile: './the/path/to/inputFile.log'
	 * 	grade: '0' (cut failed)
	 * 	    or '1' (bottle cut fewer than half)
	 * 	    or '2' (bottle cut more than half)
	 * 	    or '3' (bottle cut in 2 distinct parts)
	 */
	public static void main(String[] args) {
		boolean _continue = false;

		String outputFile;
		if(args.length > 0) {
			outputFile = args[0];
			if(outputFile.startsWith("-c")) {
				_continue = true;
				if(args.length > 0)
					outputFile = args[0];
				else {
					System.err.println("No output file defined.");
					return;
				}
			}
		} else {
			Scanner sc = new Scanner(System.in);
			System.out.print("Output file (*.csv): "+thisPath+"/");
			outputFile = sc.nextLine();

			System.out.print("['y': continue feeding, 'N': create new output file]: ");
			_continue = (sc.nextLine().charAt(0)+"").equalsIgnoreCase("y");
		}

		File outF = new File(outputFile);
		OutputStreamWriter writer = null;

		try {
			System.out.println("Output file: " + outF.getCanonicalPath());
			System.out.println("");
			if(_continue){
				writer = new OutputStreamWriter(
						new FileOutputStream(outputFile),
						Charset.forName("UTF-8").newEncoder()
				);
			}
			else{
				writer = createFile(outF);
			}

		} catch (IOException e) {
			System.out.print("Output file: " + outF.getAbsolutePath());
			System.err.println("Cannot open outputFile: "+e.getMessage());
			return;
		}

		if(args.length > (_continue?2:1)) {
			int argi = _continue?2:1;

			while(args.length > argi+1) {
				String inputFile = args[argi];
				File inF = new File(inputFile);
				Set<File> subFiles = listSubFiles(inF);

				int grade = Integer.parseInt(args[++argi]);
				if (grade < 0 || grade > 3) {
					System.err.println("Grade not in 0-3 range (" + grade + "), aborting this inputFile");
					continue;
				}

				for(File subFile : subFiles) {
					try {
						System.out.println("Input file: " + subFile.getCanonicalPath());
					} catch (IOException e) {
						System.out.println("Input file*: " + subFile.getAbsolutePath());
					}
					System.out.println("Grade: " + grade);

					feedFile(writer, inF, grade);
					System.out.println();
				}
			}
		} else {
			String input = "";
			do {
				System.out.print("Input file or folder (or 'quit' to finish): "+thisPath+"/");
				Scanner sc = new Scanner(System.in);
				String inputName = sc.nextLine();
				if(inputName.equalsIgnoreCase("quit"))
					break;

				File inF = new File(inputName);
				Set<File> subFiles = listSubFiles(inF);
				System.out.print("Grade: ");
				int grade = Integer.parseInt(sc.nextLine());

				if (grade < 0 || grade > 3) {
					System.err.println("Grade not in 0-3 range (" + grade + "), aborting this inputFile");
					continue;
				}


				for(File subFile : subFiles) {

					try {
						System.out.println("Treating "+subFile.getCanonicalPath());


						feedFile(writer, subFile, grade);
					} catch (NumberFormatException nfe) {
						System.err.println("Grade is not an integer, aborting this inputFile (" + nfe.getMessage() + ")");
					} catch (IOException e) {
						System.err.println("Problem reading file: "+subFile.getAbsolutePath());
						e.printStackTrace();
					}
					System.out.println();
				}
				sc.close();
			} while(input.equalsIgnoreCase("stop"));
		}

		try {
			writer.close();
		} catch (IOException e) {
			System.err.println("Error on closing outputFile: "+e.getMessage());
		}
	}


	private static Set<File> listSubFiles(File in) {
		final Set<File> fileSet = new HashSet<>();

		if(in.isDirectory()) {
			File[] subFiles = in.listFiles();

			if(subFiles != null) {
				for (File f : subFiles) {
					if (f != in)
						fileSet.addAll(listSubFiles(f));
				}
			}
		} else if(in.isFile() && in.canRead()) {
			fileSet.add(in);
		}

		return fileSet;
	}

	private static void feedFile(Writer outF, File inF, int grade) {
		try {

			DataSet dataSet = DataSetBuilder.extract(inF);
			if (dataSet == null){
				System.out.println("J'entre dans le if =(");
				return;
			}


			// "grade;rotX0;rotY0;rotZ0;accX0;accY0;accZ0;...;accZ499"
			outF.write(grade);

			int mid = dataSet.size();
			int lastquarter = mid * 3 / 4;
			mid /= 2;

			int i = 0;
			while(i<dataSet.size()) {
				DataElement element = dataSet.get(i);

				outF.write(OUTPUT_SEPARATOR);
				outF.write(element.get(DataType.RotX));
				outF.write(OUTPUT_SEPARATOR);
				outF.write(element.get(DataType.RotY));
				outF.write(OUTPUT_SEPARATOR);
				outF.write(element.get(DataType.RotZ));
				outF.write(OUTPUT_SEPARATOR);
				outF.write(element.get(DataType.AccX));
				outF.write(OUTPUT_SEPARATOR);
				outF.write(element.get(DataType.AccY));
				outF.write(OUTPUT_SEPARATOR);
				outF.write(element.get(DataType.AccZ));

				if(i>lastquarter) {
					i++;
				} else if(i > mid) {
					i+=2;
				} else {
					i+=3;
				}
			}

			outF.write("\n");
		} catch (IOException | NumberFormatException e) {
			System.err.println(e.getMessage());
		}
	}


	private static OutputStreamWriter createFile(File f) throws IOException {
		if(f.exists())
			f.delete();
		f.createNewFile();

		OutputStreamWriter fw;

		fw = new OutputStreamWriter(
				new FileOutputStream(f),
				Charset.forName("UTF-8").newEncoder()
		);

		// "grade;rotX0;rotY0;rotZ0;accX0;accY0;accZ0;...;accZ499"
		fw.write("grade");


		int total = 500;
		int lastquarter = total * 3 / 4;
		int mid = total / 2;

		int i = 0;
		while(i<total) {
			fw.write(";rotX"+i);
			fw.write(";rotY"+i);
			fw.write(";rotZ"+i);
			fw.write(";accX"+i);
			fw.write(";accY"+i);
			fw.write(";accZ"+i);
			fw.flush();

			if(i>lastquarter) {
				i++;
			} else if(i > mid) {
				i+=2;
			} else {
				i+=3;
			}
		}
		fw.write("\n");

		return fw;
	}
}
