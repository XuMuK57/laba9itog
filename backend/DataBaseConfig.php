<?php

class DataBaseConfig
{
    public $host;
    public $port;
    public $username;
    public $password;
    public $databasename;

    public function __construct()
    {
        $this->host = 'localhost';
        $this->port = 5432;
        $this->username = 'postgres';
        $this->password = '1111';

        $this->databasename = 'loginbase';
    }
}
?>
