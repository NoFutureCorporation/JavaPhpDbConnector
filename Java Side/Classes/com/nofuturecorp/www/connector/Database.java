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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
	 * Make a post request to the PHP url for process request and get data in JSON format
	 * @param path
	 * @param json with data to send
	 * @return JSON with data
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private String makePostRequest(String path, String json) throws ClientProtocolException, IOException {
		if (path == null) {
			throw new NullPointerException("path cannot be null");
		}
		if (json == null) {
			throw new NullPointerException("json cannot be null");
		}
		StringBuilder data = null;
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost postRequest = new HttpPost(url);
		
		ArrayList<NameValuePair> values = new ArrayList<>();
		values.add(new BasicNameValuePair("isTransaction", String.valueOf(isTransaction)));
		values.add(new BasicNameValuePair("data", json));
		postRequest.addHeader("content-type", "application/json");
	    postRequest.addHeader("Accept","application/json");
		postRequest.setEntity(new StringEntity("isTransaction="+isTransaction+"&data="+json));
		HttpResponse response = client.execute(postRequest);
						
		if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
			String line = null;
			data = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				data.append(line);
			}
			String js = data.substring(data.indexOf("{"));
			return js;
		} else {
			return null;
		}
	}

	/**
	 * Execute a transaction
	 * @param transactOperation
	 * @return true if transaction execute properly or false in otherwise
	 * @throws IOException
	 * @throws JSONException
	 * @throws SQLException
	 */
	private boolean transactOperation(TransactOperation transactOperation) throws IOException, JSONException, SQLException {
		String json = makePostRequest(url, transactOperation.toString());
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
	 * Execute a stored procedures or functions in database with parameterized values
	 * @param statement
	 * @param bindValues
	 * @return Database Response with data
	 * @throws IOException
	 * @throws JSONException
	 * @throws SQLException
	 */
	public DatabaseResponse execute(String statement, ColumnSet bindValues) throws IOException, JSONException, SQLException {
		Operation operation = Operation.getExecuteOperation(statement, bindValues);
		String json = makePostRequest(url, operation.toJSON().toString());
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
	 * Execute a raw query in database with parameterized values
	 * @param statement
	 * @param bindValues
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 * @throws SQLException
	 */
	public DataSet rawQuery(String statement, ColumnSet bindValues) throws IOException, JSONException, SQLException {
		Operation operation = Operation.getRawQueryOperation(statement, bindValues);
		String json = makePostRequest(url, operation.toJSON().toString());
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
	 * Execute a parameterized query in database
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
	 * @throws JSONException
	 * @throws IOException
	 * @throws SQLException
	 */
	public DataSet query(String table, String[] projection, String where, ColumnSet whereArgs, String groupBy, String having, String orderBy, Integer limit) throws JSONException, IOException, SQLException {
		Operation operation = Operation.getQueryOperation(table, projection, where, whereArgs, groupBy, having, orderBy, limit);
		String json = makePostRequest(url, operation.toJSON().toString());
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
	 * Execute a parameterized insert operation in database
	 * @param table
	 * @param values (Can`t be null)
	 * @param returnId (true if need to return the last inserted id or false for return the number affected rows)
	 * @return Return an integer with the number affected rows (if returnId is false) or last inserted id if is numeric(if returnId is true, if not numeric primary, return 0)
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int insert(String table, ColumnSet values, boolean returnId) throws URISyntaxException, IOException, JSONException, SQLException {
		Operation operation = Operation.getInsertOperation(table, values, returnId);
		if (isTransaction) {
			transactOperation.add(operation);
			return -1;
		} else {
			String json = makePostRequest(url, operation.toJSON().toString());
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
	 * Execute a parameterized update operation in database 
	 * @param table
	 * @param values 
	 * @param where (Allow null)
	 * @param whereArgs (Allow null)
	 * @return Integer with the number of affected rows
	 * @throws IOException
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int update(String table, ColumnSet values, String where, ColumnSet whereArgs) throws  IOException, JSONException, SQLException {
		Operation operation = Operation.getUpdateOperation(table, values, where, whereArgs);
		if (isTransaction) {
			transactOperation.add(operation);
			return -1;
		} else {
			String json = makePostRequest(url, operation.toJSON().toString());
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
	 * Execute a parameterized delete operation in database 
	 * @param table
	 * @param where
	 * @param whereArgs
	 * @return Integer with the number of affected rows
	 * @throws IOException
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int delete(String table, String where, ColumnSet whereArgs) throws  IOException, JSONException, SQLException {
		Operation operation = Operation.getDeleteOperation(table, where, whereArgs);
		if (isTransaction) {
			transactOperation.add(operation);
			return -1;
		} else {
			String json = makePostRequest(url, operation.toJSON().toString());
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
	 */
	public boolean executeTransaction() throws IOException, JSONException, SQLException {
		if (isTransaction) {
			boolean result = transactOperation(transactOperation);
			isTransaction = false;
			transactOperation = null;
			return result;
		} else {
			return false;
		}
	}

}
