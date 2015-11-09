package com.nofuturecorp.www.connector.examples;


import com.nofuturecorp.www.connector.ColumnSet;
import com.nofuturecorp.www.connector.Database;
import com.nofuturecorp.www.connector.exceptions.DatabaseException;
import com.nofuturecorp.www.connector.exceptions.SQLException;
import com.nofuturecorp.www.connector.exceptions.SQLException.ExceptionType;

public class HandleExceptionExample {

	public static void main(String[] args) {

		// This lines are not important in this example because the objective is the handled of exceptions, not manage the database
		
		String url = "http://localhost/www/projects/Java_Php_DBConnector/PetitionHandler.php";
		Database db = Database.getInstance(url);
		String table = "tableOne";
		ColumnSet values = new ColumnSet();
		boolean returnId = true;		
		
		try {
			db.insert(table, values, returnId);
			
			// This are the important lines in this example
			
		} catch (SQLException e) {
			// Exception throws when have an SQL error or warnings
			// In a Exception message he wave a Database message
			
			// Also have a Enumeration with type of exception for handled it more easily			
			ExceptionType exType = e.getType();
			switch (exType) {
			case dataTruncated:
				// When data is truncated when try to put it in database
				break;
			case databaseNotConnect:
				// When PHP file can't connect to database
				break;
			case duplicateEntry:
				// When have a conflict with primary keys (PK duplicated)
				break;
			case foreignKeyConstraintFails:
				// When have a conflict with foreign keys
				break;
			case invalidType:
				// When try to put a data type in a wrong field (Example, insert varchar in a integer column)
				break;
			case notTransactionStarted:
				// When call executeTransaction() and not call before startTransaction()
				break;
			case nullColumn:
				// When try to put a null value in a not null column
				break;
			case nullTable:
				// When table is null
				break;
			case nullValues:
				// When mapping values are null
				break;
			case syntaxError:
				// When have an error syntax in a SQL statement
				break;
			case unknownColumn:
				// When there is no column in the table affected
				break;
			case unknownError:
				// When have an unknown error
				break;
			case nullScript:
				// When sql script is null
				break;
			case scriptError:
				// When sql script execute fail
				break;			
			}
		} catch (DatabaseException e) {
			// This exception throws if have an error when try to get data from PHP file
			// In the exception message we have more information of the Exception cause
		}
	}
}
