package tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by yan on 02/12/16.
 */
public class DataFileReader {
	
	private String path_file;
	
	public DataFileReader() {
		path_file = null;
	}
	
	/**
	* Get all data from a file which is 500*6, be careful about time_ms which is also float type
	* @return ArrayList<{tims_ms, rotX, rotY, rotZ, accX, accY, accZ}>
	**/
	public ArrayList<float[]> getDataFromFile(String path_file) throws IOException {
		
		this.path_file = path_file;
		ArrayList<float[]> list_data = new ArrayList<>();
		
		FileReader input = new FileReader(path_file);
        BufferedReader bufRead = new BufferedReader(input);
        String myLine = null;
        int indexLine = 1;

        myLine = bufRead.readLine();//première ligne de consigne
        while ( (myLine = bufRead.readLine()) != null)
        {
            String[] data_thisLine = myLine.split(";");

            float[] tmp = new float[6];
            /* 0: time_ms */
            tmp[0] = (float) indexLine;
            /* 1-6: rotations of x,y,z and accelerations of x,y,z */
            tmp[1] = Float.parseFloat(data_thisLine[0]);
            tmp[2] = Float.parseFloat(data_thisLine[1]);
            tmp[3] = Float.parseFloat(data_thisLine[2]);
            tmp[4] = Float.parseFloat(data_thisLine[3]);
            tmp[5] = Float.parseFloat(data_thisLine[4]);
            tmp[6] = Float.parseFloat(data_thisLine[5]);

            list_data.add(tmp);
            
            indexLine++;
        }
        
        return list_data;
	}
	
}
