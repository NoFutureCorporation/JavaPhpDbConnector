/*
  +---------------------------------------------------------------------------+
  |    Class: ColumnSet                                                       |
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Class that contains the column values of each row and also used to map the values parameterized queries
 * @author Juan José Longoria
 * @version 1.0
 */
public class ColumnSet implements Serializable {

	// PRIVATE CONSTANTS
	
	private static final long serialVersionUID = 1049528064981418232L;

	
	// FIELDS
	
	private HashMap<String, Object> fields;
	
	
	// GETTERS AND SETTERS	

	/**
	 * Returns the Integer value of the requested column
	 * @param columnName
	 * @return Integer or null
	 */
	public Integer getInt(String columnName){
		if(this.columnCount() > 0){
			try {
				return Integer.valueOf(fields.get(columnName).toString());
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
		if(this.columnCount() > 0){
			try {
				return Double.valueOf(fields.get(columnName).toString());
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
		if(this.columnCount() > 0){
			try {
				return fields.get(columnName).toString();
			} catch (Exception e) {
				return null;
			}
		}
		else{
			return null;
		}
	}
		
//	public byte[] getBlob(String columnName){
//		if(this.columnCount() > 0){
//			try {
//				return (byte[]) fields.get(columnName);
//			} catch (Exception e) {
//				return null;
//			}
//		}
//		else{
//			return null;
//		}
//	}
	
	/**
	 * Returns the Boolean value of the requested column
	 * @param columnName
	 * @return Boolean or null
	 */
	public Boolean getBoolean(String columnName){
		if(this.columnCount() >= 0){
			try {
				if(Integer.valueOf(fields.get(columnName).toString()) == 1){
					return true;
				}
				else{
					return false;
				}
			} catch (Exception e) {
				return null;
			}
		}
		else{
			return null;
		}
	}
	
	
	// CONSTRUCTORS
	
	public ColumnSet() {
		fields = new HashMap<String, Object>();
	}
	
	
	// PRIVATE METHODS
		
	// PUBLIC METHODS
	
	/**
	 * Put new Value with String key
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value){
		if(value == null){
			fields.put(key, "null");
		}
		else if(value instanceof String){
			fields.put(key, (String) value);
		}
		else if(value instanceof Integer){
			fields.put(key, (Integer) value);
		}
		else if(value instanceof Double){
			fields.put(key, (Double) value);
		}
		else if(value instanceof Boolean){
			fields.put(key, String.valueOf(value));
		}
		else if(value instanceof byte[]){
			fields.put(key, (byte[])value);
		}
	}
	
	/**
	 * Get entry set of Columns map
	 * @return
	 */
	public Set<Entry<String, Object>> entrySet(){
		return fields.entrySet();
	}
	
	/**
	 * Get KeySet of Columns map
	 * @return
	 */
	public Set<String> keySet(){
		return fields.keySet();
	}
	
	/**
	 * Get the number of columns
	 * @return
	 */
	public int columnCount(){
		return fields.size();
	}
	
	/**
	 * Removes all items that match the pattern (Regexp pattern)
	 * @param pattern
	 */
	public void removeContains(String pattern){
		for (Iterator<Entry<String, Object>> iterator = fields.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> element = (Entry<String, Object>) iterator.next();
			if(element.getKey().matches(pattern)){
				iterator.remove();
			}
		}
	}
	
	
	// OVERRIDE METHODS
	
}
