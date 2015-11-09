/*
  +---------------------------------------------------------------------------+
  |    Class: JSONTransform		                                              |
  |  Version: 1.0.0                                                           |
  |  Project: Java-Php-DBConnector                                            |
  | ------------------------------------------------------------------------- |
  |     Admin: Juan José Longoria López                                       |
  |    Author: Juan José Longoria López                                       |
  |   License: GNU GENERAL PUBLIC LICENSE 2.0.							      |
  +---------------------------------------------------------------------------+
 */

package com.nofuturecorp.www.connector.interfaces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface to implement objects that need transformed into JSON or constructed with a JSON
 * @author Juan José Longoria López
 * @version 1.0
 */
public interface JSONTransform {

	/**
	 * Return object structure in JSON format
	 * @return
	 * @throws JSONException
	 */
	public JSONObject toJSON() throws JSONException;
	
	/**
	 * Construct the object with json data
	 * @param json
	 * @throws JSONException
	 */
	public void fromJSON(String json) throws JSONException;
	
	/**
	 * Construct the object with json object
	 * @param json
	 * @throws JSONException
	 */
	public void fromJSON(JSONObject json) throws JSONException;
	
	/**
	 * Construct the object with json Array
	 * @param json
	 * @throws JSONException
	 */
	public void fromJSONArray(JSONArray json) throws JSONException;
	
}
