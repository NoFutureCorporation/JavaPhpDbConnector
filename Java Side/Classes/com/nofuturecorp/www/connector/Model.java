/*
  +---------------------------------------------------------------------------+
  |    Class: Model				                                              |
  |  Version: 1.0.0                                                           |
  |  Project: Java-Php-DBConnector                                            |
  | ------------------------------------------------------------------------- |
  |     Admin: Juan José Longoria López                                       |
  |    Author: Juan José Longoria López                                       |
  |   License: GNU GENERAL PUBLIC LICENSE 2.0.							      |
  +---------------------------------------------------------------------------+
 */

package com.nofuturecorp.www.connector;

/**
 * Interface must implement the Model classes to build them quickly with a query data 
 * and perform a mapping fields to send to the database.
 * @author Juan José Longoria López
 * @version 1.0
 */
public interface Model {

	/**
	 * Construct the object with a ColumnSet data
	 * @param columnSet
	 */
	public void fromSQL(ColumnSet columnSet);
	
	/**
	 * Mapping the object in a ColumnSet for send to database
	 * @return
	 */
	public ColumnSet toSQL(); 
	
}
