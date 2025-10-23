<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Acceso Denegado</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: linear-gradient(135deg, #f44336, #c62828);
            color: white;
            text-align: center;
            margin: 0;
            height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }
        .alert-box {
            background-color: #fff;
            color: #333;
            border-radius: 12px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.4);
            padding: 30px 50px;
            max-width: 400px;
            text-align: center;
            animation: slideDown 0.6s ease;
        }
        @keyframes slideDown {
            from { transform: translateY(-50px); opacity: 0; }
            to { transform: translateY(0); opacity: 1; }
        }
        .alert-box h1 {
            color: #e53935;
            font-size: 28px;
            margin-bottom: 10px;
        }
        .alert-box p {
            margin: 10px 0;
            font-size: 16px;
        }
        button {
            background-color: #e53935;
            color: white;
            border: none;
            padding: 10px 25px;
            border-radius: 6px;
            cursor: pointer;
            margin-top: 15px;
            font-weight: bold;
        }
        button:hover {
            background-color: #c62828;
        }
    </style>
</head>
<body>
    <div class="alert-box">
        <h1>🚫 Acceso Denegado</h1>
        <p>No tienes permisos para acceder a esta sección.</p>
        <button onclick="window.location.href='<%= request.getContextPath() %>/dashboard'">Volver al inicio</button>
    </div>
</body>
</html>