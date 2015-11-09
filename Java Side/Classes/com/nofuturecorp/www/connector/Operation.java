/*
  +---------------------------------------------------------------------------+
  |    Class: Operation			                                              |
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
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nofuturecorp.www.connector.interfaces.JSONTransform;

/**
 * Class that encapsulates all the data needed to be converted into JSON and send operations to the database
 * @author Juan José Longoria López
 * @version 1.0
 */
class Operation implements JSONTransform, Serializable {
		
	// CONSTANTS

	private static final long serialVersionUID = -6929669860420311385L;
	
	public static final int SELECT = 0;
	public static final int INSERT = 1;
	public static final int UPDATE = 2;
	public static final int DELETE = 3;
	public static final int STORED_PROCEDURE = 4;
	public static final int STORED_FUNCTION = 5;
	public static final int RAW_QUERY = 6;
	public static final int SCRIPT = 7;

	
	// FIELDS
	
	private int operation;
	private String table;
	private String[] projection;
	private String where;
	private ColumnSet whereArgs;
	private String groupBy;
	private String having;
	private String orderBy;
	private Integer limit;
	private ColumnSet values;
	private String statement;
	private ColumnSet bindValues;
	private boolean returnId;

	
	// GETTERS AND SETTERS
	
	/**
	 * Return operation code
	 * @return operation code
	 */
	public int getOperation() {
		return operation;
	}

	/**
	 * Set operation code
	 * @param operation
	 */
	public void setOperation(int operation) {
		this.operation = operation;
	}

	/**
	 * Return table name
	 * @return
	 */
	public String getTable() {
		return table;
	}

	/**
	 * Set table name
	 * @param table
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * Return projection columns
	 * @return projection columns
	 */
	public String[] getProjection() {
		return projection;
	}

	/**
	 * Set projection columns
	 * @param projection
	 */
	public void setProjection(String[] projection) {
		this.projection = projection;
	}

	/**
	 * Return where clause
	 * @return where clause
	 */
	public String getWhere() {
		return where;
	}

	/**
	 * Set where clause
	 * @param where
	 */
	public void setWhere(String where) {
		if(where != null && where.length() == 0){
			where = null;
		}
		this.where = where;
	}
	
	/**
	 * Return where args
	 * @return where args
	 */
	public ColumnSet getWhereArgs() {
		return whereArgs;
	}

	/**
	 * Set where args
	 * @param selectionArgs
	 */
	public void setWhereArgs(ColumnSet selectionArgs) {
		if(selectionArgs != null && selectionArgs.entrySet().size() == 0){
			selectionArgs = null;
		}
		this.whereArgs = selectionArgs;
	}

	/**
	 * Return group by clause
	 * @return group by clause
	 */
	public String getGroupBy() {
		return groupBy;
	}

	/**
	 * Set group by clause
	 * @param groupBy
	 */
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	/**
	 * Return having clause
	 * @return having clause
	 */
	public String getHaving() {
		return having;
	}

	/**
	 * Set having clause
	 * @param having
	 */
	public void setHaving(String having) {
		this.having = having;
	}

	/**
	 * Return order by clause
	 * @return order by clause
	 */
	public String getOrderBy() {
		return orderBy;
	}

	/**
	 * Set order by clause
	 * @param orderBy
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * Return limit caluse
	 * @return limit clause
	 */
	public Integer getLimit() {
		return limit;
	}

	/**
	 * Set limit clause
	 * @param limit
	 */
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	/**
	 * Return mapped values
	 * @return
	 */
	public ColumnSet getValues() {
		return values;
	}

	/**
	 * Set mapped values
	 * @param values
	 */
	public void setValues(ColumnSet values) {
		this.values = values;
	}

	/**
	 * Return raw statement
	 * @return raw statement
	 */
	public String getStatement() {
		return statement;
	}

	/**
	 * Set raw statement
	 * @param statement
	 */
	public void setStatement(String statement) {
		this.statement = statement;
	}

	/**
	 * Return mapped bind values
	 * @return
	 */
	public ColumnSet getBindValues() {
		return bindValues;
	}

	/**
	 * Set mapped bind values
	 * @param bindValues
	 */
	public void setBindValues(ColumnSet bindValues) {
		this.bindValues = bindValues;
	}

	
	// CONSTRUCTORS
	
	public Operation() {
		
	}

	/**
	 * Constructor with operation code
	 * @param operation
	 */
	public Operation(int operation) {
		this.operation = operation;
	}

	/**
	 * Return query operation
	 * @param table
	 * @param projection
	 * @param where
	 * @param whereArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @param limit
	 * @return query operation
	 */
	public static Operation getQueryOperation(String table,String[] projection, String where, ColumnSet whereArgs, String groupBy, String having, String orderBy, Integer limit){
		Operation operation = new Operation(SELECT);
		operation.table = table;
		operation.projection = projection;
		operation.where = where;
		operation.whereArgs = whereArgs;
		operation.groupBy = groupBy;
		operation.having = having;
		operation.orderBy = orderBy;
		operation.limit = limit;
		return operation;
	}
	
	/**
	 * Return Insert operation
	 * @param table
	 * @param values
	 * @param returnId
	 * @return insert operation
	 */
	public static Operation getInsertOperation(String table, ColumnSet values, boolean returnId){
		Operation operation = new Operation(INSERT);
		operation.table = table;
		operation.values = values;
		operation.returnId = returnId;
		return operation;
	}

	/**
	 * Return update operation
	 * @param table
	 * @param values
	 * @param where
	 * @param whereArgs
	 * @return update operation
	 */
	public static Operation getUpdateOperation(String table, ColumnSet values, String where, ColumnSet whereArgs){
		Operation operation = new Operation(UPDATE);
		operation.table = table;
		operation.values = values;
		operation.where = where;
		operation.whereArgs = whereArgs;
		return operation;
	}
	
	/**
	 * Return delete operation
	 * @param table
	 * @param where
	 * @param whereArgs
	 * @return delete operation
	 */
	public static Operation getDeleteOperation(String table, String where, ColumnSet whereArgs){
		Operation operation = new Operation(DELETE);
		operation.table = table;
		operation.where = where;
		operation.whereArgs = whereArgs;
		return operation;
	}

	/**
	 * Return raw query operation
	 * @param statement
	 * @param bindValues
	 * @return raw query operation
	 */
	public static Operation getRawQueryOperation(String statement, ColumnSet bindValues){
		Operation operation = new Operation(RAW_QUERY);
		operation.statement = statement;
		operation.bindValues = bindValues;
		return operation;
	}

	/**
	 * Return execute operation
	 * @param statement
	 * @param bindValues
	 * @return execute operation
	 */
	public static Operation getExecuteOperation(String statement, ColumnSet bindValues){
		Operation operation = new Operation(STORED_FUNCTION);
		operation.statement = statement;
		operation.bindValues = bindValues;
		return operation;
	}

	/**
	 * Return execute Script operation
	 * @param script
	 * @return execute Script Operation
	 */
	public static Operation getExecuteScriptOperation(String script){
		Operation operation = new Operation(SCRIPT);
		operation.statement = script;
		return operation;
	}
	
	// OVERRIDE METHODS
	
	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put("operation", operation);
		json.put("table", table);
		
		if(projection != null && projection.length > 0){
		JSONArray projectionArray = new JSONArray();
			for (String field : projection) {
				projectionArray.put(field);
			}
			json.put("projection", projectionArray);
		}
		
		json.put("where", where);
		
		if(whereArgs != null && whereArgs.columnCount() > 0){
			JSONObject whereArgsObject = new JSONObject();
			for (Entry<String,Object> entry : whereArgs.entrySet()) {
				if(entry.getKey() != null && entry.getValue() != null){
					whereArgsObject.put(entry.getKey(), entry.getValue());
				}
			}
			json.put("whereArgs", whereArgsObject);
		}
		
		json.put("groupBy", groupBy);
		json.put("having", having);
		json.put("orderBy", orderBy);
		json.put("limit", limit);
		
		if(values != null && values.columnCount() > 0){
			JSONObject valuesObject = new JSONObject();
			for (Entry<String,Object> entry : values.entrySet()) {
				if(entry.getKey() != null && entry.getValue() != null){
					valuesObject.put(entry.getKey(), entry.getValue());
				}
			}
			json.put("values", valuesObject);
		}
		
		json.put("statement", statement);
		
		if(bindValues != null && bindValues.columnCount() > 0){
			JSONObject bindValuesObject = new JSONObject();
			for (Entry<String,Object> entry : bindValues.entrySet()) {
				if(entry.getKey() != null && entry.getValue() != null){
					bindValuesObject.put(entry.getKey(), entry.getValue());
				}
			}
			json.put("bindValues", bindValuesObject);
		}
		json.put("returnId", String.valueOf(returnId));

		return json;
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

}

