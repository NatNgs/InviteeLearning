import java.io.IOException;
import tools.MysqlConnecter;

/**
 * Created by nathael on 02/12/16.
 */
public class Main {
	
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		 
		/* Test to connect to DB and access to data */
		MysqlConnecter connecter = new MysqlConnecter();
		
		connecter.connectToMysql("jdbc:mysql://localhost:3306/pfe", "root", "56375309");
		
		connecter.sendDataToMysql("file1", 0, 500, 1, 0, 0.3, 0.7);
		
		double proba_yes = connecter.getProbabilityFromMysql(true, 500, 1, 0);
		System.out.println(proba_yes);
		  
	}
}
