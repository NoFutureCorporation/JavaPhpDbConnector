package com.nofuturecorp.www.connector.examples;
import com.nofuturecorp.www.connector.ColumnSet;
import com.nofuturecorp.www.connector.Database;
import com.nofuturecorp.www.connector.exceptions.DatabaseException;
import com.nofuturecorp.www.connector.exceptions.SQLException;


public class TransactionExample {

	public static void main(String[] args) {
		
		// URL to PHP file in server
		String url = "http://localhost/www/projects/Java_Php_DBConnector/PetitionHandler.php";
		
		// Get a database instance (singleton)
		Database db = Database.getInstance(url);
		
		// Table Names
		String tableOne = "tableOne";
		String tableTwo = "tableTwo";
		
		// Values for table One
		ColumnSet valuesOne = new ColumnSet();
		valuesOne.put("to_fieldOne", null);	// Null because primary key is auto increment (if key is auto increment, this is not necessary)
		valuesOne.put("to_fieldTwo", "Text Value");
		valuesOne.put("to_fieldThree", true);
		valuesOne.put("to_fieldFour", 56.26);
		
		// Values for table Two
		ColumnSet valuesTwo = new ColumnSet();
		valuesTwo.put("tt_fieldOne", null);	// Null because primary key is auto increment (if key is auto increment, this is not necessary)
		valuesTwo.put("tt_fieldTwo", true);
		valuesTwo.put("tt_fieldThree", 3);
		valuesTwo.put("tt_fieldFour", "Text value");
		
		// Return last id (In transaction is the same set returnId to true or false because transaction only return boolean value)
		boolean returnId = false;
		
		// Start a new transaction
		db.startTransaction();
		
		try {
			// Insert data in table one
			db.insert(tableOne, valuesOne, returnId);
			
			// Insert data in table two
			db.insert(tableTwo, valuesTwo, returnId);
			
			// Get result of execute transaction (true if transaction is OK, false in otherwise and make auto rollback)
			boolean resultOk = db.executeTransaction();
			
			if(resultOk){
				// Transaction executed properly
			}
			else{
				// Transaction not executed properly
			}			
		} catch (SQLException e) {
			// Hander exception
		} catch (DatabaseException e) {
			// Handler exception
		}	
	}
}
