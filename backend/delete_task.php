<?php
require_once "DataBase.php";
require_once "_util.php";

requirePost(["task_id"]);

$db = new DataBase();
if (!$db->dbConnect()) jsonOut(false, "DB error");

$id = intval($_POST["task_id"]);

$stmt = $db->pdo()->prepare("DELETE FROM tasks WHERE id = :id");
$stmt->execute([":id" => $id]);

jsonOut(true, "Deleted");
