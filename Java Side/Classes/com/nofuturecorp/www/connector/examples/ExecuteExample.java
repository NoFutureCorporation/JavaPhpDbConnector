package com.nofuturecorp.www.connector.examples;
import com.nofuturecorp.www.connector.Database;
import com.nofuturecorp.www.connector.DatabaseResponse;
import com.nofuturecorp.www.connector.exceptions.DatabaseException;
import com.nofuturecorp.www.connector.exceptions.SQLException;

public class ExecuteExample {

	public static void main(String[] args) {

		// URL to PHP file in server
		String url = "http://localhost/www/projects/Java_Php_DBConnector/RequestHandler.php";

		// Get a database instance (singleton)
		Database db = Database.getInstance(url);

		// Set Secure protocol ignored to true because we use HTTP protocol in
		// this example (for security use HTTPS protocol)
		db.setSecureProtocolIgnored(true);
		
		// Make statement
		String statement = "create table if not exists tableThree (id int,ttr_fieldTwo boolean not null,primary key (id));";
		statement += "insert into tableThree (id, ttr_fieldTwo) values (1, true),(2, false);";
		try {
			
			// Execute statement
			DatabaseResponse response = db.execute(statement, null);
			if(response.getCode() == 0){
				// If code == 0, statement run properly
			}
			else{
				// an error occurred
			}
		} catch (SQLException | DatabaseException e) {
			// handler exceptions
		}

	}

}
