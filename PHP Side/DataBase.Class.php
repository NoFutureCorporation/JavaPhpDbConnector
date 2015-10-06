<?php
/*
  +---------------------------------------------------------------------------+
  |    File: Database                                                         |
  |  Version: 1.0.0                                                           |
  |  Project: Java-Php-DBConnector                                            |
  | ------------------------------------------------------------------------- |
  |     Admin: Juan José Longoria López                                       |
  |    Author: Juan José Longoria López                                       |
  |   License: GNU GENERAL PUBLIC LICENSE 2.0.                                |
  +---------------------------------------------------------------------------+
 */

/**
 * Class for manage SQL database than use PDO library for connection
 * 
 * @author Juan José Longoria López
 * @version 1.0.0
 */
class MySQLDataBase {

    //----------//
    //  FIELDS  //
    //----------//
    
    private $db;
    private $mysql_dsn;
    private $username;
    private $password;
    private $dbMessage = "";
    private $responseCode = 0;
    private $keyInvolved = "";
    private $returnId = false;
    private $exception = null;
    private $pdoOptions = array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION);


    //---------------//
    //  CONSTRUCTOR  //
    //---------------//    
    

    /**
     * Constructor for connection
     * @param string $mysql_dsn Format => mysql:dbname=DatabaseName;host=ipHost;charset=CharacterSet   Example => mysql:dbname=exampleDB;host=127.0.0.1;charset=UTF8
     * @param string $username username of the MySQL database user
     * @param string $password password for MySQL database user
     */
    public function __construct($mysql_dsn, $username, $password) {
        $this->mysql_dsn = $mysql_dsn;
        $this->username = $username;
        $this->password = $password;
    }

    //-----------------------//
    //  GETTERS AND SETTERS  //
    //-----------------------//
    
    
    /**
     * Get the response code for the last operation
     * @return Integer with followin response codes
     */
    public function getResponseCode() {
        return $this->responseCode;
    }

    /**
     * Get the database message in case of error
     * @return Integer database message
     */
    public function getDBMessage() {
        return $this->dbMessage;
    }

    /**
     * Get the key involved in error duplicate entry for primary or unique columns,
     * @return String key involved
     */
    public function getKeyInvolved() {
        return $this->keyInvolved;
    }

    
    //---------------------//
    //  MAGNAMENT METHODS  //
    //---------------------//
    
    
    /**
     * Check if database is open
     * @return boolean Return true if database is open or false if is closed
     */
    public function isOpenDB() {
        if (is_null($this->db)) {
            return false;
        }
        return true;
    }

    /**
     * Open database connection
     * @return boolean Return true if database is openned or false if can't connect
     */
    public function openDB() {
        try {
            if (!$this->isOpenDB()) {
                $this->db = new PDO($this->mysql_dsn, $this->username, $this->password, $this->pdoOptions);
            }
        } catch (PDOException $exc) {
            $this->responseCode = -1;
            $this->dbMessage = $exc->getMessage();
            $this->exception = ExceptionCodes::DATABASE_NOT_CONNECT;
            return false;
        }
        return true;
    }

    /**
     * Close database connection
     */
    public function closeDB() {
        $this->db = NULL;
    }

    
    //-----------------------//
    //  TRANSACTION METHODS  //
    //-----------------------//
    
    
    /**
     * Start transaction
     */
    public function beginTransaction() {
        $this->db->beginTransaction();
    }

    /**
     * Commit transaction
     */
    public function commitTransaction() {
        $this->db->commit();
    }

    /**
     * Rollback transaction
     */
    public function rollbackTransaction() {
        $this->db->rollBack();
    }
    
    public function getException(){
        return $this->exception;
    }

    
    //---------------------//
    //  EXECUTING METHODS  //
    //---------------------//    
    
    
    /**
     * Execute a MySQLOperation
     * 
     * @param MySQLOperation $operation MySql precompiled operation
     * @return type can return all types of operations insert, delete, select, update, rawquery and execute
     * @throws Exception can throw all exceptions of operations insert, delete, select, update, rawquery and execute
     */
    public function executeOperation(MySQLOperation $operation) {
        $result = null;
        $this->returnId = $operation->getReturnId();
        switch ($operation->getOperation()) {
            case MySQLOperation::SELECT:
                $result = $this->query($operation->getTable(), $operation->getProjection(), $operation->getWhere(), $operation->getWhereArgs(), $operation->getGroupBy(), $operation->getHaving(), $operation->getOrderBy(), $operation->getLimit());
                break;
            case MySQLOperation::INSERT:
                $result = $this->insert($operation->getTable(), $operation->getValues());
                break;
            case MySQLOperation::UPDATE:
                $result = $this->update($operation->getTable(), $operation->getValues(), $operation->getWhere(), $operation->getWhereArgs());
                break;
            case MySQLOperation::DELETE:
                $result = $this->delete($operation->getTable(), $operation->getWhere(), $operation->getWhereArgs());
                break;
            case MySQLOperation::STORED_PROCEDURE:
            case MySQLOperation::STORED_FUNCTION:
                $result = $this->execute($operation->getStatement(), $operation->getBindValues());
                break;
            case MySQLOperation::RAW_QUERY:
                $result = $this->rawQuery($operation->getStatement(), $operation->getBindValues());
                break;
        }
        return $result;
    }

    /**
     * Execute an Array of MySQLOperations in atomitc operation with transactions
     * 
     * @param array $operations Array with MySQLOperation values
     * @return boolean
     * @throws Exception if $operations values are not MySQLOperation instance
     */
    public function executeTransactOperations($operations) {
        $this->db->beginTransaction();
        foreach ($operations as $value) {
            if ($value instanceof MySQLOperation) {
                $result = $this->executeOperation($value);
                if (is_null($result) || $result < 0) {
                    $this->rollbackTransaction();
                    return false;
                }
            }
        }

        $this->db->commit();
        return true;
    }

    
    //----------------//
    //  CRUD METHODS  //
    //----------------//
    
    /**
     * Execute sql statement that not return data or return only one result like stored procedures or functions like count, max, average...
     * 
     * @param string $statement statement for execute
     * @param Array $bindValues optional values for bind in statement
     * @return return data or null in case of error
     * @throws Exception in case of statement null or empty
     */
    public function execute($statement, $bindValues) {
        if ($this->isOpenDB()) {
            if (is_null($statement) || empty($statement)) {
                throw new Exception("statement cannot be null or empty", null, null);
            }

            $statement = $this->getPreparedStatement($statement);
            $statement = $this->bindValues($statement, $bindValues);

            return $this->getExecuteResult($statement);
        } else {
            $this->exception = ExceptionCodes::DATABASE_NOT_CONNECT;
            return null;
        }
    }
    
    /**
     * Make a raw query in database
     * @param string $query sql statement (example => select * from table where colum1 = :column1, column2 = :column2, ...)
     * @param Array $bindValues values for bind in query (example => [column1 => value1, column2 => value2, ...])
     * @return Array with data or null
     * @throws Exception in case of query is null or empty
     */
    public function rawQuery($query, $bindValues) {
        if ($this->isOpenDB()) {
            if (is_null($query) || empty($query)) {
                throw new Exception("query cannot be null or empty", null, null);
            }

            $statement = $this->getPreparedStatement($query);

            $statement = $this->bindValues($statement, $bindValues);

            return $this->getData($statement);
        } else {
            $this->exception = ExceptionCodes::DATABASE_NOT_CONNECT;
            return null;
        }
    }

    /**
     * Query in MySQL database
     * @param string $table table to query (if need join, you can do this like ==>> table1 join table2 on table1_pk = table2_fk join...)
     * @param array $projection colunm projection (make like this ==>> [column1, column2, column3,..., columnN])
     * @param string $selection where clause (make like this ==>> column1 = :column1 and column2 = :column2...)
     * @param array $selectionArgs where args (make like this ==>> [ column1 => value1, column2 => value2, ..., columnN => valueN])
     * @param string $groupBy group by clause
     * @param string $having habing clause
     * @param string $orderBy order by clause
     * @param int $limit limit clause
     * @return Array with data or null (Array => [row1 => [column1 => value1, 0 => value1, column2 => value2, 1 => value2, ...]])
     * @throws Exception if $table is null or is empty
     */
    public function query($table, $projection, $selection, $selectionArgs, $groupBy, $having, $orderBy, $limit) {
        if ($this->isOpenDB()) {
            $sql = "select";

            // Comprobamos si la proyeccion es o no nula o está vacía
            // En caso de ser nula o estar vacía proyectamos todos los valores
            if (is_null($projection) || empty($projection)) {
                $sql .= " *";
            } else {
                $cont = 0;
                foreach ($projection as $value) {
                    if ($cont == 0) {
                        $sql .= " " . $value;
                    } else {
                        $sql .= ", " . $value;
                    }
                    $cont += 1;
                }
            }

            // Comprobamos si la tabla no es nula
            if (is_null($table) || empty($table)) {
                $this->exception = ExceptionCodes::NULL_TABLE;
                throw new Exception("table cannot be null", NULL, NULL);
            }

            // Añadimos la tabla a la búsqueda
            $sql .= " from " . $table;

            // Comprobamos si la clausula where no es nula o no está vacía
            if (!is_null($selection) || !empty($selection)) {
                $sql .= " where " . $selection;
            }

            // Comprobamos si la clausula groupby no es nula o no esta vacia
            if (!is_null($groupBy) || !empty($groupBy)) {
                $sql .= " group by " . $groupBy;
            }

            // Comprobamos si la calusula having no es nula o no esta vacía
            if (!is_null($having) || !empty($having)) {
                $sql .= " having " . $having;
            }

            // Comprobamos si la clausula orderBy no es nula o no esta vacía
            if (!is_null($orderBy) || !empty($orderBy)) {
                $sql .= " order by " . $orderBy;
            }

            // comprobamos si la clausula limit no es nula o no esta vacía
            if (!is_null($limit) || !empty($limit)) {
                $sql .= " limit " . $limit;
            }

            // Obtenemos la consula sql preparada
            $statement = $this->getPreparedStatement($sql);
            if ((!is_null($selection) || !empty($selection)) && (!is_null($selectionArgs) || !empty($selectionArgs))) {
                $statement = $this->bindValues($statement, $selectionArgs);
            }

            return $this->getData($statement);
        } else {
            $this->exception = ExceptionCodes::DATABASE_NOT_CONNECT;
            return null;
        }
    }

    /**
     * Insert data in MySQL database
     * @param string $table table to insert
     * @param array $values values for insert (make like this ==>> [ column1 => value1, column2 => value2, ..., columnN => valueN])
     * @return Integer number of row affected or -1 in case of error
     * @throws Exception if $table or $values are null or empty
     */
    public function insert($table, $values) {
        if ($this->isOpenDB()) {
            if (is_null($table) || empty($table)) {
                $this->exception = ExceptionCodes::NULL_TABLE;
                throw new Exception("table cannot be null or empty", NULL, NULL);
            }
            if (is_null($values) || empty($values)) {
                $this->exception = ExceptionCodes::NULL_VALUES;
                throw new Exception("values cannot be null or empty", NULL, NULL);
            }

            // Preparamos la sencencia sql
            $sql = "insert into " . $table . " (";
            $val = "";
            $cont = 0;
            foreach ($values as $key => $value) {
                if ($cont == 0) {
                    $sql .= $key;
                    $val .= "(:" . $key;
                } else {
                    $sql .= ", " . $key;
                    $val .= ", :" . $key;
                }
                $cont += 1;
            }
            $val .= ")";
            $sql .= ") values " . $val;

            // Obtenemos la sentencia sql preparada
            $statement = $this->getPreparedStatement($sql);

            // Hacemos un binValue de los datos
            $statement = $this->bindValues($statement, $values);
            // Devolvemos el resultado de ejecutar el método getResult
            return $this->getInsertResult($statement);
        } else {
            $this->exception = ExceptionCodes::DATABASE_NOT_CONNECT;
            return -1;
        }
    }

    /**
     * Update data in MySQL database
     * @param string $table table to update
     * @param array $values values for update (make like this ==>> [ column1 => value1, column2 => value2, ..., columnN => valueN])
     * @param string $where where clause (make like this ==>> column1 = :column1 and column2 = :column2...)
     * @param array $whereArgs where args (make like this ==>> [ column1 => value1, column2 => value2, ..., columnN => valueN])
     * @return Integer number of row affected or -1 in case of error
     * @throws Exception if $table or $values are null or empty
     */
    public function update($table, $values, $where, $whereArgs) {

        if ($this->isOpenDB()) {
            if (is_null($table) || empty($table)) {
                $this->exception = ExceptionCodes::NULL_TABLE;
                throw new Exception("table cannot be null or empty", NULL, NULL);
            }
            if (is_null($values) || empty($values)) {
                $this->exception = ExceptionCodes::NULL_VALUES;
                throw new Exception("values cannot be null or empty", NULL, NULL);
            }

            // Preparamos la cabecera de la sentencia update
            $sql = "update " . $table . " set ";

            // Termiandos de preparar la sentencia con los campos que tenemos
            $cont = 0;
            foreach ($values as $key => $value) {
                if ($cont == 0) {
                    $sql .= $key . " = :" . $key;
                } else {
                    $sql .= ", " . $key . " = :" . $key;
                }
                $cont += 1;
            }

            if (!is_null($where) || !empty($where)) {
            // Añadimos la claúsula where al la sentencia sql
                $sql .= " where " . $where;
            }

            // Obtenemos la sentencia preparada como PDOStatement
            $statement = $this->getPreparedStatement($sql);

            // Hacemos un binValue de los datos
            $statement = $this->bindValues($statement, $values);

            if (!is_null($where) || !empty($where)) {
            // Hacmos un bindvalue de los argumentos del where
                $statement = $this->bindValues($statement, $whereArgs);
            }
            // Devolvemos el resultado de ejecutar el método getResult
            return $this->getResult($statement);
        } else {
            $this->exception = ExceptionCodes::DATABASE_NOT_CONNECT;
            return -1;
        }
    }

    /**
     * Delete data in MySQL database
     * @param string $table table to delete
     * @param string $where where clause (make like this ==>> column1 = :column1 and column2 = :column2...)
     * @param array $whereArgs where args (make like this ==>> [ column1 => value1, column2 => value2, ..., columnN => valueN])
     * @return Integer number of row affected or -1 in case of error
     * @throws Exception if table is null or empty
     */
    public function delete(string $table, $where, $whereArgs) {
        if ($this->isOpenDB()) {

            if (is_null($table) || empty($table)) {
                $this->exception = ExceptionCodes::NULL_TABLE;
                throw new Exception("table cannot be null or empty", NULL, NULL);
            }
            // Preparamos la cabecera de la sentencia update
            $sql = "delete from " . $table;

            if (!is_null($where) || !empty($where)) {
            // Añadimos la claúsula where al la sentencia sql
                $sql .= " where " . $where;
            }

            // Obtenemos la sentencia preparada como PDOStatement
            $statement = $this->getPreparedStatement($sql);

            if (!is_null($where) || !empty($where)) {
            // Hacmos un bindvalue de los argumentos del where
                $statement = $this->bindValues($statement, $whereArgs);
            }

            // Devolvemos el resultado de ejecutar el método getResult
            return $this->getResult($statement);
        } else {
            $this->exception = ExceptionCodes::DATABASE_NOT_CONNECT;
            return -1;
        }
    }


    //-----------------------------//
    //  PREPARE STATEMENT METHODS  //
    //-----------------------------//
    
    

    /**
     * Bind values in prepared PDOStatement
     * 
     * @param PDOStatement $statement
     * @param array $values (make like this ==>> [ column1 => value1, column2 => value2, ..., columnN => valueN])
     * @return \PDOStatement PDOStatement with values bind to its coluns
     */
    private function bindValues(PDOStatement $statement, $values) {
        if (!is_null($values) || isset($values)) {
            foreach ($values as $key => $value) {
                if($value === 'true' || $value === 'false'){
                    $value = $value === 'true';
                }
                $paramType = PDO::PARAM_STR;
                if (is_null($value) || $value === "null") {
                    $value = null;
                    $paramType = PDO::PARAM_NULL;
                } else if (is_integer($value)) {
                    $paramType = PDO::PARAM_INT;
                } else if (is_real($value)) {
                    $paramType = PDO::PARAM_INT;
                } else if (is_string($value)) {;
                    $paramType = PDO::PARAM_STR;
                } else if (is_bool($value)) {
                    $paramType = PDO::PARAM_BOOL;
                } else {
                    $paramType = PDO::PARAM_LOB;
                }
                $statement->bindValue(":" . $key, $value, $paramType);
            }
        }
        return $statement;
    }

    /**
     * Get a prepare sentence (PDOStatement)
     * @param String $sql sentence sql
     * @return PDOStatement Return prepare sentence
     */
    private function getPreparedStatement($sql) {
        return $this->db->prepare($sql);
    }

    
    //------------------------//
    //  EXTRACT DATA METHODS  //
    //------------------------//
    
    
    /**
     * Get Data from PDOStatement
     * @param PDOStatement $statement prepared statement
     * @return Array with data or null (Array => [row1 => [column1 => value1, 0 => value1, column2 => value2, 1 => value2, ...]])
     */
    private function getData(PDOStatement $statement) {

        // Comprobamos la ejecución de la sentencia
        if ($statement->execute()) {
            // Si es correcto comprobamos el número de filas para actuar en conssecuencia
            if ($statement->rowCount() === 0) {
                $this->responseCode = 0;
                $this->dbMessage = "Empty set";
                return Array();
            } else {
                $this->responseCode = 0;
                $this->dbMessage = $statement->rowCount() . " rows in set";
                return $statement->fetchAll();
            }
        } else {
            $this->getError($statement);
            return null;
        }
    }

    /**
     * Get result for update, insert or delete for get the number of rows affected
     * @param PDOStatement $statement
     * @return Integer number of row affected or -1 in case of error
     */
    private function getResult(PDOStatement $statement) {
        if ($statement->execute()) {
            $this->responseCode = 0;
            if ($statement->rowCount() == 0) {
                $this->dbMessage = "Empty set";
            } else {
                $this->dbMessage = $statement->rowCount() . " rows in set";
            }
            $this->showWarnings();
            
            return $statement->rowCount();
        } else {
            $this->getError($statement);
            return -1;
        }
    }

    /**
     * Get result for update, insert or delete for get the number of rows affected
     * @param PDOStatement $statement
     * @return Integer number of row affected or -1 in case of error
     */
    private function getInsertResult(PDOStatement $statement) {
        if ($statement->execute()) {
            $this->responseCode = 0;
            if ($statement->rowCount() == 0) {
                $this->dbMessage = "Empty set";
            } else {
                $this->dbMessage = $statement->rowCount() . " rows in set";
            }
            $this->showWarnings();
            
            if ($this->returnId == "true") {
                return $this->db->lastInsertId();
            } else {
                return $statement->rowCount();
            }
        } else {
            $this->getError($statement);
            return -1;
        }
    }

    /**
     * Get result for executing stored procedures or functions like count, average, max...
     * 
     * @param PDOStatement $statement prepared PDOStatement
     * @return data returned by executed statement or null in case of error
     */
    private function getExecuteResult(PDOStatement $statement) {
        if ($statement->execute()) {
            $this->responseCode = 0;
            if ($statement->rowCount() == 0) {
                $this->dbMessage = "Empty set";
            } else {
                $this->dbMessage = $statement->rowCount() . " rows in set";
            }
            $this->showWarnings();
            return $statement->fetchAll();
        } else {
            $this->getError($statement);
            return null;
        }
    }

    
    //-------------------------//
    //  HANDLER ERROR METHODS  //
    //-------------------------//
    
    /**
     * Execute show warnings statement
     * @throws Exception
     */
    private function showWarnings(){
        $sql = "show warnings";
        $statement = $this->getPreparedStatement($sql);
        $statement->execute();
        if($statement->rowCount() > 0){
            $result = $statement->fetchAll();
            $this->getWarning($result);
            throw new Exception();
        }        
    }
    
    /**
     * Get warning type
     * @param type $warning
     */
    private function getWarning($warning){
        $this->responseCode = $warning[0]["Code"];
        $this->dbMessage = $warning[0]["Message"];
        switch ($this->responseCode){
            case MySQLCodes::INVALID_VALUE:
                $this->exception = ExceptionCodes::INVALID_VALUE;
                break;
            default :
                $this->exception = ExceptionCodes::UNKNOWN_ERROR;
                break;
        }      
    }
    
    /**
     * Get error code from mysql database
     * @param PDOStatement $statement executed PDOStatement instance
     */
    private function getError(PDOStatement $statement) {
        $error = $statement->errorInfo();
        $errorInfo = $error[2];
        $this->dbMessage = $errorInfo;
        $this->responseCode = $error[1];
        switch ($this->responseCode) {
            case MySQLCodes::UNKNOWN_COLUM:                
                $this->exception = ExceptionCodes::UNKNOWN_COLUM;
                break;
            case MySQLCodes::DUPLICATE_ENTRY:
                $this->exception = ExceptionCodes::DUPLICATE_ENTRY;
                break;
            case MySQLCodes::SYNTAX_ERROR:
                $this->exception = ExceptionCodes::SYNTAX_ERROR;
                break;
            case MySQLCodes::FOREIGN_KEY_CONSTRAINT_FAILS:
                $this->exception = ExceptionCodes::FOREIGN_KEY_CONSTRAINT_FAILS;
                break;
            case MySQLCodes::COLUMN_NULL:
                $this->exception = ExceptionCodes::NULL_COLUMN;
                break;
            default :
                $this->exception = ExceptionCodes::UNKNOWN_ERROR;
                break;
        }
    }

    /**
     * Get the key that throw the exception
     * @param String $errorInfo Error message that contains key name (SQL CONSTRAINT)
     * @return String Return key name
     */
    private function extractKey($errorInfo) {
        $splits = split("'", $errorInfo);
        return $splits[3];
    }

}

/**
 * Class that encapsulates all the data needed to be converted into JSON and send operations to the database
 * @author Juan José Longoria López
 * @version 1.0.0
 */
class MySQLOperation {

    //------------------------------------//
    //  CONSTANTS FOR IDENTIFY OPERATION  //
    //------------------------------------//
    
    const SELECT = 0;
    const INSERT = 1;
    const UPDATE = 2;
    const DELETE = 3;
    const STORED_PROCEDURE = 4;
    const STORED_FUNCTION = 5;
    const RAW_QUERY = 6;

    
    //----------//
    //  FIELDS  //
    //----------//
    
    private $operation;
    private $table;
    private $projection;
    private $where;
    private $whereArgs;
    private $groupBy;
    private $having;
    private $orderBy;
    private $limit;
    private $values;
    private $statement;
    private $bindValues;
    private $returnId;

    
    //----------------//
    //  CONSTRUCTORS  //
    //----------------//
    
    public static function createOperation($operationArray) {
        $op = $operationArray['operation'];
        $operation = new MySQLOperation($op);
        $operation->setTable($operationArray['table']);
        if (isset($operationArray['projection'])) {
            $operation->setProjection($operationArray['projection']);
        }
        if (isset($operationArray['where'])) {
            $operation->setWhere($operationArray['where']);
        }
        if (isset($operationArray['whereArgs'])) {
            $operation->setWhereArgs($operationArray['whereArgs']);
        }
        if (isset($operationArray['groupBy'])) {
            $operation->setGroupBy($operationArray['groupBy']);
        }
        if (isset($operationArray['having'])) {
            $operation->setHaving($operationArray['having']);
        }
        if (isset($operationArray['orderBy'])) {
            $operation->setOrderBy($operationArray['orderBy']);
        }
        if (isset($operationArray['limit'])) {
            $operation->setLimit($operationArray['limit']);
        }
        if (isset($operationArray['values'])) {
            $operation->setValues($operationArray['values']);
        }
        if (isset($operationArray['statement'])) {
            $operation->setStatement($operationArray['statement']);
        }
        if (isset($operationArray['bindValues'])) {
            $operation->setBindValues($operationArray['bindValues']);
        }
        if (isset($operationArray['returnId'])) {
            $operation->setReturnId($operationArray['returnId']);
        }
        return $operation;
    }

    public function __construct($operation) {
        $this->operation = $operation;
    }
    
    
    //-----------------------//
    //  GETTERS AND SETTERS  //
    //-----------------------//
    

    /**
     * Getter for operation code
     * @return int Operation code
     */
    public function getOperation() {
        return $this->operation;
    }

    /**
     * Setter for operation code (Constant fields of this class)
     * @param int $operation
     */
    public function setOperation($operation) {
        $this->operation = $operation;
    }

    /**
     * Getter for table field
     * @return string table name
     */
    public function getTable() {
        return $this->table;
    }

    /**
     * Setter for table name (if need join, you can do this like ==>> table1 join table2 on table1_pk = table2_fk join...)
     * @param string $table
     */
    public function setTable($table) {
        $this->table = $table;
    }

    /**
     * Getter for projection fields
     * @return Array projection fields
     */
    public function getProjection() {
        return $this->projection;
    }

    /**
     * Setter for projection fields (make like this ==>> [column1, column2, column3,..., columnN])
     * @param string $projection
     */
    public function setProjection($projection) {
        $this->projection = $projection;
    }

    /**
     * Getter for where clause
     * @return string where clause
     */
    public function getWhere() {
        return $this->where;
    }

    /**
     * Setter for where clause (make like this ==>> column1 = :column1 and column2 = :column2...)
     * @param string $where
     */
    public function setWhere($where) {
        $this->where = $where;
    }

    /**
     * Getter for where args
     * @return Array where args
     */
    public function getWhereArgs() {
        return $this->whereArgs;
    }

    /**
     * Setter for where args (make like this ==>> [ column1 => value1, column2 => value2, ..., columnN => valueN])
     * @param array $whereArags
     */
    public function setWhereArgs($whereArags) {
        $this->whereArgs = $whereArags;
    }

    /**
     * Getter for group by clause
     * @return string group by
     */
    public function getGroupBy() {
        return $this->groupBy;
    }

    /**
     * Setter for group by clause
     * @param string $groupBy
     * @throws Exception if operation isn`t select
     */
    public function setGroupBy($groupBy) {
        $this->groupBy = $groupBy;
    }

    /**
     * Getter for having clause
     * @return string having clause
     */
    public function getHaving() {
        return $this->having;
    }

    /**
     * Setter for having clause
     * @param string $having
     * @throws Exception if operation isn`t select
     */
    public function setHaving($having) {
        $this->having = $having;
    }

    /**
     * Getter for order by
     * @return string order by
     */
    public function getOrderBy() {
        return $this->orderBy;
    }

    /**
     * Setter for order by
     * @param string $orderBy
     * @throws Exception if operation isn`t select
     */
    public function setOrderBy($orderBy) {
        $this->orderBy = $orderBy;
    }

    /**
     * Getter for limit by clause
     * @return string limit
     */
    public function getLimit() {
        return $this->limit;
    }

    /**
     * Setter for limit by clause
     * @param string $limit
     * @throws Exception if operation isn`t select
     */
    public function setLimit($limit) {
        $this->limit = $limit;
    }

    /**
     * Getter for values for insert or update operation 
     * @return Array values for insert, or update
     */
    public function getValues() {
        return $this->values;
    }

    /**
     * Setter for values for insert or update operation (make like this ==>> [ column1 => value1, column2 => value2, ..., columnN => valueN])
     * @param Array $values
     * @throws Exception if operation aren't insert or update
     */
    public function setValues($values) {
        $this->values = $values;
    }

    /**
     * Getter for sql statement for raw query or stored functions or procedure
     * @return string sql statement
     */
    public function getStatement() {
        return $this->statement;
    }

    /**
     * Setter for sql statement for raw query or stored functions or procedure
     * @param string $statement sql statement
     * @throws Exception if operation aren't stored function, stored procedure or raw query
     */
    public function setStatement($statement) {
        $this->statement = $statement;
    }

    /**
     * Getter for values to bind in query
     * @return Array values to bind in query
     */
    public function getBindValues() {
        return $this->bindValues;
    }

    /**
     * Setter for values to bind in query
     * @param Array $bindValues values to bind in query
     * @throws Exception if operation aren't stored function, stored procedure or raw query
     */
    public function setBindValues($bindValues) {
        $this->bindValues = $bindValues;
    }

    /**
     * Getter for return last id
     * @return Array values to bind in query
     */
    public function getReturnId() {
        return $this->returnId;
    }

    /**
     * Setter for return last id
     * @param Array $bindValues values to bind in query
     * @throws Exception if operation aren't stored function, stored procedure or raw query
     */
    public function setReturnId($returnId) {
        $this->returnId = $returnId;
    }

}

/**
 * Class tant encapsulates database response for send to Java
 * @author Juan José Longoria López
 * @version 1.0.0
 */
class Response {
    
    //----------//
    //  FIELDS  //
    //----------//

    public $code;
    public $message;
    public $data;
    public $exception;

    public static function makeCustomResponse($code, $message, $data, $exception) {
        $response = new Response();
        $response->code = $code;
        $response->message = $message;
        $response->data = $data;
        $response->exception = $exception;
        return $response;
    }

}

/**
 * Class with Exception Codes
 * @author Juan José Longoria López
 * @version 1.0.0
 */
class ExceptionCodes{
    
    const UNKNOWN_ERROR = -1;
    const DATABASE_NOT_CONNECT = 1;
    const DUPLICATE_ENTRY = 2;
    const NULL_TABLE = 3;
    const NULL_VALUES = 4;
    const UNKNOWN_COLUM = 5;
    const FOREIGN_KEY_CONSTRAINT_FAILS = 6;
    const SYNTAX_ERROR = 7;
    const NULL_COLUMN = 8;
    const INVALID_VALUE = 9;
    
    
}

/**
 * Class with mysql error codes
 * @author Juan José Longoria López
 * @version 1.0.0
 */
class MySQLCodes {

    const UNKNOWN_COLUM = 1054;
    const DUPLICATE_ENTRY = 1062;
    const SYNTAX_ERROR = 1064;
    const FOREIGN_KEY_CONSTRAINT_FAILS = 1452;
    const COLUMN_NULL = 1048;
    const INVALID_VALUE = 1366;
}
