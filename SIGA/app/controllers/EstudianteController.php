<?php
session_start();
require_once "../models/Estudiante.php";

if ($_SESSION['rol'] != 'control') {
    die("Acceso no autorizado");
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    Estudiante::create(
        $_SESSION['id_usuario'],
        $_POST['boleta'],
        $_POST['carrera'],
        $_POST['semestre']
    );
    header("Location: ../views/estudiante/index.php");
}
