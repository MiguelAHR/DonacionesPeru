<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nueva Solicitud - Donaciones Perú</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .page-header {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 40px 0;
        }
    </style>
</head>
<body class="bg-light">
    <!-- Header -->
    <div class="page-header">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h1 class="fw-bold mb-2">
                        <i class="fas fa-hands-helping me-2"></i>Nueva Solicitud de Ayuda
                    </h1>
                    <p class="lead mb-0">Describe tu situación y necesidades</p>
                </div>
            </div>
        </div>
    </div>

    <div class="container my-4">
        <div class="card border-0 shadow-sm">
            <div class="card-body p-4">
                <form action="${pageContext.request.contextPath}/donations" method="post">
                    <input type="hidden" name="action" value="newRequest">
                    
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Tipo de ayuda necesitada:</label>
                                <select name="requestType" class="form-select" required>
                                    <option value="">Seleccione tipo</option>
                                    <option value="ropa">Ropa</option>
                                    <option value="utiles_escolares">Útiles Escolares</option>
                                    <option value="cuadernos">Cuadernos</option>
                                    <option value="alimentos">Alimentos</option>
                                    <option value="medicamentos">Medicamentos</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Nivel de urgencia:</label>
                                <select name="priority" class="form-select">
                                    <option value="3">Media</option>
                                    <option value="4">Alta</option>
                                    <option value="5">Urgente</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold">Descripción detallada:</label>
                        <textarea name="requestDescription" class="form-control" rows="4" 
                                placeholder="Describa su situación y necesidades específicas..." required></textarea>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label fw-bold">Ubicación donde necesita la ayuda:</label>
                        <input type="text" name="requestLocation" class="form-control" 
                               placeholder="Ej: Lima, Arequipa, etc." required>
                    </div>
                    
                    <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-secondary me-md-2">
                            <i class="fas fa-times me-2"></i>Cancelar
                        </a>
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-paper-plane me-2"></i>Enviar Solicitud
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>