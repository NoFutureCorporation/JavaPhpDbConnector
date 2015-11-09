/*
  +---------------------------------------------------------------------------+
  |    Class: SecureProtocolException                                         |
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
 * Class for throws error about the URL protocol
 * @author Juan José Longoria López
 * @version 1.0
 */
public class SecureProtocolException extends RuntimeException {

	private static final long serialVersionUID = 2012978144336638638L;

	public SecureProtocolException(String message) {
		super(message);
	}
	
}
