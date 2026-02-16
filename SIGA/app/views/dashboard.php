<?php
session_start();
if (!isset($_SESSION['rol'])) {
  header("Location: auth/login.php");
}
?>

<h2>Bienvenido <?php echo $_SESSION['nombre']; ?></h2>

<?php if ($_SESSION['rol'] == 'estudiante'): ?>
  <a href="estudiante/index.php">Inscripción</a>
<?php endif; ?>

<?php if ($_SESSION['rol'] == 'control'): ?>
  <a href="#">Gestión académica</a>
<?php endif; ?>

<?php if ($_SESSION['rol'] == 'control'): ?>
  <a href="estudiante/index.php">Gestión de estudiantes</a>
<?php endif; ?>

<?php if ($_SESSION['rol'] == 'estudiante'): ?>
  <a href="estudiante/inscripcion.php">Inscripción de materias</a>
<?php endif; ?>

<?php if ($_SESSION['rol'] == 'estudiante'): ?>
  <br>
  <a href="estudiante/mis_materias.php">Mis materias inscritas</a>
<?php endif; ?>
