<?php
require_once "../../controllers/MisMateriasController.php";
?>

<h3>Mis materias inscritas</h3>

<?php if ($materias->num_rows == 0): ?>
  <p>No tienes materias inscritas.</p>
<?php else: ?>
<table border="1">
  <tr>
    <th>Materia</th>
    <th>Créditos</th>
    <th>Grupo</th>
    <th>Fecha</th>
  </tr>

  <?php while($m = $materias->fetch_assoc()): ?>
  <tr>
    <td><?= $m['materia'] ?></td>
    <td><?= $m['creditos'] ?></td>
    <td><?= $m['grupo'] ?></td>
    <td><?= $m['fecha'] ?></td>
  </tr>
  <?php endwhile; ?>
</table>
<?php endif; ?>
