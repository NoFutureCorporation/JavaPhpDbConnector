/*
  +---------------------------------------------------------------------------+
  |    Class: TransactOperation	                                              |
  |  Version: 1.0.0                                                           |
  |  Project: Java-Php-DBConnector                                            |
  | ------------------------------------------------------------------------- |
  |     Admin: Juan José Longoria López                                       |
  |    Author: Juan José Longoria López                                       |
  |   License: GNU GENERAL PUBLIC LICENSE 2.0.							      |
  +---------------------------------------------------------------------------+
 */

package com.nofuturecorp.www.connector;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nofuturecorp.www.connector.interfaces.JSONTransform;

/**
 * Class that encapsulates all the data needed to be converted into JSON and send operations to the database in transaction
 * @author Juan José Longoria López
 * @version 1.0
 */
class TransactOperation implements JSONTransform, Serializable{

	// SONSTANTS
	
	private static final long serialVersionUID = -8509712134959099947L;
	
	
	// FIELDS
	
	private ArrayList<Operation> operations;
	
	// GETTERS AND SETTERS
	
	// CONSTRUCTORS
	
	public TransactOperation() {
		operations = new ArrayList<Operation>();
	}


	// PRIVATE METHODS
	
	
	// PUBLIC METHODS
	
	/**
	 * Add operation to transaction
	 * @param operation
	 */
	public void add(Operation operation){
		operations.add(operation);
	}
	
	/**
	 * Remove operation to transaction
	 * @param operation
	 */
	public void remove(Operation operation){
		operations.remove(operation);
	}

	
	// OVERRIDE METHODS
	
	@Override
	public JSONObject toJSON() throws JSONException {
		StringBuilder builder = new StringBuilder();
		try {
			int cont = 0;
			builder.append("{\n");
			for (Operation mySQLOperation : operations) {	
				builder.append("\"" + cont + "\":" + mySQLOperation.toJSON().toString());
				builder.append(",");
				cont++;
			}
			builder.replace(builder.length() -1, builder.length(), "");
			builder.append("}\n");
		} catch (JSONException e) {
			return null;
		}
		return new JSONObject(builder.toString());
	}

	@Override
	public void fromJSON(String json) throws JSONException {

	}

	@Override
	public void fromJSON(JSONObject json) throws JSONException {

	}

	@Override
	public void fromJSONArray(JSONArray json) throws JSONException {
		
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		try {
			int cont = 0;
			builder.append("{\n");
			for (Operation mySQLOperation : operations) {	
				builder.append("\"" + cont + "\":" + mySQLOperation.toJSON().toString());
				builder.append(",");
				cont++;
			}
			builder.replace(builder.length() -1, builder.length(), "");
			builder.append("}\n");
		} catch (JSONException e) {
			return null;
		}
		return builder.toString();
	}
	
}
