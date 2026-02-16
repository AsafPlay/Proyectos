<?php
session_start();

if (!isset($_SESSION['rol'])) {
    header("Location: app/views/auth/login.php");
    exit;
} else {
    header("Location: app/views/dashboard.php");
    exit;
}

if (isset($_GET['controller']) && $_GET['controller'] === 'controlEscolar') {
    require_once __DIR__ . '/../app/controllers/ControlEscolarController.php';
    require_once __DIR__ . '/../app/controllers/AuthController.php';
    require_once __DIR__ . '/../app/controllers/EstudianteController.php';
    require_once __DIR__ . '/../app/controllers/ControlEscolarController.php';

   if (isset($_GET['controller']) && $_GET['controller'] === 'controlEscolar') {

    if (isset($_GET['action']) && $_GET['action'] === 'cambiarEstado') {
        ControlEscolarController::cambiarEstado();
    } else {
        ControlEscolarController::index();
    }

    exit;
    }

}
