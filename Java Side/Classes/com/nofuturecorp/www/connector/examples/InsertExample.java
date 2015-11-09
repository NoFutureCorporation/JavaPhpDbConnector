package com.nofuturecorp.www.connector.examples;
import com.nofuturecorp.www.connector.ColumnSet;
import com.nofuturecorp.www.connector.Database;
import com.nofuturecorp.www.connector.exceptions.DatabaseException;
import com.nofuturecorp.www.connector.exceptions.SQLException;

public class InsertExample {

	public static void main(String[] args) {

		// URL to PHP file in server
		String url = "http://localhost/www/projects/Java_Php_DBConnector/PetitionHandler.php";

		// Get a database instance (singleton)
		Database db = Database.getInstance(url);

		// table name
		String table = "tableOne";

		// values to insert
		ColumnSet values = new ColumnSet();
		values.put("to_fieldOne", null); // Null because is an auto_increment id (this is not necessary with auto_increment keys)
		values.put("to_fieldTwo", "Text value");
		values.put("to_fieldThree", true);
		values.put("to_fieldFour", 3589.64);

		// returnId => If true return last inserted id (only work with numeric id, otherwise return 0). If false, return the number affected rows
		boolean returnId = true;
		
		// Insert values
		try {
			// get result of insert values (Integer with number of rows because returnId is false)
			int result = db.insert(table, values, returnId);
			
			// check result value
			if (result > 0){
				// values inserted
			}
			else{
				// values not inserted
			}
		} catch (SQLException e) {
			// handler exception
		} catch (DatabaseException e) {
			// handler exception
		}		
	}
}
