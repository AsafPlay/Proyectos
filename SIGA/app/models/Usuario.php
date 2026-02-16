<?php
require_once __DIR__ . "/../config/database.php";

class Usuario {
    public static function login($correo) {
        $db = Database::connect();
        $sql = "SELECT * FROM usuarios WHERE correo = ?";
        $stmt = $db->prepare($sql);
        $stmt->bind_param("s", $correo);
        $stmt->execute();
        return $stmt->get_result()->fetch_assoc();
    }
}
