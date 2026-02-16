<?php
require_once __DIR__ . "/../config/database.php";

class Estudiante {

    public static function getAll() {
        $db = Database::connect();
        $sql = "SELECT * FROM estudiantes";
        return $db->query($sql);
    }

    public static function create($usuario_id, $boleta, $carrera, $semestre) {
        $db = Database::connect();
        $sql = "INSERT INTO estudiantes (usuario_id, boleta, carrera, semestre)
                VALUES (?, ?, ?, ?)";
        $stmt = $db->prepare($sql);
        $stmt->bind_param("issi", $usuario_id, $boleta, $carrera, $semestre);
        return $stmt->execute();
    }
}
