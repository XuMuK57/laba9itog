<?php
require_once "DataBase.php";
require_once "_util.php";

requirePost(["username","task_id","title","description","deadline_at","status"]);

$db = new DataBase();
if (!$db->dbConnect()) jsonOut(false, "DB error");

$username = $_POST["username"];
$uid = $db->findUserIdByUsername($username);

if ($uid === null) jsonOut(false, "User not found");

$tid = intval($_POST["task_id"]);

$stmt = $db->pdo()->prepare("
    UPDATE tasks
    SET title=:t, description=:d, deadline_at=:dl, status=:s, updated_at=NOW()
    WHERE id=:id AND user_id=:uid
");

$stmt->execute([
    ":t" => $_POST["title"],
    ":d" => $_POST["description"],
    ":dl" => $_POST["deadline_at"],
    ":s" => $_POST["status"],
    ":id" => $tid,
    ":uid" => $uid
]);

jsonOut(true, "Updated");
