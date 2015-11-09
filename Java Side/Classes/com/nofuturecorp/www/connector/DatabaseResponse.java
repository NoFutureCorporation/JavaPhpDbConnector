/*
  +---------------------------------------------------------------------------+
  |    Class: DatabaseResponse                                                |
  |  Version: 1.0.0                                                           |
  |  Project: Java-Php-DBConnector                                            |
  | ------------------------------------------------------------------------- |
  |     Admin: Juan José Longoria López                                       |
  |    Author: Juan José Longoria López                                       |
  |   License: GNU GENERAL PUBLIC LICENSE 2.0.							      |
  +---------------------------------------------------------------------------+
 */

package com.nofuturecorp.www.connector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nofuturecorp.www.connector.exceptions.DatabaseException;
import com.nofuturecorp.www.connector.exceptions.SQLException;

/**
 * Class for get and handling database responses
 * @author Juan José Longoria López
 * @version 1.0
 */
public class DatabaseResponse {

	private int code;
	private String message;
	private JSONArray data;
	private int dataCount;
	private boolean dataBoolean;
	private Integer exception;

	/**
	 * Get Database response code
	 * @return Database response code
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * Set Database response code
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}
	
	/**
	 * Get Database response message
	 * @return Database response message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Set Database response message
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Get Database response data in JSONArray format for operations that returns values
	 * @return Database response data in JSONArray format
	 */
	public JSONArray getData() {
		return data;
	}
	
	/**
	 * Set Database response data in JSONArray format
	 * @param data
	 */
	public void setData(JSONArray data) {
		this.data = data;
	}
	
	/**
	 * Get Database response data in Integer format for operations that returns integer value
	 * @return Database response data in Integer format
	 */
	public int getDataCount() {
		return dataCount;
	}
	
	/**
	 * Set Database response data in Integer format
	 * @param dataCount
	 */
	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}
	
	/**
	 * Get Database response data in Boolean format for operations that returns boolean value
	 * @return Database response data in Boolean format
	 */
	public boolean getDataBoolean() {
		return dataBoolean;
	}
	
	/**
	 * Set Database response data in Boolean format
	 * @param dataBoolean
	 */
	public void setDataBoolean(boolean dataBoolean) {
		this.dataBoolean = dataBoolean;
	}
	
	/**
	 * Get Exception Code for throw SQL Exception
	 * @return Integer with SQL Exception Code
	 */
	public Integer getException() {
		return exception;
	}
	
	/**
	 * Set Exception Code
	 * @param exception
	 */
	public void setException(Integer exception) {
		this.exception = exception;
	}
	
	
	public DatabaseResponse() {
		code = -1;
		message = "No message";
		data = null;
		dataCount = -1;
		dataBoolean = false;
		exception = null;
	}

	/**
	 * Cosntructor for instance the DatabaseResponse object with a JSONObject
	 * @param data
	 * @throws JSONException
	 * @throws SQLException
	 */
	public DatabaseResponse(JSONObject data) throws DatabaseException, SQLException {
		
		try{
		try{
			this.code = data.getInt("code");
		}
		catch(Exception e){
			code = -1;
		}
		this.message = data.getString("message");
		try{
			this.data  = data.getJSONArray("data");
		}
		catch(JSONException e){
			try{
				this.dataCount = data.getInt("data");
			}
			catch(JSONException ex){
				try {
					this.dataBoolean = data.getBoolean("data");
				} catch (Exception exc) {
					this.data = null;
					this.dataBoolean= false;
					this.dataCount = 0;
				}
			}
		}
		
		try {
			exception = data.getInt("exception");
			throw new SQLException(this.message, exception);
		} catch (JSONException e) {
			exception = null;
		}
		}
		catch(JSONException e){
			throw new DatabaseException("An error ocurred when try to get data from database -> " + e.getMessage());
		}
		
	}

	@Override
	public String toString() {
		return "DatabaseResponse\n[code=" + code + "\nmessage=" + message + "\ndata=" + data + "\ndataCount=" + dataCount + "\ndataBoolean=" + dataBoolean + "\nexception=" + exception + "]";
	}


}
