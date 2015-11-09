<?php

/*
  +---------------------------------------------------------------------------+
  |    File: Request Handler                                                  |
  |  Version: 1.0.0                                                           |
  |  Project: Java-Php-DBConnector                                            |
  | ------------------------------------------------------------------------- |
  |     Admin: Juan José Longoria López                                       |
  |    Author: Juan José Longoria López                                       |
  |   License: GNU GENERAL PUBLIC LICENSE 2.0.                                |
  +---------------------------------------------------------------------------+
 */

require './DataBase.Class.php';
header('Content-type: application/json');

//  Get data from post request
$postData = file_get_contents('php://input');
$values = split("&", $postData);

// Extract data from post body
$post = array();
$post["isTransaction"] = split("=", $values[0]);
$post["data"] = substr($values[1], strlen("data="));
$isTransaction = $post["isTransaction"];
$json = $post["data"];

// Decode data from json to an Array
$operationArray = json_decode(utf8_encode($json), true);

//
//  IMPORTANT INFO FOR CONFIG AND ACCESS TO DATABASE
//

// DB DATA
$dbType = "mysql";
$dbName = "test";
$host = "127.0.0.1";
$charset = "UTF8";
$username = "root";
$password = "kankone";

// Make DSN to connect to database with DB DATA data
$dsn = $dbType.":dbname=".$dbName.";host=".$host.";charset=".$charset;

$datasource = new MySQLDataBase($dsn, $username, $password);

//
// MAKE DATABASE PETITIONS
//

try {
    // Open database
    if ($datasource->openDB()) {
        if ($isTransaction[1] === "true") {
            
            //
            // Is in a transaction
            //
            
            $operations = array();

            for ($index = 0; $index < count($operationArray); $index++) {
                $value = $operationArray[$index];
                $operation = MySQLOperation::createOperation($value);
                $operations[$index] = $operation;
            }
            $response = null;
            try {
                $data = $datasource->executeTransactOperations($operations);
                $response = Response::makeCustomResponse($datasource->getResponseCode(), $datasource->getDBMessage(), $data, null);
            } catch (Exception $ex){
                $response = Response::makeCustomResponse($datasource->getResponseCode(), $datasource->getDBMessage(), false, $datasource->getException());
            }

            $datasource->closeDB();
            echo json_encode($response);
        } else {
            
            //
            // Not in a transaction
            //

            $operation = MySQLOperation::createOperation($operationArray);
            $response = null;
            try {
                $data = $datasource->executeOperation($operation);
                $response = Response::makeCustomResponse($datasource->getResponseCode(), $datasource->getDBMessage(), $data, $datasource->getException());
            }catch (Exception $ex){
                $response = Response::makeCustomResponse($datasource->getResponseCode(), $datasource->getDBMessage(), $data, $datasource->getException());
            }

            $datasource->closeDB();
            echo json_encode($response);
        }
    } else {
        //
        // Database not open
        //
        $response->makeCustomResponse(-1, "Database is closed", null, $datasource->getException());
        echo json_encode($response);
    }
} catch (Exception $ex) {
    //
    // Exception thrown
    //
    $response->makeCustomResponse(-1, $ex->getMessage(), null, ExceptionCodes::UNKNOWN_ERROR);
    echo json_encode($response);
}


