<?php
session_start();
require_once "../models/Usuario.php";

$correo = $_POST['correo'];
$password = $_POST['password'];

$user = Usuario::login($correo);

if ($user && password_verify($password, $user['password'])) {
    $_SESSION['id_usuario'] = $user['id'];
    $_SESSION['rol'] = $user['rol'];
    $_SESSION['nombre'] = $user['nombre'];

    header("Location: ../views/dashboard.php");
} else {
    header("Location: ../views/auth/login.php?error=1");
}
