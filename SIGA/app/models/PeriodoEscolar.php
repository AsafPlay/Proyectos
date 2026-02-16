<?php
require_once __DIR__ . '/../config/database.php';

class PeriodoEscolar {

    public static function obtenerPeriodoActivo() {
        $db = Database::connect();
        $sql = "SELECT * FROM periodo_escolar LIMIT 1";
        return $db->query($sql)->fetch_assoc();
    }

    public static function cambiarEstado($estado) {
        $db = Database::connect();
        $sql = "UPDATE periodo_escolar SET activo = ?";
        $stmt = $db->prepare($sql);
        $stmt->bind_param("i", $estado);
        return $stmt->execute();
    }
}
