package com.nofuturecorp.www.connector.examples;
import java.io.IOException;

import com.nofuturecorp.www.connector.Database;
import com.nofuturecorp.www.connector.exceptions.DatabaseException;
import com.nofuturecorp.www.connector.exceptions.SQLException;

public class ScriptExample {

	public static void main(String[] args) {
		// URL to PHP file in server
		String url = "http://localhost/www/projects/Java_Php_DBConnector/RequestHandler.php";

		// Get a database instance (singleton)
		Database db = Database.getInstance(url);

		// Set Secure protocol ignored to true because we use HTTP protocol in
		// this example (for security use HTTPS protocol)
		db.setSecureProtocolIgnored(true);
		
		try {
			db.executeScript("./script.sql");
		} catch (SQLException | DatabaseException | IOException e) {
			System.out.println(e);
			// Handle exception
		}
		
	}

}
