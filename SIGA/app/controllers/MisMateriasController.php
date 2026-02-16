<?php
session_start();
require_once "../models/Inscripcion.php";
require_once "../config/database.php";

if ($_SESSION['rol'] !== 'estudiante') {
    die("Acceso no autorizado");
}

// Obtener id del estudiante desde el usuario logueado
$db = Database::connect();
$res = $db->query(
  "SELECT id FROM estudiantes WHERE usuario_id = ".$_SESSION['id_usuario']
);
$estudiante = $res->fetch_assoc();

$materias = Inscripcion::misMaterias($estudiante['id']);
