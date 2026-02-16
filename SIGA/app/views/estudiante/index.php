<?php
session_start();
require_once "../../models/Estudiante.php";

$estudiantes = Estudiante::getAll();
?>

<p>
  Registrado por: 
  <b><?php echo $_SESSION['nombre']; ?></b>
</p>

<h3>Listado de Estudiantes</h3>

<table border="1">
<tr>
  <th>Boleta</th>
  <th>Carrera</th>
  <th>Semestre</th>
</tr>

<?php while($e = $estudiantes->fetch_assoc()): ?>
<tr>
  <td><?= $e['boleta'] ?></td>
  <td><?= $e['carrera'] ?></td>
  <td><?= $e['semestre'] ?></td>
</tr>
<?php endwhile; ?>
</table>

<h4>Registrar nuevo estudiante</h4>

<form method="POST" action="../../controllers/EstudianteController.php">
  <input type="text" name="boleta" placeholder="Boleta" required>
  <input type="text" name="carrera" placeholder="Carrera" required>
  <input type="number" name="semestre" placeholder="Semestre" required>
  <button>Guardar</button>
</form>
