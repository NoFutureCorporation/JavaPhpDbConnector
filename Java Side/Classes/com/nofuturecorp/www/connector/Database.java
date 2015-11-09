/*
  +---------------------------------------------------------------------------+
  |    Class: Database                                                        |
  |  Version: 1.0.0                                                           |
  |  Project: Java-Php-DBConnector                                            |
  | ------------------------------------------------------------------------- |
  |     Admin: Juan José Longoria López                                       |
  |    Author: Juan José Longoria López                                       |
  |   License: GNU GENERAL PUBLIC LICENSE 2.0.							      |
  +---------------------------------------------------------------------------+
 */

package com.nofuturecorp.www.connector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.nofuturecorp.www.connector.exceptions.DatabaseException;
import com.nofuturecorp.www.connector.exceptions.SQLException;
import com.nofuturecorp.www.connector.exceptions.SQLException.ExceptionType;
import com.nofuturecorp.www.connector.exceptions.SecureProtocolException;

/**
 * Class for connect to MySQL Database
 * @author Juan José Longoria López
 * @version 1.0
 */
public class Database {

	
	// FIELDS

	private String url;
	private static Database singleton;
	private int code;
	private String message;
	private boolean isTransaction;
	private TransactOperation transactOperation;
	private boolean ignoreSecureProtocol;
	
	
	// GETTERS AND SETTERS

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
	 * Set Database response Message
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Set ignore secure protocol (HTTPS) to not throw SecureProtocolException
	 * @param ignoreSecureProtocol
	 */
	public void setSecureProtocolIgnored(boolean ignoreSecureProtocol) {
		this.ignoreSecureProtocol = ignoreSecureProtocol;
	}
	
	/**
	 * Check if secure protocol (HTTPS) is ignored or not
	 * @return
	 */
	public boolean isSecureProtocolIgnored() {
		return ignoreSecureProtocol;
	}

	
	// CONSTRUCTORS

	/**
	 * Private constructor for singleton
	 * @param url with php file for receive petitions
	 */
	private Database(String url) {
		this.url = url;
	}

	/**
	 * Get a database instance
	 * @param url with php file for receive petitions
	 */
	public static Database getInstance(String url) {
		if (singleton == null) {
			singleton = new Database(url);
		}
		return singleton;
	}
	
	
	// PRIVATE METHODS
		
	/**
	 * Make a post request to the PHP URL for process request and get data in JSON format
	 * @param json with data to send
	 * @return JSON with data received
	 * @throws IOException
	 * @throws DatabaseException 
	 */
	private String makePostRequest(String json) throws IOException, DatabaseException {
		
		if (json == null) {
			throw new NullPointerException("json cannot be null");
		}
		if(!ignoreSecureProtocol && url.toUpperCase().startsWith("HTTPS")){
			throw new SecureProtocolException("URL Protocol is not valid. Please use HTTPS or set true ignore secure protocol");
		}
		
		StringBuffer data = null;
		URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        
        OutputStream os = con.getOutputStream();
        os.write(("isTransaction="+isTransaction+"&data="+json).getBytes());
        os.flush();
        os.close();
        
		int response = con.getResponseCode();
		if (response == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			String line = null;
			data = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				data.append(line);
			}			
			String js = data.substring(data.indexOf("{"));
			return js;
		} else {
			throw new DatabaseException("HTTP connection fails. Response code: " + response);
		}
	}

	/**
	 * Execute a transaction
	 * @param transactOperation
	 * @return true if transaction execute properly or false in otherwise
	 * @throws SQLException
	 * @throws DatabaseException 
	 */
	private boolean transactOperation(TransactOperation transactOperation) throws SQLException, DatabaseException {
		String json = null;
		try {
			json = makePostRequest(transactOperation.toString());
		} catch (IOException e) {
			throw new DatabaseException("An error ocurred when try to get data from database -> " + e.getMessage());
		}
		if (json != null) {
			DatabaseResponse response = new DatabaseResponse(new JSONObject(json));
			message = response.getMessage();
			code = response.getCode();
			return response.getDataBoolean();
		} else {
			return false;
		}
	}
	
	
	// PUBLIC METHODS

	/**
	 * Execute a stored procedures or functions in database with parameterised values
	 * @param statement
	 * @param bindValues
	 * @return Database Response with data
	 * @throws SQLException
	 * @throws DatabaseException 
	 */
	public DatabaseResponse execute(String statement, ColumnSet bindValues) throws SQLException, DatabaseException {
		Operation operation = Operation.getExecuteOperation(statement, bindValues);
		String json;
		try {
			json = makePostRequest(operation.toJSON().toString());
		} catch (JSONException | IOException e) {
			throw new DatabaseException("An error ocurred when try to execute statement into database -> " + e.getMessage());
		}
		if (json != null) {
			DatabaseResponse response = new DatabaseResponse(new JSONObject(json));
			message = response.getMessage();
			code = response.getCode();
			return response;
		} else {
			code = -1;
			return null;
		}
	}

	/**
	 * Execute a raw query in database with parameterised values
	 * @param statement
	 * @param bindValues
	 * @return DataSet with result of query
	 * @throws SQLException
	 * @throws DatabaseException 
	 */
	public DataSet rawQuery(String statement, ColumnSet bindValues) throws SQLException, DatabaseException {
		Operation operation = Operation.getRawQueryOperation(statement, bindValues);
		String json;
		try {
			json = makePostRequest(operation.toJSON().toString());
		} catch (JSONException | IOException e) {
			throw new DatabaseException("An error ocurred when try to get data from database -> " + e.getMessage());
		}
		if (json != null) {
			DatabaseResponse response = new DatabaseResponse(new JSONObject(json));
			message = response.getMessage();
			code = response.getCode();
			return new DataSet(response.getData());
		} else {
			code = -1;
			return new DataSet();
		}
	}

	/**
	 * Execute a parameterised query in database
	 * 
	 * @param table
	 * @param projection (if null return all columns)
	 * @param where (if null return all rows)
	 * @param whereArgs (allow null)
	 * @param groupBy (allow null)
	 * @param having (allow null)
	 * @param orderBy (allow null)
	 * @param limit (allow null)
	 * @return DataSet with result of query
	 * @throws SQLException
	 * @throws DatabaseException 
	 */
	public DataSet query(String table, String[] projection, String where, ColumnSet whereArgs, String groupBy, String having, String orderBy, Integer limit) throws SQLException, DatabaseException {
		Operation operation = Operation.getQueryOperation(table, projection, where, whereArgs, groupBy, having, orderBy, limit);
		String json;
		try {
			json = makePostRequest(operation.toJSON().toString());
		} catch (JSONException | IOException e) {
			throw new DatabaseException("An error ocurred when try to get data from database -> " + e.getMessage());
		}
		if (json != null) {
			DatabaseResponse response = new DatabaseResponse(new JSONObject(json));
			message = response.getMessage();
			code = response.getCode();
			return new DataSet(response.getData());
		} else {
			return new DataSet();
		}
	}

	/**
	 * Execute a parameterised insert operation in database
	 * @param table
	 * @param values (Can`t be null)
	 * @param returnId (true if need to return the last inserted id or false for return the number affected rows)
	 * @return Return an integer with the number affected rows (if returnId is false) or last inserted id if is numeric(if returnId is true, if not numeric primary, return 0)
	 * @throws SQLException
	 * @throws DatabaseException 
	 */
	public int insert(String table, ColumnSet values, boolean returnId) throws SQLException, DatabaseException {
		Operation operation = Operation.getInsertOperation(table, values, returnId);
		if (isTransaction) {
			transactOperation.add(operation);
			return -1;
		} else {
			String json;
			try {
				json = makePostRequest(operation.toJSON().toString());
			} catch (JSONException | IOException e) {
				throw new DatabaseException("An error ocurred when try to insert data into database -> " + e.getMessage());
			}
			if (json != null) {
				DatabaseResponse response = new DatabaseResponse(new JSONObject(json));
				message = response.getMessage();
				code = response.getCode();
				return response.getDataCount();
			} else {
				return -1;
			}
		}
	}
	
	/**
	 * Execute a parameterised update operation in database 
	 * @param table
	 * @param values 
	 * @param where (Allow null)
	 * @param whereArgs (Allow null)
	 * @return Integer with the number of affected rows
	 * @throws SQLException
	 * @throws DatabaseException 
	 */
	public int update(String table, ColumnSet values, String where, ColumnSet whereArgs) throws SQLException, DatabaseException {
		Operation operation = Operation.getUpdateOperation(table, values, where, whereArgs);
		if (isTransaction) {
			transactOperation.add(operation);
			return -1;
		} else {
			String json;
			try {
				json = makePostRequest(operation.toJSON().toString());
			} catch (JSONException | IOException e) {
				throw new DatabaseException("An error ocurred when try to update data from database -> " + e.getMessage());
			}
			if (json != null) {
				DatabaseResponse response = new DatabaseResponse(new JSONObject(json));
				message = response.getMessage();
				code = response.getCode();
				return response.getDataCount();
			} else {
				return -1;
			}
		}
	}

	/**
	 * Execute a parameterised delete operation in database 
	 * @param table
	 * @param where
	 * @param whereArgs
	 * @return Integer with the number of affected rows
	 * @throws SQLException
	 * @throws DatabaseException 
	 */
	public int delete(String table, String where, ColumnSet whereArgs) throws SQLException, DatabaseException {
		Operation operation = Operation.getDeleteOperation(table, where, whereArgs);
		if (isTransaction) {
			transactOperation.add(operation);
			return -1;
		} else {
			String json;
			try {
				json = makePostRequest(operation.toJSON().toString());
			} catch (JSONException | IOException e) {
				throw new DatabaseException("An error ocurred when try to delete data from database -> " + e.getMessage());
			}
			if (json != null) {
				DatabaseResponse response = new DatabaseResponse(new JSONObject(json));
				message = response.getMessage();
				code = response.getCode();
				return response.getDataCount();
			} else {
				return -1;
			}
		}
	}

	/**
	 * Execute a SQL Script into Database
	 * @param scriptPath
	 * @throws SQLException
	 * @throws DatabaseException
	 * @throws IOException 
	 */
	public void executeScript(String scriptPath) throws  SQLException, DatabaseException, IOException {
		StringBuffer sb = new StringBuffer();
		File script = new File(scriptPath);
		BufferedReader reader = new BufferedReader(new FileReader(script));
		String line = "";
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		reader.close();
		Operation operation = Operation.getExecuteScriptOperation(sb.toString());
		if(isTransaction){
			transactOperation.add(operation);
			return;
		}
		else{
			String json;
			try {
				json = makePostRequest(operation.toJSON().toString());
			} catch (JSONException | IOException e) {
				throw new DatabaseException("An error ocurred when try to execute script: " + scriptPath + " -> " + e.getMessage());
			}
			if (json != null) {
				DatabaseResponse response = new DatabaseResponse(new JSONObject(json));
				message = response.getMessage();
				code = response.getCode();
			} else {
				throw new DatabaseException("An error ocurred when try to execute script: " + scriptPath);
			}
		}
	}
	
	/**
	 * Start transaction
	 */
	public void startTransaction() {
		transactOperation = new TransactOperation();
		isTransaction = true;
	}

	/**
	 * End transaction
	 */
	public void endTransaction() {
		transactOperation = null;
		isTransaction = false;
	}

	/**
	 * Execute transaction in database
	 * @return return true if transaction is correct or false in otherwise
	 * @throws IOException
	 * @throws JSONException
	 * @throws SQLException
	 * @throws DatabaseException 
	 */
	public boolean executeTransaction() throws SQLException, DatabaseException {
		boolean result = false;
		if (isTransaction) {
			result = transactOperation(transactOperation);
			isTransaction = false;
			transactOperation = null;
		} else {
			throw new SQLException("There is no current transaction , you must first call startTransaction()",ExceptionType.notTransactionStarted.getCode());
		}
		return result;
	}

}
