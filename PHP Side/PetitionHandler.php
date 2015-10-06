<?php

/*
  +---------------------------------------------------------------------------+
  |    File: Petition Handler                                                 |
  |  Version: 1.0.0                                                           |
  |  Project: Java-Php-DBConnector                                            |
  | ------------------------------------------------------------------------- |
  |     Admin: Juan José Longoria López                                       |
  |    Author: Juan José Longoria López                                       |
  |   License: GNU GENERAL PUBLIC LICENSE 2.0.                                |
  +---------------------------------------------------------------------------+
 */

require './DataBase.Class.php';
//header('Content-type: application/json');
$postData = file_get_contents('php://input');
$values = split("&", $postData);
$post = array();
for ($i = 0; $i < count($values); $i++) {
    $tmp = split("=", $values[$i]);
    $post[$tmp[0]] = $tmp[1];
}
$isTransaction = $post["isTransaction"] === 'true';
$json = $post["data"];
$operationArray = json_decode(utf8_encode($json), true);
$datasource = new MySQLDataBase("mysql:dbname=test;host=127.0.0.1;charset=UTF8", "root", "kankone");

try {
    if ($datasource->openDB()) {
        if ($isTransaction) {
            
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
        $response->makeCustomResponse(-1, "Database is closed", null, $datasource->getException());
        echo json_encode($response);
    }
} catch (Exception $ex) {
    $response->makeCustomResponse(-1, $ex->getMessage(), null, ExceptionCodes::UNKNOWN_ERROR);
    echo json_encode($response);
}


