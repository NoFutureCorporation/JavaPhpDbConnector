package com.nofuturecorp.www.connector.examples;
import com.nofuturecorp.www.connector.ColumnSet;
import com.nofuturecorp.www.connector.Database;
import com.nofuturecorp.www.connector.exceptions.DatabaseException;
import com.nofuturecorp.www.connector.exceptions.SQLException;

public class DeleteExample {

	public static void main(String[] args) {

		// URL to PHP file in server
		String url = "http://localhost/www/projects/Java_Php_DBConnector/PetitionHandler.php";

		// Get a database instance (singleton)
		Database db = Database.getInstance(url);

		// table name
		String table = "tableTwo";

		// Where clause (mapping values with ":")
		String where = "tt_fieldOne = :tt_fieldOne";
		
		// Where Args
		ColumnSet whereArgs = new ColumnSet();
		whereArgs.put("tt_fieldOne", 3);
		
		// Insert values
		try {
			// get result of insert values (Integer with number of affected rows)
			int result = db.delete(table, where, whereArgs);

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
