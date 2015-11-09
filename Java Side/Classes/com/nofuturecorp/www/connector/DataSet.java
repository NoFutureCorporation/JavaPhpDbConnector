/*
  +---------------------------------------------------------------------------+
  |    Class: DataSet			                                              |
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
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nofuturecorp.www.connector.interfaces.JSONTransform;

/**
 * Class to store the extracted rows of a query. The elements can be extracted from various different ways, 
 * similar to a list but also as a cursor (like an Android) or other iterable elements
 * @author Juan José Longoria López
 * @version 1.0
 */
public class DataSet implements JSONTransform, Iterable<ColumnSet>, Serializable {

	// CONSTANTS

	private static final long serialVersionUID = -609845876155175818L;
	
	// FIELDS
	
	private ArrayList<ColumnSet> dataSet;
	private int position;
	
	
	// GETTERS AND SETTERS
	
	/**
	 * Returns the Integer value of the requested column
	 * @param columnName
	 * @return Integer or null
	 */
	public Integer getInt(String columnName){
		if(dataSet != null && dataSet.size() > 0){
			try {
				return dataSet.get(position).getInt(columnName);
			} catch (Exception e) {
				return null;
			}
		}
		else{
			return null;
		}
	}
	
	/**
	 * Returns the Double value of the requested column
	 * @param columnName
	 * @return Double or null
	 */
	public Double getDouble(String columnName){
		if(dataSet != null && dataSet.size() > 0){
			try {
				return (Double) dataSet.get(position).getDouble(columnName);
			} catch (Exception e) {
				return null;
			}
		}
		else{
			return null;
		}
	}
	
	/**
	 * Returns the String value of the requested column
	 * @param columnName
	 * @return String or null
	 */
	public String getString(String columnName){
		if(dataSet != null && dataSet.size() > 0){
			try {
				return dataSet.get(position).getString(columnName);
			} catch (Exception e) {
				return null;
			}
		}
		else{
			return null;
		}
	}
	
	/**
	 * Returns the Boolean value of the requested column
	 * @param columnName
	 * @return Boolean or null
	 */
	public Boolean getBoolean(String columnName){
		if(dataSet != null && dataSet.size() > 0){
			try {
				return dataSet.get(position).getBoolean(columnName);
			} catch (Exception e) {
				return null;
			}
		}
		else{
			return null;
		}
	}
	
//	public byte[] getBlob(String columnName){
//		if(dataSet != null && dataSet.size() > 0){
//			try {
//				return (byte[]) dataSet.get(position).getBlob(columnName);
//			} catch (Exception e) {
//				return null;
//			}
//		}
//		else{
//			return null;
//		}
//	}
	
	/**
	 * Return Column name with index columnIndex
	 * @param columnIndex
	 * @return Column Name or null
	 * @throws IndexOutOfBoundsException
	 */
	public String getColumnName(int columnIndex) {
		try {
			if(columnIndex < 0){
				throw new IndexOutOfBoundsException("columnIndex cannot be negative");
			}
			if(dataSet.get(columnIndex).keySet().size() - 1 > columnIndex){
				throw new IndexOutOfBoundsException("columnIndex exceed last column index");
			}
			return new ArrayList<String>(dataSet.get(columnIndex).keySet()).get(columnIndex);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Return row of rowIndex
	 * @param rowIndex
	 * @return ColumnSet with row data or null
	 */
	public ColumnSet getRow(int rowIndex){
		try {
			if(rowIndex < 0){
				throw new IndexOutOfBoundsException("rowIndex cannot be negative");
			}
			if(dataSet.size() - 1 > rowIndex){
				throw new IndexOutOfBoundsException("rowIndex exceed last row index");
			}
			if(dataSet.size() == 0){
				return null;
			}
			return dataSet.get(rowIndex);
		} catch (Exception e) {
			return null;
		}
	}
	
	
	// CONSTRUCTORS
	
	public DataSet() {
		position = -1;
		dataSet = new ArrayList<ColumnSet>();
	}
	
	public DataSet(JSONArray json) throws JSONException{
		this();
		if(json != null)
			fromJSONArray(json);
	}
	
	public DataSet(JSONObject json) throws JSONException{
		this();
		if(json != null)
			fromJSON(json);
	}
	
	// PRIVATE METHODS
	
	
	// PUBLIC METHODS	
	
	/**
	 * Return the number of rows
	 * @return Integer with number of rows
	 */
	public int rowCount(){
		if(dataSet != null){
			return dataSet.size();
		}
		else{
			return 0;
		}
	}
	
	/**
	 * Return the number of columns in selected row
	 * @return Integer with number of columns
	 */
	public int columnCount(){
		if(dataSet != null && dataSet.size() > 0){
			return dataSet.get(position).columnCount();
		}
		else{
			return 0;
		}
	}
	
	/**
	 * Move to first row in DataSet
	 * @return true if can move to the first element, false in otherwise
	 */
	public boolean moveToFirst(){
		if(dataSet != null && dataSet.size() > 0){
			position = 0;
			return true;
		}
		else{
			position = -1;
			return false;
		}
	}
	
	/**
	 * Move to last row in DataSet
	 * @return true if can move to the last element, false in otherwise
	 */
	public boolean moveToLast(){
		if(dataSet != null && dataSet.size() > 0){
			position = dataSet.size() - 1;
			return true;
		}
		else{
			position = -1;
			return false;
		}
	}
	
	/**
	 * Move to the selected position in Dataset
	 * @param position
	 * @return true if can move to the position, false in otherwise
	 */
	public boolean moveToPosition(int position){
		if(dataSet != null && dataSet.size() > 0){
			try {
				this.position = position;
				dataSet.get(position);
				return true;
			} catch (Exception e) {
				this.position = -1;
				return false;
			}
		}
		else{
			this.position = -1;
			return false;
		}
	}
	
	/**
	 * Return if has more elements to move
	 * @return true if has more elements or false in otherwise
	 */
	public boolean hasMoreElements(){
		if(position < dataSet.size()){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Return the next row
	 * @return next row or null if not has more rows
	 */
	public ColumnSet next(){
		if(position == dataSet.size()){
			return null;
		}
		return dataSet.get(position++);
	}
	
	// OVERRIDE METHODS
	
	@Override
	public JSONObject toJSON() throws JSONException {
		return null;
	}

	@Override
	public void fromJSON(String json) throws JSONException {
		
	}

	@Override
	public void fromJSON(JSONObject json) throws JSONException {
		
	}

	@Override
	public void fromJSONArray(JSONArray json) throws JSONException {
		for (int i = 0; i < json.length(); i++) {
			JSONObject data = json.getJSONObject(i);
			JSONArray names = data.names();
			ColumnSet set = new ColumnSet();
			for (int j = 0; j < names.length(); j++) {
				set.put(names.getString(j), data.get(names.getString(j)));
			}
			dataSet.add(set);
		}
	}
	
	@Override
	public Iterator<ColumnSet> iterator() {
		return new Iterator<ColumnSet>() {

			@Override
			public boolean hasNext() {
				if(dataSet != null && dataSet.size() > 0){
					if(position >= dataSet.size() - 1){
						position = -1;
						return false;
					}
					else{
						return true;
					}
				}
				else{
					position = -1;
					return false;
				}
			}

			@Override
			public ColumnSet next() {
				if(position == dataSet.size()){
					throw new NoSuchElementException();
				}
				position++;
				return dataSet.get(position);
			}

			@Override
			public void remove() {
				
			}
			
		};
	}

}
