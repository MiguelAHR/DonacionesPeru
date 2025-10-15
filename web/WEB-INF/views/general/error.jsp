<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error</title>
</head>
<body>
    <h1>Error</h1>
    <p>Ha ocurrido un error en la aplicación.</p>
    <p>Código de error: ${pageContext.errorData.statusCode}</p>
    <p><a href="login">Volver al login</a></p>
</body>
</html>