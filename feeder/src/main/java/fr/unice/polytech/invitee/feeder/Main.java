package fr.unice.polytech.invitee.feeder;

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
				if(args.length > 1)
					outputFile = args[1];
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
		Writer writer;

		try {
			writer = createFile(_continue, outF);
		} catch (IOException e) {
			System.err.println("Cannot open outputFile "+outF.getAbsolutePath()+": "+e.toString());
			return;
		}
		System.out.println();

		if(args.length > (_continue?2:1)) {
			int argi = _continue?2:1;

			while(args.length > argi+1) {
				String inputFile = args[argi];
				File inF = new File(inputFile);

				int grade = Integer.parseInt(args[++argi]);

				Set<File> subFiles = listSubFiles(inF);
				for(File subFile : subFiles) {
					try {
						System.out.println("Input file: " + subFile.getCanonicalPath());
					} catch (IOException e) {
						System.out.println("Input file*: " + subFile.getAbsolutePath());
					}
					System.out.println("Grade: " + grade);
					if (grade < 0 || grade > 3) {
						System.err.println("Grade not in 0-3 range (" + grade + "), aborting this file");
						continue;
					}

					feedFile(writer, subFile, grade);
					System.out.println();
				}
				argi++;
			}
		} else {
			String input = "";
			Scanner sc = new Scanner(System.in);
			do {
				System.out.print("Input file or folder (or 'quit' to finish): "+thisPath+"/");
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
			} while(!input.equalsIgnoreCase("stop"));
			sc.close();
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
				System.err.println("DataSet NULL !!");
				return;
			}

			// "grade;rotX0;rotY0;rotZ0;accX0;accY0;accZ0;...;accZ499"
			outF.write("class"+grade);

			int mid = dataSet.size();
			int lastquarter = mid * 3 / 4;
			mid /= 2;

			int i = 0;
			while(i<dataSet.size()) {
				DataElement element = dataSet.get(i);

				outF.write(";" + element.get(DataType.RotX));
				outF.write(";" + element.get(DataType.RotY));
				outF.write(";" + element.get(DataType.RotZ));
				outF.write(";" + element.get(DataType.AccX));
				outF.write(";" + element.get(DataType.AccY));
				outF.write(";" + element.get(DataType.AccZ));
				outF.flush();

				if(i>lastquarter) {
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


	private static Writer createFile(boolean _continue, File f) throws IOException {
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
		outF.write("grade");

		int total = 500;
		int lastquarter = total * 3 / 4;
		int mid = total / 2;

		int i = 0;
		while(i<total) {
			outF.write(";rotX"+i);
			outF.write(";rotY"+i);
			outF.write(";rotZ"+i);
			outF.write(";accX"+i);
			outF.write(";accY"+i);
			outF.write(";accZ"+i);
			outF.flush();

			if(i>lastquarter) {
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
