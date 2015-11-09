package com.nofuturecorp.www.connector.examples;
import com.nofuturecorp.www.connector.ColumnSet;
import com.nofuturecorp.www.connector.DataSet;
import com.nofuturecorp.www.connector.Database;
import com.nofuturecorp.www.connector.exceptions.DatabaseException;
import com.nofuturecorp.www.connector.exceptions.SQLException;


public class SelectExample {
	
	public static void main(String[] args) {
		
		// URL to PHP file in server
		String url = "http://localhost/www/projects/Java_Php_DBConnector/RequestHandler.php";
		
		// Get a database instance (singleton)
		Database db = Database.getInstance(url);
		
		// Set Secure protocol ignored to true because we use HTTP protocol in 
		// this example (for security use HTTPS protocol)
		db.setSecureProtocolIgnored(true);
		
		// Table Names
		String tableOne = "tableOne";
		String tableTwo = "tableTwo";
		
		// join tables
		String join = tableOne + " join " + tableTwo + " on to_fieldOne = tt_fieldThree";
		
		// projection 2 fields from tableOne and 2 fields from tableTwo
		String[] projection = { "to_fieldOne", "tt_fieldThree", "to_fieldTwo", "tt_fieldFour" };
				
		// where clause (mapping values with ":")
		String where = "to_fieldOne = :to_fieldOne";
		
		// where Args for parameterized query
		ColumnSet whereArgs = new ColumnSet();
		whereArgs.put("to_fieldOne", 2);
		
		// Query
		try {
			// Get data returned in a DataSet
			DataSet set = db.query(join, projection, where, whereArgs, null, null, null, null);
			
			for (ColumnSet row : set) {
				System.out.println("FieldOne (tableOne) => " + row.getInt("to_fieldOne"));
				System.out.println("FieldThree (tableTwo) => " + row.getString("tt_fieldThree"));
				System.out.println("FieldTwo (tableOne) => " + row.getString("to_fieldTwo"));
				System.out.println("FieldFour (tableTwo) => " + row.getString("tt_fieldFour"));
			}
			
		} catch (SQLException e) {
			// handler exception
		} catch (DatabaseException e) {
			// handler exception
		}	
	}	
}
