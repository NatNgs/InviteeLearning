package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by yan on 02/12/16.
 */
public class MysqlConnecter {
	
	private Connection connection;
	
	public MysqlConnecter() {
		connection = null;
	}
	
	/**
	* Build connexion to Mysql server
	* @param usrMysql: "jdbc:mysql://serverName:portNumber/databaseName"
	**/
	public void connectToMysql(String urlMysql, String user, String password) throws ClassNotFoundException {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");  
			connection = DriverManager.getConnection(urlMysql, user, password);
		    System.out.println("Connected to database");

		} catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	/**
	* Build connexion to Mysql server
	* @param isGood: 0 means bad cut, 1 means good cut
	**/
	public void sendDataToMysql(String fileName, int isGood, int time_ms, int type, double minimum, double proba_yes, double proba_no) {
		Statement statement;
		String sql_probabilities;
		String sql_datafiles;
		
		/* SQL insert command for table 'probabilities' */
		sql_probabilities = "INSERT INTO probabilities (time_ms, type, minimum, proba_yes, proba_no) "
							+ "VALUES(" + time_ms + ", " + type + ", " +  minimum + ", " + proba_yes + ", " + proba_no + ") "
							+ "ON DUPLICATE KEY UPDATE proba_yes = " + proba_yes + ", proba_no = " + proba_no;
		
		/* SQL insert command for table 'data_files' */
		sql_datafiles = "INSERT INTO data_files (time_ms, type, minimum, file_name, isGood) "
				+ "VALUES ('" + time_ms + "','" + type + "','" + minimum + "','" + fileName + "','" + isGood + "')";
		
		try {
			statement = connection.createStatement();
			
			/* Execute SQL command for table 'probabilities' */
			statement.executeUpdate(sql_probabilities); 
			System.out.println("Data updated/inserted into table 'probabilities'");
			
			/* Execute SQL command for table 'data_files' */
			statement.executeUpdate(sql_datafiles); 
			System.out.println("Data updated/inserted into table 'data_files'");
			
		} catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}   
	}
	
	/**
	* Build connexion to Mysql server
	* @param getYes: true means getYes, false means getNo, (time_ms, type, minimum) is the composite primary key for table 'probabilities'
	* @return the probability of good cut/bad cut (0.00-1.00)
	**/
	public double getProbabilityFromMysql(boolean getYes, int time_ms, int type, double minimum) {
		Statement statement;
		String sql;
		Double result_yes = 0.0;
		Double result_no = 0.0;
		
		/* SQL select command for table 'probabilities' */
		sql = "SELECT * FROM probabilities WHERE (time_ms, type, minimum) = (" + time_ms + ", " + type + ", " + minimum + ")";
		
		try {
			statement = connection.createStatement();
			
			/* Execute SQL command for table 'probabilities' */
			ResultSet resultSet = statement.executeQuery(sql);
			System.out.println("Data retrieved from table 'probabilities'");
			
			while (resultSet.next()) {
				result_yes = resultSet.getDouble("proba_yes");
				result_no = resultSet.getDouble("proba_no");
			}
			
			if(getYes) {
				return result_yes;
			}
			else {
				return result_no;
			}
			
			
		} catch (SQLException ex) {
		    // handle any errors
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
		return 0.0;   
	}
	
	
	
}
