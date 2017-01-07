package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by yan on 02/12/16.
 */
public class DataFileReader {
	
	public DataFileReader() {
		
	}
	
	/**
	* Retrieve the value of isGood from a file
	* @param path_file must be a valid path ended by i.e fichier.xxxxY.txt
	* @return int: 0 means bad cut, 1 means good cut
	**/
	public int retrieveIsGoodFromFile(String path_file) {
	    
		File file = null;
		String completeFileName = null;
		String isGood = null;
		
		try {
	          file = new File(path_file);
	          completeFileName = file.getName();
	          //get the last character of fileName before '.txt'
	          isGood = completeFileName.substring(completeFileName.length()-5, completeFileName.length()-4);
	    } 
		catch(Exception e){
	          e.printStackTrace();
	    }
		
		
		if (isGood.equals("Y"))
			return 1;
		else if (isGood.equals("N"))
			return 0;
		else
			return -1;
		
	}
	
	/**
	* Retrieve the filename
	* @param path_file must be a valid path ended by i.e. fichier1111Y.txt
	* @return String: fileName i.e. fichier1111
	**/
	public String retrieveFileName(String path_file) {
		
		File file = null;
		String completeFileName = null;
		String fileName = null;
		
		try {
	          file = new File(path_file);
	          completeFileName = file.getName();
	          //get fileName without '.txt' and 'Y'/'N'
	          fileName = completeFileName.substring(0, completeFileName.length()-5);
	    } 
		catch(Exception e){
	          e.printStackTrace();
	    }
		
		return fileName;
		
	}
	
	/**
	* Retrieve all data from a file which is 500*6, be careful about time_ms which is also float type
	* @return ArrayList<{tims_ms, rotX, rotY, rotZ, accX, accY, accZ}>
	**/
	public double[][] retrieveDataFromFile(String path_file) throws IOException {
		
		double[][] list_data = new double[500][6];
		
		FileReader input = new FileReader(path_file);
        BufferedReader bufRead = new BufferedReader(input);
        String myLine = null;
        int indexLine = 0;

        myLine = bufRead.readLine();//première ligne de consigne
        while ( (myLine = bufRead.readLine()) != null)
        {
            String[] data_thisLine = myLine.split(";");

            /* 0-5: rotations of x,y,z and accelerations of x,y,z */
            list_data[indexLine][0] = Double.parseDouble(data_thisLine[0]);
            list_data[indexLine][1] = Double.parseDouble(data_thisLine[1]);
            list_data[indexLine][2] = Double.parseDouble(data_thisLine[2]);
            list_data[indexLine][3] = Double.parseDouble(data_thisLine[3]);
            list_data[indexLine][4] = Double.parseDouble(data_thisLine[4]);
            list_data[indexLine][5] = Double.parseDouble(data_thisLine[5]);
            
            indexLine++;
        }
        
        return list_data;
	}
	
}
