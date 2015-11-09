/*
  +---------------------------------------------------------------------------+
  |    Class: SQLException		                                              |
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
 * Class for throws SQL Exception for handling database errors
 * @author Juan José Longoria López
 * @version 1.0
 */
public class SQLException extends Exception{

	private static final long serialVersionUID = -1488604288991862617L;

	// EXCEPTION TYPES
	
	/**
	 * Enumeration with error types
	 * @author Juan José Longoria López
	 * @version 1.0
	 */
	public enum ExceptionType{
		
		unknownError(-1),
		databaseNotConnect(1),
		duplicateEntry(2),
		nullTable(3),
		nullValues(4),
		unknownColumn(5),
		foreignKeyConstraintFails(6),
		syntaxError(7),
		nullColumn(8),
		invalidType(9),
		notTransactionStarted(10),
		dataTruncated(11),
		nullScript(12),
		scriptError(13);
		
		
		private int code;
		
		/**
		 * Return exception code
		 * @return exception code
		 */
		public int getCode() {
			return code;
		}
		
		private ExceptionType(int code) {
			this.code = code;
		}		
	}
	
	
	// FIELDS
	
	private ExceptionType type;
	
	
	// GETTERS AND SETTERS
	
	/**
	 * Return exception type
	 * @return exception type
	 */
	public ExceptionType getType() {
		return type;
	}
	
	
	// CONSTRUCTORS
	
	/**
	 * Constructor with Message and Exception code
	 * @param message
	 * @param code
	 */
	public SQLException(String message, int code) {
		super(message);
		type = ExceptionType.unknownError;
		for (ExceptionType ex : ExceptionType.values()) {
			if(ex.getCode() == code){
				type = ex;
				break;
			}
		}
	}
	
	
	
}
