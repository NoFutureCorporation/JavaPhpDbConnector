package com.nofuturecorp.www.connector.examples;
import java.util.ArrayList;

import com.nofuturecorp.www.connector.ColumnSet;
import com.nofuturecorp.www.connector.DataSet;
import com.nofuturecorp.www.connector.Database;
import com.nofuturecorp.www.connector.exceptions.DatabaseException;
import com.nofuturecorp.www.connector.exceptions.SQLException;
import com.nofuturecorp.www.connector.interfaces.Model;


class TableOne implements Model{
	
	// CONSTANTS WITH COLUMN AND TABLE NAMES
	
	public static final String TABLE = "tableOne";
	public static final String FIELD_ONE = "to_fieldOne";
	public static final String FIELD_TWO = "to_fieldTwo";
	public static final String FIELD_THREE = "to_fieldThree";
	public static final String FIELD_FOUR = "to_fieldFour";
	
	
	// FIELDS
	
	private int fieldOne;
	private String fieldTwo;
	private boolean fieldThree;
	private double fieldFour;
	
	
	// GETTERS AND SETTERS...
	
	public void setFieldOne(int fieldOne) {
		this.fieldOne = fieldOne;
	}
	
	public void setFieldTwo(String fieldTwo) {
		this.fieldTwo = fieldTwo;
	}
	
	public void setFieldThree(boolean fieldThree) {
		this.fieldThree = fieldThree;
	}
	
	public void setFieldFour(double fieldFour) {
		this.fieldFour = fieldFour;
	}
	
	// CONSTRUCTORS
	
	public TableOne() {
		fieldOne = -1; // Initialise primary key at -1 because is an auto_increment key
	}
	
	// Constructor for initialise object with ColumnSet from database
	public TableOne(ColumnSet columnSet){
		fromSQL(columnSet);
	}
	
	// METHODS FROM Model INTERFACE
	
	@Override
	public void fromSQL(ColumnSet columnSet) {
		
		// Get values from columnSet
		
		fieldOne = columnSet.getInt(FIELD_ONE);			// Get Integer value from fieldOne Column
		fieldTwo = columnSet.getString(FIELD_TWO);		// Get String value from fieldTne Column
		fieldThree = columnSet.getBoolean(FIELD_THREE);	// Get Boolean value from fieldThree Column
		fieldFour = columnSet.getDouble(FIELD_FOUR);	// Get Double value from fieldFour Column
	}

	@Override
	public ColumnSet toSQL() {
		// Mapping values for database

		ColumnSet values = new ColumnSet();
		
		// Check if primary key is lower than 0 (auto_increment field, if not auto_increment, design other check method)
		if(fieldOne < 0){
			values.put(FIELD_ONE, null);
		}
		else{
			values.put(FIELD_ONE, fieldOne);
		}
		values.put(FIELD_TWO, fieldTwo);
		values.put(FIELD_THREE, fieldThree);
		values.put(FIELD_FOUR, fieldFour);		
		
		return values;
	}	
}


public class MVCExample {
	
	public static void main(String[] args) {
		
		// URL to PHP file in server
		String url = "http://localhost/www/projects/Java_Php_DBConnector/PetitionHandler.php";
		
		// Get a database instance (singleton)
		Database db = Database.getInstance(url);
		
		// Initialise an ArrayList for stored all data in tableOne
		ArrayList<TableOne> list = new ArrayList<TableOne>();
		
		try {
			// get all data of tableOne
			DataSet set = db.query(TableOne.TABLE, null, null, null, null, null, null, null);
			
			// check result
			if(set.rowCount() > 0){
				for (ColumnSet columnSet : set) {
					// Populate list with data
					list.add(new TableOne(columnSet));
				}
			}	
			
			// Create model to insert in database
			TableOne tOne = new TableOne();
			tOne.setFieldTwo("Field Two Value");
			tOne.setFieldThree(false);
			tOne.setFieldFour(569.56);
			
			// Insert data
			int result = db.insert(TableOne.TABLE, tOne.toSQL(), false);
			
			// Check result
			if(result > 0){
				// data inserted
			}
			else{
				// not data inserted
			}	
		} catch (SQLException e) {
			// handling exception
		} catch (DatabaseException e) {
			// handling exception
		}	
	}
}
