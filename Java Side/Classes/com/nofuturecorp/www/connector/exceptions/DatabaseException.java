/*
  +---------------------------------------------------------------------------+
  |    Class: DatabaseException		                                          |
  |  Version: 1.0.0                                                           |
  |  Project: Java-Php-DBConnector                                            |
  | ------------------------------------------------------------------------- |
  |     Admin: Juan José Longoria López                                       |
  |    Author: Juan José Longoria López                                       |
  |   License: GNU GENERAL PUBLIC LICENSE 2.0.							      |
  +---------------------------------------------------------------------------+
 */

package com.nofuturecorp.www.connector.exceptions;

/**
 * Class for throws database connection errors
 * @author Juan José Longoria López
 * @version 1.0
 */
public class DatabaseException extends Exception {

	private static final long serialVersionUID = 3730010284691831007L;

	public DatabaseException(String message) {
		super(message);
	}
	
}

