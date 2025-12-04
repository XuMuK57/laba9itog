<?php
require "DataBaseConfig.php";

class DataBase
{
    /** @var PDO|null */
    private $pdo = null;

    protected $host;
    protected $port;
    protected $username;
    protected $password;
    protected $databasename;

    public function __construct()
    {
        $cfg = new DataBaseConfig();
        $this->host = $cfg->host;
        $this->port = $cfg->port;
        $this->username = $cfg->username;
        $this->password = $cfg->password;
        $this->databasename = $cfg->databasename;
    }

    public function dbConnect()
    {
        try {
            $dsn = "pgsql:host={$this->host};port={$this->port};dbname={$this->databasename}";
            $this->pdo = new PDO($dsn, $this->username, $this->password, [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES => false,
            ]);
            return true;
        } catch (Throwable $e) {
            return false;
        }
    }

    public function pdo()
    {
        return $this->pdo;
    }

    private function onlyUsersTable($table)
    {
        return ($table === "users") ? "users" : null;
    }

    public function logIn($table, $username, $password)
    {
        if ($this->pdo === null) return false;

        $t = $this->onlyUsersTable($table);
        if ($t === null) return false;

        $username = trim($username);
        $password = trim($password);

        $stmt = $this->pdo->prepare("SELECT password FROM {$t} WHERE username = :u LIMIT 1");
        $stmt->execute([":u" => $username]);
        $row = $stmt->fetch();

        if (!$row) return false;

        return password_verify($password, $row["password"]);
    }

    public function signUp($table, $username, $fullname, $email, $password)
    {
        if ($this->pdo === null) return false;

        $t = $this->onlyUsersTable($table);
        if ($t === null) return false;

        $username = trim($username);
        $fullname = trim($fullname);
        $email = trim($email);
        $password = trim($password);

        if ($username === "" || $fullname === "" || $email === "" || $password === "") {
            return false;
        }

        $hash = password_hash($password, PASSWORD_DEFAULT);

        try {
            $stmt = $this->pdo->prepare(
                "INSERT INTO {$t} (username, fullname, email, password)
                 VALUES (:u, :f, :e, :p)"
            );
            $stmt->execute([
                ":u" => $username,
                ":f" => $fullname,
                ":e" => $email,
                ":p" => $hash,
            ]);
            return true;
        } catch (Throwable $e) {
            return false;
        }
    }

    public function findUserIdByUsername($username)
    {
        $stmt = $this->pdo->prepare("SELECT id FROM users WHERE username = :u LIMIT 1");
        $stmt->execute([":u" => $username]);
        $row = $stmt->fetch();
        return $row ? intval($row["id"]) : null;
    }
}
?>
