<!DOCTYPE html>
<html>
<head>
  <title>SIGA - Login</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
</head>
<body class="container mt-5">

<h3>Inicio de sesión</h3>

<form method="POST" action="../../controllers/AuthController.php">
  <div class="mb-3">
    <input type="email" name="correo" class="form-control" placeholder="Correo institucional" required>
  </div>
  <div class="mb-3">
    <input type="password" name="password" class="form-control" placeholder="Contraseña" required>
  </div>
  <button class="btn btn-primary">Ingresar</button>
</form>

</body>
</html>
