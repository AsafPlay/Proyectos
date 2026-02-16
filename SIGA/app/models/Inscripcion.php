<?php
require_once __DIR__ . "/../config/database.php";

class Inscripcion {

    public static function obtenerGrupos() {
        $db = Database::connect();
        $sql = "
          SELECT g.id, m.nombre, g.cupo
          FROM grupos g
          JOIN materias m ON g.materia_id = m.id
        ";
        return $db->query($sql);
    }

    public static function inscribir($estudiante_id, $grupo_id) {
        $db = Database::connect();

        // Verificar cupo
        $check = $db->prepare("SELECT cupo FROM grupos WHERE id = ?");
        $check->bind_param("i", $grupo_id);
        $check->execute();
        $cupo = $check->get_result()->fetch_assoc()['cupo'];

        if ($cupo <= 0) return false;

        // Registrar inscripción
        $stmt = $db->prepare(
          "INSERT INTO inscripciones (estudiante_id, grupo_id)
           VALUES (?, ?)"
        );
        $stmt->bind_param("ii", $estudiante_id, $grupo_id);
        $stmt->execute();

        // Reducir cupo
        $db->query("UPDATE grupos SET cupo = cupo - 1 WHERE id = $grupo_id");

        return true;
    }

    public static function misMaterias($estudiante_id) {
        $db = Database::connect();
        $sql = "
        SELECT 
            m.nombre AS materia,
            m.creditos,
            g.id AS grupo,
            i.fecha
        FROM inscripciones i
        JOIN grupos g ON i.grupo_id = g.id
        JOIN materias m ON g.materia_id = m.id
        WHERE i.estudiante_id = ?
        ";
        $stmt = $db->prepare($sql);
        $stmt->bind_param("i", $estudiante_id);
        $stmt->execute();
        return $stmt->get_result();
    }

    public static function yaInscrito($estudiante_id, $grupo_id) {
        $db = Database::connect();
        $sql = "SELECT id FROM inscripciones 
                WHERE estudiante_id = ? AND grupo_id = ?";
        $stmt = $db->prepare($sql);
        $stmt->bind_param("ii", $estudiante_id, $grupo_id);
        $stmt->execute();
        return $stmt->get_result()->num_rows > 0;
    }

    public static function inscribir($estudiante_id, $grupo_id) {
        $db = Database::connect();

        // 1. Verificar inscripción duplicada
        if (self::yaInscrito($estudiante_id, $grupo_id)) {
            return "duplicado";
        }

        // 2. Verificar cupo
        $check = $db->prepare("SELECT cupo FROM grupos WHERE id = ?");
        $check->bind_param("i", $grupo_id);
        $check->execute();
        $cupo = $check->get_result()->fetch_assoc()['cupo'];

        if ($cupo <= 0) {
            return "sin_cupo";
        }

        // 3. Registrar inscripción
        $stmt = $db->prepare(
        "INSERT INTO inscripciones (estudiante_id, grupo_id)
        VALUES (?, ?)"
        );
        $stmt->bind_param("ii", $estudiante_id, $grupo_id);
        $stmt->execute();

        // 4. Reducir cupo
        $db->query("UPDATE grupos SET cupo = cupo - 1 WHERE id = $grupo_id");

        return "ok";
    }

    }
