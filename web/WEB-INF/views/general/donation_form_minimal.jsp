<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nueva Donación - Sistema Simple</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h4 class="mb-0">Nueva Donación</h4>
                    </div>
                    <div class="card-body">
                        <form action="donations" method="post">
                            <input type="hidden" name="action" value="create">
                            
                            <div class="mb-3">
                                <label class="form-label">Tipo de donación:</label>
                                <select name="donationType" class="form-select" required>
                                    <option value="">Seleccionar...</option>
                                    <option value="ropa">Ropa</option>
                                    <option value="cuadernos">Cuadernos</option>
                                    <option value="utiles_escolares">Útiles Escolares</option>
                                    <option value="material_reciclable">Material Reciclable</option>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Descripción:</label>
                                <textarea name="description" class="form-control" rows="3" required 
                                          placeholder="Describa los artículos a donar"></textarea>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Cantidad:</label>
                                <input type="number" name="quantity" class="form-control" min="1" required>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Condición:</label>
                                <select name="condition" class="form-select" required>
                                    <option value="">Seleccionar...</option>
                                    <option value="nuevo">Nuevo</option>
                                    <option value="como_nuevo">Como Nuevo</option>
                                    <option value="buen_estado">Buen Estado</option>
                                    <option value="usado">Usado</option>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Ubicación:</label>
                                <input type="text" name="location" class="form-control" required 
                                       placeholder="Ej: Lima, Arequipa, etc.">
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Dirección (opcional):</label>
                                <textarea name="address" class="form-control" rows="2" 
                                          placeholder="Dirección específica para recojo"></textarea>
                            </div>
                            
                            <div class="d-grid">
                                <button type="submit" class="btn btn-success">
                                    <i class="fas fa-paper-plane me-2"></i>Enviar Donación
                                </button>
                            </div>
                        </form>
                        
                        <div class="mt-3">
                            <a href="dashboard" class="btn btn-secondary">
                                <i class="fas fa-arrow-left me-2"></i>Volver al Dashboard
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>