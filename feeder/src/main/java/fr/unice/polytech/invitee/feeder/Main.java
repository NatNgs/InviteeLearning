package fr.unice.polytech.invitee.feeder;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by nathael on 20/02/17.
 */
public class Main {
	private static final String thisPath = new File("").getAbsolutePath();

	private void mainWithPrompt() {
		Scanner sc = new Scanner(System.in);

		System.out.print("Output file (*.csv): "+thisPath+"/");
		String outputFile = sc.nextLine();

		System.out.print("Continue feeding if possible (if not will create new output file) ? ['y'/'N']: ");
		boolean _continue = (sc.nextLine().charAt(0)+"").equalsIgnoreCase("y");

		System.out.print("Set the grades in output file ? ['Y'/'n']: ");
		boolean _unknownGrades = (sc.nextLine().charAt(0)+"").equalsIgnoreCase("y");

		File outF = new File(outputFile);
		Writer writer;

		try {
			writer = createFile(_continue, _unknownGrades, outF);
		} catch (IOException e) {
			System.err.println("Cannot open outputFile "+outF.getAbsolutePath()+": "+e.toString());
			return;
		}
		System.out.println();

		do {
			System.out.print("Input file or folder (or type return without completing path to finish)\n"
					+ ": "+thisPath+"/");
			String inputName = sc.nextLine();
			if(inputName.isEmpty())
				break;

			File inF = new File(inputName);
			Set<File> subFiles = listSubFiles(inF);

			int grade = -1;

			if(!_unknownGrades) {
				System.out.print("Grade: ");
				grade = Integer.parseInt(sc.nextLine());

				if (grade < 0 || grade > 3) {
					System.err.println("Grade not in 0-3 range (" + grade + "), aborting this inputFile");
					continue;
				}
			}

			if(subFiles.isEmpty()) {
				System.err.println("No file found with this name, or this is an Empty folder.");
			} else {
				for (File subFile : subFiles) {
					try {
						System.out.println("Treating " + subFile.getCanonicalPath());
						feedFile(writer, subFile, grade);
					} catch (NumberFormatException nfe) {
						System.err.println("Grade is not an integer, aborting this inputFile (" + nfe.getMessage() + ")");
					} catch (IOException e) {
						System.err.println("Problem reading file: " + subFile.getAbsolutePath());
						e.printStackTrace();
					}
				}
			}
			System.out.println();
		} while(true);

		try {
			writer.close();
		} catch (IOException e) {
			System.err.println("Error on closing outputFile: "+e.getMessage());
		}
		sc.close();
	}

	private void mainWithArgs(List<String> args, boolean _continue, boolean _unknownGrades, boolean _help) {
		if(_help || args.size() < 1) {
			System.out.println(
					"Usage:   $ feeder (-c|-h|-u)* path/outputFile.csv ([inputFile|inputFolder] grade)*\n"
					+ "or just: $ feeder\n\n"
					+ "-c (-continue):\n\t\tContinue feeding an existing output file if possible.\n"
					+ "-h (-help):\n\t\tPrint this help\n"
					+ "-u (-unknownGrades):\n\t\tDo not specify grades in output file.");
			return;
		}

		String outputFile = args.remove(0);

		File outF = new File(outputFile);
		Writer writer;

		try {
			writer = createFile(_continue, _unknownGrades, outF);
		} catch (IOException e) {
			System.err.println("Cannot open outputFile "+outF.getAbsolutePath()+": "+e.toString());
			return;
		}
		System.out.println();

		while(args.size() >= 2 || (_unknownGrades && args.size() >= 1)) {
			String inputFile = args.remove(0);
			int grade = -1;

			if(!_unknownGrades)
				grade = Integer.parseInt(args.remove(0));

			for(File subFile : listSubFiles(new File(inputFile))) {
				try {
					System.out.println("Input file: " + subFile.getCanonicalPath());
					if(_unknownGrades) {
						feedFile(writer, subFile, grade);
					} else if (grade < 0 || grade > 3) {
						System.err.println("Grade not in 0-3 range (" + grade + "), aborting this file");
					} else {
						System.out.println("Grade: " + grade);
						feedFile(writer, subFile, grade);
					}
				} catch (IOException e) {
					System.err.println("Input file error: " + subFile.getAbsolutePath()+" ("+e.toString()+")");
				}
				System.out.println();
			}
		}

		try {
			writer.close();
		} catch (IOException e) {
			System.err.println("Error on closing outputFile: "+e.getMessage());
		}
	}

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
		if(args.length == 0)
			new Main().mainWithPrompt();
		else {
			List<String> argList = new ArrayList<>();
			boolean _c = false;
			boolean _u = false;
			for(String s : args) {
				if(s.startsWith("-h")) {
					new Main().mainWithArgs(argList, false, false, true);
					return;
				} else if(s.startsWith("-c")) {
					_c = true;
				} else if(s.startsWith("-u")) {
					_u = true;
				} else {
					argList.add(s);
				}
			}

			new Main().mainWithArgs(argList, _c, _u, false);
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
				System.err.println("DataSet NULL !!");
				return;
			}

			// "grade;rotX0;rotY0;rotZ0;accX0;accY0;accZ0;...;accZ499"
			if(grade >= 0 && grade <= 3)
				outF.write(grade+";");

			int mid = dataSet.size();
			int lastQuarter = mid * 3 / 4;
			mid /= 2;

			int i = 0;
			while(i<dataSet.size()) {
				DataElement element = dataSet.get(i);

				if(i!=0) // ignore first
					outF.write(";");

				outF.write(""+element.get(DataType.RotX));
				outF.write(";" + element.get(DataType.RotY));
				outF.write(";" + element.get(DataType.RotZ));
				outF.write(";" + element.get(DataType.AccX));
				outF.write(";" + element.get(DataType.AccY));
				outF.write(";" + element.get(DataType.AccZ));
				outF.flush();

				if(i>lastQuarter) {
					i++;
				} else if(i > mid) {
					i+=2;
				} else {
					i+=3;
				}
			}

			outF.write("\n");
			outF.flush();
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private static Writer createFile(boolean _continue, boolean _unknownGrades, File f) throws IOException {
		if(!_continue || !f.exists()) {
			if (f.exists() && f.isFile())
				//noinspection ResultOfMethodCallIgnored
				f.delete();

			if(f.exists())
				throw new IOException("Cannot delete: This is not a regular file");

			//noinspection ResultOfMethodCallIgnored
			f.createNewFile();
			System.out.println("Created new file "+f.getCanonicalPath());
		}

		Writer outF = new OutputStreamWriter(
				new FileOutputStream(f),
				Charset.forName("UTF-8").newEncoder()
		);

		// "grade;rotX0;rotY0;rotZ0;accX0;accY0;accZ0;...;accZ499"
		if(!_unknownGrades)
			outF.write("grade;");

		int total = 500;
		int lastQuarter = total * 3 / 4;
		int mid = total / 2;

		int i = 0;
		while(i<total) {
			outF.write((i!=0?";rotX":"rotX")+i); // ignore first ';'
			outF.write(";rotY"+i);
			outF.write(";rotZ"+i);
			outF.write(";accX"+i);
			outF.write(";accY"+i);
			outF.write(";accZ"+i);
			outF.flush();

			if(i>lastQuarter) {
				i++;
			} else if(i > mid) {
				i+=2;
			} else {
				i+=3;
			}
		}
		outF.write("\n");
		outF.flush();

		return outF;
	}
}
