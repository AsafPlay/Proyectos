<?php
session_start();
require_once "../models/Inscripcion.php";
require_once "../models/Estudiante.php";

if ($_SESSION['rol'] !== 'estudiante') {
    die("Acceso no autorizado");
}

// Obtener estudiante ligado al usuario
$db = Database::connect();
$res = $db->query(
  "SELECT id FROM estudiantes WHERE usuario_id = ".$_SESSION['id_usuario']
);
$estudiante = $res->fetch_assoc();

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $ok = Inscripcion::inscribir(
    $estudiante['id'],
    $_POST['grupo_id']
);

header("Location: ../views/estudiante/inscripcion.php?ok=$ok");
}
