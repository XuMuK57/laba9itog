<?php
require_once "DataBase.php";
require_once "_util.php";

requirePost(["username"]);
$username = trim($_POST["username"]);

$db = new DataBase();
if (!$db->dbConnect()) jsonOut(false, "Database connection failed");

$uid = $db->findUserIdByUsername($username);
if ($uid === null) jsonOut(false, "User not found");

$stmt = $db->pdo()->prepare("
    SELECT id, title, description, deadline_at, status
    FROM tasks
    WHERE user_id = :uid
    ORDER BY
        CASE WHEN deadline_at IS NULL THEN 1 ELSE 0 END,
        deadline_at ASC,
        id DESC
");
$stmt->execute([":uid" => $uid]);

$tasks = $stmt->fetchAll();

jsonOut(true, "OK", ["tasks" => $tasks]);
