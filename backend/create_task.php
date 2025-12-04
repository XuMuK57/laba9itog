<?php
require_once "DataBase.php";
require_once "_util.php";

requirePost(["username","title","description","deadline_at"]);

$username = trim($_POST["username"]);
$title = trim($_POST["title"]);
$desc = trim($_POST["description"]);
$deadline = trim($_POST["deadline_at"]);

$db = new DataBase();
if (!$db->dbConnect()) jsonOut(false, "DB error");

$uid = $db->findUserIdByUsername($username);
if ($uid === null) jsonOut(false, "User not found");

$stmt = $db->pdo()->prepare("
    INSERT INTO tasks(user_id,title,description,deadline_at,status)
    VALUES(:u,:t,:d,:dl,'pending') RETURNING id
");
$stmt->execute([
    ":u" => $uid,
    ":t" => $title,
    ":d" => $desc,
    ":dl" => $deadline
]);

$id = $stmt->fetchColumn();

jsonOut(true, "Created", ["task_id" => $id]);
