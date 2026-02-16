<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Control Escolar</title>
    <link rel="stylesheet" href="/SIGA/public/css/styles.css">
</head>
<body>

<div class="container">

    <h2>Panel de Control Escolar</h2>

    <p><strong>Periodo actual:</strong> <?= $periodo['nombre'] ?></p>

    <p>
        <strong>Estado:</strong>
        <?php if ($periodo['activo']): ?>
            <span class="estado-abierto">ABIERTO</span>
        <?php else: ?>
            <span class="estado-cerrado">CERRADO</span>
        <?php endif; ?>
    </p>

    <form method="POST" action="/SIGA/public/index.php?controller=controlEscolar&action=cambiarEstado">
        <?php if ($periodo['activo']): ?>
            <input type="hidden" name="estado" value="0">
            <button type="submit">Cerrar periodo</button>
        <?php else: ?>
            <input type="hidden" name="estado" value="1">
            <button type="submit">Abrir periodo</button>
        <?php endif; ?>
    </form>

</div>

</body>
</html>
