<?php
session_start();
require_once "../../models/Inscripcion.php";

$grupos = Inscripcion::obtenerGrupos();
?>

<h3>Inscripción de materias</h3>

<?php if (isset($_GET['ok'])): ?>
  <p>
    <?php
      if ($_GET['ok'] === 'ok') echo "Inscripción exitosa";
      elseif ($_GET['ok'] === 'duplicado') echo "Ya estás inscrito en este grupo";
      elseif ($_GET['ok'] === 'sin_cupo') echo "No hay cupo disponible";
    ?>
  </p>
<?php endif; ?>

<form method="POST" action="../../controllers/InscripcionController.php">
  <select name="grupo_id" required>
    <?php while($g = $grupos->fetch_assoc()): ?>
      <option value="<?= $g['id'] ?>">
        <?= $g['nombre'] ?> (Cupo: <?= $g['cupo'] ?>)
      </option>
    <?php endwhile; ?>
  </select>
  <button>Inscribirse</button>
</form>
