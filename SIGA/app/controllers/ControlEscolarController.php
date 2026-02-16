<?php
require_once __DIR__ . '/../models/PeriodoEscolar.php';

class ControlEscolarController {

    public static function index() {
        session_start();

        if (!isset($_SESSION['usuario']) || $_SESSION['rol'] !== 'admin') {
            header("Location: /SIGA/public/index.php");
            exit;
        }

        $periodo = PeriodoEscolar::obtenerPeriodoActivo();
        require_once __DIR__ . '/../views/control_escolar/index.php';
    }

    public static function cambiarEstado() {
        session_start();

        if ($_SESSION['rol'] !== 'admin') {
            header("Location: /SIGA/public/index.php");
            exit;
        }

        $nuevoEstado = $_POST['estado'];
        PeriodoEscolar::cambiarEstado($nuevoEstado);

        header("Location: /SIGA/public/index.php?controller=controlEscolar");
    }
}
