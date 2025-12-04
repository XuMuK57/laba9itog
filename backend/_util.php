<?php
function jsonOut($success, $message, $data = []) {
    header("Content-Type: application/json; charset=utf-8");

    echo json_encode([
        "success" => $success,
        "message" => $message,
        "data" => $data
    ], JSON_UNESCAPED_UNICODE);

    exit;
}

function requirePost($fields) {
    foreach ($fields as $f) {
        if (!isset($_POST[$f])) {
            jsonOut(false, "Missing field: $f");
        }
    }
}

function normDeadline($d) {
    $x = trim($d);
    if ($x === "" || $x === "null") return null;
    return $x; 
}

function normStatus($s) {
    $x = trim($s);
    if ($x === "") return "pending";
    return $x;
}
?>
