import java.io.IOException;
import java.util.ArrayList;

import tools.DataFileReader;
import tools.MysqlConnecter;

public class TestFileWithMysql {

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		
		final String path_file = "./src/res/fichier1035Y.txt";
		
		/* Test to retrieve data from a file */
		DataFileReader dfReader = new DataFileReader();
		String fileName = dfReader.retrieveFileName(path_file);
		int isGood = dfReader.retrieveIsGoodFromFile(path_file);
		double[][] dataList = dfReader.retrieveDataFromFile(path_file);
		
		System.out.println("FileName: "+fileName);
		System.out.println("IsGood: "+isGood);
		
		
		
		/* Test to connect to DB and access to data table */
		MysqlConnecter connecter = new MysqlConnecter();
		
		connecter.connectToMysql("jdbc:mysql://localhost:3306/pfe", "root", "56375309");
		
/*		connecter.sendDataToMysql("file1", 0, 500, 1, 0, 0.3, 0.7);
		
		double proba_yes = connecter.getProbabilityFromMysql(true, 500, 1, 0);
		System.out.println(proba_yes);*/
		  
	}
	
}
