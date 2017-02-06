import java.io.*;
import java.util.*;

/**
 * Created by nathael on 06/02/17.
 */
public class Newfeeder {
    private static final int NBVALUES = 500;

    public static void main(String[] args) {
        final File thisPath = new File("");

        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Output file (*.csv): "+thisPath.getCanonicalPath()+"/");
            String outputFile = sc.nextLine();

            FileWriter out = loadOutputFile(outputFile);

            while(true) {
                System.out.print("Type a folder or filename (or 'stop'): "+thisPath.getCanonicalPath()+"/");
                String input = sc.nextLine();
                if(input.equalsIgnoreCase("stop")) {
                    break;
                }

                File in = new File(input);
                if(!in.exists()) {
                    System.err.println("File not found: "+in.getCanonicalPath());
                    Thread.sleep(1000);
                    continue;
                }
                if(in.isDirectory()) {
                    for(File ff : listSubFiles(in)) {
                        feedFile(ff, out, sc);
                    }
                } else
                    feedFile(in, out, sc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Set<File> listSubFiles(File in) throws IOException {
        final Set<File> fileSet = new HashSet<>();

        File[] subFiles = in.listFiles();
        assert subFiles != null;
        for(File f : subFiles) {
            if(f.isDirectory() && f != in)
                fileSet.addAll(listSubFiles(f));
            else if(f.isFile())
                fileSet.add(f);
        }

        return fileSet;
    }

    private static void feedFile(File in, FileWriter out, Scanner sysin) throws IOException {
        do {
            try {
                System.out.println("-- File " + in.getCanonicalPath() + " --");
                Maplist mapList = new Maplist(in);

                System.out.print("Note the quality of the cut ([0-3] or 'pass'): ");
                String str = sysin.nextLine();
                if(str.equalsIgnoreCase("pass"))
                    return;

                int grade = Integer.parseInt(str);

                int msStart = 0;
                int msStop = 0;
                if(grade > 0) {
                    System.out.print("Note the time where cut begin (ms):  ");
                    msStart = Integer.parseInt(sysin.nextLine());

                    System.out.print("Note the time where cut finish (ms): ");
                    msStop = Integer.parseInt(sysin.nextLine());
                }


                Maplist sublist = mapList.subList(0,NBVALUES-1);
                for (int i = 0; i < mapList.size() - NBVALUES; i++) {
                    sublist.add(mapList.get(i));
                    feedLine((sublist.get(0).get(Maplist.DataType.Timer) <= msStart
                                    && sublist.get(sublist.size()-1).get(Maplist.DataType.Timer) >= msStop) ? grade : 0,
                            sublist,
                            out);
                    sublist.remove(0);
                }
                return;
            } catch (NumberFormatException e) {
                System.err.println(e.getMessage());
            }
        } while(true);
    }

    private static void feedLine(int grade, Maplist sublist, FileWriter out) throws IOException {
        // "grade;rotX0;rotY0;rotZ0;accX0;accY0;accZ0;...;accZ499"
        StringBuilder builder = new StringBuilder();
        builder.append(grade);
        for(Map<Maplist.DataType, Integer> line : sublist) {
            builder .append(";")
                    .append(line.get(Maplist.DataType.RotX))
                    .append(";")
                    .append(line.get(Maplist.DataType.RotY))
                    .append(";")
                    .append(line.get(Maplist.DataType.RotZ))
                    .append(";")
                    .append(line.get(Maplist.DataType.AccX))
                    .append(";")
                    .append(line.get(Maplist.DataType.AccY))
                    .append(";")
                    .append(line.get(Maplist.DataType.AccZ));
        }

        System.out.println("Added 1 vector for quality "+grade);
        out.write(builder.toString());
    }

    private static FileWriter loadOutputFile(String outputFile) throws Exception {
        File f = new File(outputFile);

        FileWriter fw;
        if(!f.exists()) {
            System.out.println("File not found: "+f.getCanonicalPath());
            if(!f.createNewFile())
                throw new Exception("Impossible to create new file "+f.getCanonicalPath());
            System.out.println("File created. Adding header...");

            fw = addHeader(f);
        } else {
            fw = new FileWriter(f);
        }

        return fw;
    }

    private static FileWriter addHeader(File f) throws IOException {
        FileWriter fw = new FileWriter(f);

        // "grade;rotX0;rotY0;rotZ0;accX0;accY0;accZ0;...;accZ499"
        fw.write("grade");
        for (int i = 0; i < NBVALUES; i++) {
            fw.write(";rotX"+i);
            fw.write(";rotY"+i);
            fw.write(";rotZ"+i);
            fw.write(";accX"+i);
            fw.write(";accY"+i);
            fw.write(";accZ"+i);
        }
        fw.write("\n");

        return fw;
    }
}
