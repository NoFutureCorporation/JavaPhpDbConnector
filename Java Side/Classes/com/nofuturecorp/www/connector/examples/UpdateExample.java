package com.nofuturecorp.www.connector.examples;
import com.nofuturecorp.www.connector.ColumnSet;
import com.nofuturecorp.www.connector.Database;
import com.nofuturecorp.www.connector.exceptions.DatabaseException;
import com.nofuturecorp.www.connector.exceptions.SQLException;

public class UpdateExample {

	public static void main(String[] args) {

		// URL to PHP file in server
		String url = "http://localhost/www/projects/Java_Php_DBConnector/PetitionHandler.php";

		// Get a database instance (singleton)
		Database db = Database.getInstance(url);

		// table name
		String table = "tableOne";

		// values to update
		ColumnSet values = new ColumnSet();
		values.put("to_fieldTwo", "Text value (updated)");
		values.put("to_fieldThree", false);
		values.put("to_fieldFour", 3589.65);

		// Where clause (mapping values with ":")
		String where = "to_fieldOne = :to_fieldOne";
		
		// Where Args
		ColumnSet whereArgs = new ColumnSet();
		whereArgs.put("to_fieldOne", 2);
		
		// Insert values
		try {
			// get result of insert values (Integer with number of rows because
			// returnId is false)
			int result = db.update(table, values, where, whereArgs);

			// check result value
			if (result > 0) {
				// values inserted
			} else {
				// values not inserted
			}
		} catch (SQLException e) {
			// handler exception
		} catch (DatabaseException e) {
			// handler exception
		}
	}
}
