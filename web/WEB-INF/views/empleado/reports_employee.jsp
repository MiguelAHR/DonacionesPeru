<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Mis Reportes - Empleado</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            .stat-card {
                border-radius: 12px;
                transition: all 0.3s ease;
                box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                border: none;
            }
            .stat-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 8px 15px rgba(0,0,0,0.15);
            }
            .export-card {
                background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                border-left: 4px solid #28a745 !important;
            }
            .export-btn-intelligent {
                background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
                color: white;
                border: none;
                border-radius: 8px;
                padding: 14px 24px;
                font-weight: 500;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 10px;
                min-width: 180px;
                position: relative;
                overflow: hidden;
            }
            .export-btn-pdf {
                background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
                color: white;
                border: none;
                border-radius: 8px;
                padding: 14px 24px;
                font-weight: 500;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 10px;
                min-width: 180px;
                position: relative;
                overflow: hidden;
            }
            .export-btn-intelligent:hover, .export-btn-pdf:hover {
                transform: translateY(-3px);
                box-shadow: 0 6px 12px rgba(0,0,0,0.2);
                color: white;
            }
            .export-btn-intelligent:hover {
                background: linear-gradient(135deg, #20c997 0%, #28a745 100%);
            }
            .export-btn-pdf:hover {
                background: linear-gradient(135deg, #c82333 0%, #dc3545 100%);
            }
            .export-btn-intelligent:active, .export-btn-pdf:active {
                transform: translateY(-1px);
            }
            .btn-spinner {
                display: none;
                animation: spin 1s linear infinite;
            }
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
            .download-progress {
                height: 4px;
                background: #e9ecef;
                border-radius: 2px;
                overflow: hidden;
                margin-top: 10px;
                display: none;
            }
            .progress-bar {
                height: 100%;
                background: linear-gradient(90deg, #28a745, #20c997);
                width: 0%;
                transition: width 0.3s ease;
            }
            .progress-bar-pdf {
                height: 100%;
                background: linear-gradient(90deg, #dc3545, #c82333);
                width: 0%;
                transition: width 0.3s ease;
            }
            .badge-stat {
                font-size: 0.8em;
                padding: 4px 8px;
                border-radius: 12px;
            }
            .section-title {
                border-left: 4px solid #007bff;
                padding-left: 15px;
                margin-bottom: 20px;
            }
            .info-badge {
                background: rgba(0,123,255,0.1);
                color: #007bff;
                padding: 8px 12px;
                border-radius: 6px;
                font-size: 0.9em;
            }
            .btn-container {
                display: flex;
                flex-direction: column;
                gap: 15px;
                align-items: center;
            }
            .btn-row {
                display: flex;
                gap: 15px;
                justify-content: center;
                flex-wrap: wrap;
            }
        </style>
    </head>
    <body>
        <div class="container-fluid">
            <div class="row">
                <jsp:include page="../empleado/sidebar_empleado.jsp" />

                <div class="col-md-9 col-lg-10 main-content">
                    <div class="p-4">
                        <!-- Encabezado -->
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h2 class="fw-bold mb-1">📊 Mis Reportes de Actividad</h2>
                                <p class="text-muted mb-0">Gestión y análisis de tus donaciones como empleado</p>
                            </div>
                            <span class="info-badge">
                                <i class="fas fa-user-circle me-1"></i>
                                ${employeeUsername}
                            </span>
                        </div>

                        <!-- Tarjetas de Estadísticas -->
                        <div class="row g-4 mb-4">
                            <div class="col-md-3">
                                <div class="card stat-card border-0 bg-primary bg-gradient text-white">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-start">
                                            <div>
                                                <h6 class="mb-2 opacity-75">Mis Donaciones</h6>
                                                <h3 class="fw-bold">${myDonations}</h3>
                                                <small>
                                                    <i class="fas fa-arrow-trend-up me-1"></i>
                                                    Total asignadas
                                                </small>
                                            </div>
                                            <div class="icon-circle bg-white bg-opacity-25 p-2 rounded-circle">
                                                <i class="fas fa-gift fa-lg"></i>
                                            </div>
                                        </div>
                                        <div class="mt-3">
                                            <span class="badge-stat bg-white bg-opacity-25">
                                                <i class="fas fa-chart-line me-1"></i>
                                                ${myDonations != null && totalDonations != null && totalDonations > 0 ? 
                                                  String.format("%.1f", (myDonations * 100.0) / totalDonations) : "0"}%
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-md-3">
                                <div class="card stat-card border-0 bg-warning bg-gradient text-white">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-start">
                                            <div>
                                                <h6 class="mb-2 opacity-75">En Proceso</h6>
                                                <h3 class="fw-bold">${activeDonations}</h3>
                                                <small>
                                                    <i class="fas fa-clock me-1"></i>
                                                    Pendientes
                                                </small>
                                            </div>
                                            <div class="icon-circle bg-white bg-opacity-25 p-2 rounded-circle">
                                                <i class="fas fa-spinner fa-lg"></i>
                                            </div>
                                        </div>
                                        <div class="mt-3">
                                            <span class="badge-stat bg-white bg-opacity-25">
                                                <i class="fas fa-hourglass-half me-1"></i>
                                                Activas
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-md-3">
                                <div class="card stat-card border-0 bg-success bg-gradient text-white">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-start">
                                            <div>
                                                <h6 class="mb-2 opacity-75">Completadas</h6>
                                                <h3 class="fw-bold">${completedDonations}</h3>
                                                <small>
                                                    <i class="fas fa-check-circle me-1"></i>
                                                    Finalizadas
                                                </small>
                                            </div>
                                            <div class="icon-circle bg-white bg-opacity-25 p-2 rounded-circle">
                                                <i class="fas fa-clipboard-check fa-lg"></i>
                                            </div>
                                        </div>
                                        <div class="mt-3">
                                            <span class="badge-stat bg-white bg-opacity-25">
                                                <i class="fas fa-trophy me-1"></i>
                                                ${myDonations != null && completedDonations != null && myDonations > 0 ? 
                                                  String.format("%.0f", (completedDonations * 100.0) / myDonations) : "0"}%
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="col-md-3">
                                <div class="card stat-card border-0 bg-info bg-gradient text-white">
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between align-items-start">
                                            <div>
                                                <h6 class="mb-2 opacity-75">Total Sistema</h6>
                                                <h3 class="fw-bold">${totalDonations}</h3>
                                                <small>
                                                    <i class="fas fa-database me-1"></i>
                                                    Todas las donaciones
                                                </small>
                                            </div>
                                            <div class="icon-circle bg-white bg-opacity-25 p-2 rounded-circle">
                                                <i class="fas fa-chart-line fa-lg"></i>
                                            </div>
                                        </div>
                                        <div class="mt-3">
                                            <span class="badge-stat bg-white bg-opacity-25">
                                                <i class="fas fa-users me-1"></i>
                                                ${totalDonors} donantes
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Sección de Tablas -->
                        <div class="row g-4">
                            <div class="col-md-6">
                                <div class="card border-0 shadow-sm">
                                    <div class="card-header bg-white border-0 pt-3">
                                        <h5 class="mb-0 fw-bold section-title">
                                            <i class="fas fa-chart-pie me-2 text-primary"></i>
                                            Donaciones por Tipo
                                        </h5>
                                    </div>
                                    <div class="card-body">
                                        <div class="table-responsive">
                                            <table class="table table-hover mb-0">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th><i class="fas fa-tag me-1"></i> Tipo</th>
                                                        <th class="text-end"><i class="fas fa-hashtag me-1"></i> Cantidad</th>
                                                        <th class="text-end"><i class="fas fa-percentage me-1"></i> Porcentaje</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <%
                                                        Map<String, Long> myDonationsByType = (Map<String, Long>) request.getAttribute("myDonationsByType");
                                                        Long totalMyDonations = (Long) request.getAttribute("myDonations");
                                                        if (myDonationsByType != null && totalMyDonations != null && totalMyDonations > 0) {
                                                            for (Map.Entry<String, Long> entry : myDonationsByType.entrySet()) {
                                                                double percentage = (entry.getValue() * 100.0) / totalMyDonations;
                                                    %>
                                                    <tr>
                                                        <td>
                                                            <span class="d-flex align-items-center">
                                                                <span class="badge bg-primary bg-opacity-10 text-primary me-2">●</span>
                                                                <%= entry.getKey()%>
                                                            </span>
                                                        </td>
                                                        <td class="text-end fw-bold"><%= entry.getValue()%></td>
                                                        <td class="text-end">
                                                            <div class="d-flex align-items-center justify-content-end">
                                                                <span class="me-2"><%= String.format("%.1f%%", percentage)%></span>
                                                                <div class="progress" style="width: 60px; height: 6px;">
                                                                    <div class="progress-bar bg-primary" style="width: <%= percentage %>%"></div>
                                                                </div>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                    <%
                                                            }
                                                        } else {
                                                    %>
                                                    <tr>
                                                        <td colspan="3" class="text-center py-4 text-muted">
                                                            <i class="fas fa-inbox fa-lg mb-3"></i><br>
                                                            No hay datos disponibles
                                                        </td>
                                                    </tr>
                                                    <%
                                                        }
                                                    %>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="card border-0 shadow-sm">
                                    <div class="card-header bg-white border-0 pt-3">
                                        <h5 class="mb-0 fw-bold section-title">
                                            <i class="fas fa-map-marker-alt me-2 text-success"></i>
                                            Donaciones por Ubicación
                                        </h5>
                                    </div>
                                    <div class="card-body">
                                        <div class="table-responsive">
                                            <table class="table table-hover mb-0">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th><i class="fas fa-location-dot me-1"></i> Ubicación</th>
                                                        <th class="text-end"><i class="fas fa-hashtag me-1"></i> Cantidad</th>
                                                        <th class="text-end"><i class="fas fa-map-pin me-1"></i> Zona</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <%
                                                        Map<String, Long> myDonationsByLocation = (Map<String, Long>) request.getAttribute("myDonationsByLocation");
                                                        if (myDonationsByLocation != null && !myDonationsByLocation.isEmpty()) {
                                                            for (Map.Entry<String, Long> entry : myDonationsByLocation.entrySet()) {
                                                    %>
                                                    <tr>
                                                        <td>
                                                            <span class="d-flex align-items-center">
                                                                <i class="fas fa-map-pin text-danger me-2"></i>
                                                                <%= entry.getKey()%>
                                                            </span>
                                                        </td>
                                                        <td class="text-end fw-bold"><%= entry.getValue()%></td>
                                                        <td class="text-end">
                                                            <span class="badge bg-success bg-opacity-10 text-success">
                                                                Zona <%= Math.abs(entry.getKey().hashCode()) % 5 + 1 %>
                                                            </span>
                                                        </td>
                                                    </tr>
                                                    <%
                                                            }
                                                        } else {
                                                    %>
                                                    <tr>
                                                        <td colspan="3" class="text-center py-4 text-muted">
                                                            <i class="fas fa-map fa-lg mb-3"></i><br>
                                                            No hay datos de ubicación
                                                        </td>
                                                    </tr>
                                                    <%
                                                        }
                                                    %>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Sección de Exportación Inteligente - AMBOS BOTONES -->
                        <div class="row mt-5">
                            <div class="col-12">
                                <div class="card border-0 shadow-sm export-card">
                                    <div class="card-header bg-white border-0 pt-4">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div>
                                                <h4 class="fw-bold mb-1">
                                                    <i class="fas fa-download me-2 text-success"></i>
                                                    Exportar Reportes Detallados
                                                </h4>
                                                <p class="text-muted mb-0">Descarga análisis completos de tu actividad en diferentes formatos</p>
                                            </div>
                                            <span class="badge bg-success bg-opacity-10 text-success py-2 px-3">
                                                <i class="fas fa-file-alt me-1"></i>
                                                Formatos Disponibles
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <div class="card-body">
                                        <div class="row align-items-center">
                                            <div class="col-md-8">
                                                <h5 class="fw-bold mb-3">📊 Exportación de Reportes</h5>
                                                <p class="text-muted mb-4">
                                                    Elige el formato que mejor se adapte a tus necesidades:
                                                    <strong>CSV para análisis de datos</strong> o 
                                                    <strong>PDF para presentaciones profesionales</strong>.
                                                </p>
                                                
                                                <div class="row g-3 mb-4">
                                                    <div class="col-md-6">
                                                        <div class="d-flex align-items-start p-3 border rounded bg-light">
                                                            <div class="bg-success bg-opacity-10 p-2 rounded me-3">
                                                                <i class="fas fa-file-csv text-success fa-lg"></i>
                                                            </div>
                                                            <div>
                                                                <h6 class="fw-bold mb-1">CSV Profesional</h6>
                                                                <small class="text-muted d-block">
                                                                    Ideal para análisis en Excel, cálculos automáticos y procesamiento de datos
                                                                </small>
                                                                <div class="mt-2">
                                                                    <span class="badge bg-success bg-opacity-10 text-success me-1">
                                                                        <i class="fas fa-check me-1"></i>6 secciones
                                                                    </span>
                                                                    <span class="badge bg-success bg-opacity-10 text-success me-1">
                                                                        <i class="fas fa-calculator me-1"></i>Cálculos
                                                                    </span>
                                                                    <span class="badge bg-success bg-opacity-10 text-success">
                                                                        <i class="fas fa-chart-bar me-1"></i>Estadísticas
                                                                    </span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <div class="d-flex align-items-start p-3 border rounded bg-light">
                                                            <div class="bg-danger bg-opacity-10 p-2 rounded me-3">
                                                                <i class="fas fa-file-pdf text-danger fa-lg"></i>
                                                            </div>
                                                            <div>
                                                                <h6 class="fw-bold mb-1">PDF Profesional</h6>
                                                                <small class="text-muted d-block">
                                                                    Perfecto para imprimir, compartir y presentaciones formales
                                                                </small>
                                                                <div class="mt-2">
                                                                    <span class="badge bg-danger bg-opacity-10 text-danger me-1">
                                                                        <i class="fas fa-print me-1"></i>Impresión
                                                                    </span>
                                                                    <span class="badge bg-danger bg-opacity-10 text-danger me-1">
                                                                        <i class="fas fa-share-alt me-1"></i>Compartir
                                                                    </span>
                                                                    <span class="badge bg-danger bg-opacity-10 text-danger">
                                                                        <i class="fas fa-presentation me-1"></i>Presentación
                                                                    </span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            
                                            <div class="col-md-4">
                                                <div class="btn-container">
                                                    <!-- Botón CSV -->
                                                    <div class="text-center mb-3">
                                                        <button id="btnExportarCSV" class="btn export-btn-intelligent mb-2">
                                                            <i class="fas fa-file-csv fa-lg"></i>
                                                            <span id="btnTextCSV">Descargar CSV</span>
                                                            <i id="btnSpinnerCSV" class="fas fa-spinner btn-spinner"></i>
                                                        </button>
                                                        <div id="downloadProgressCSV" class="download-progress">
                                                            <div id="progressBarCSV" class="progress-bar"></div>
                                                        </div>
                                                        <small class="text-muted mt-2 d-block">
                                                            <i class="fas fa-info-circle me-1"></i>
                                                            Reporte_${employeeUsername}_<span id="currentDate"></span>.csv
                                                        </small>
                                                    </div>
                                                    
                                                    <!-- Botón PDF -->
                                                    <div class="text-center">
                                                        <button id="btnExportarPDF" class="btn export-btn-pdf mb-2">
                                                            <i class="fas fa-file-pdf fa-lg"></i>
                                                            <span id="btnTextPDF">Descargar PDF</span>
                                                            <i id="btnSpinnerPDF" class="fas fa-spinner btn-spinner"></i>
                                                        </button>
                                                        <div id="downloadProgressPDF" class="download-progress">
                                                            <div id="progressBarPDF" class="progress-bar-pdf"></div>
                                                        </div>
                                                        <small class="text-muted mt-2 d-block">
                                                            <i class="fas fa-info-circle me-1"></i>
                                                            Reporte_${employeeUsername}_<span id="currentDatePDF"></span>.pdf
                                                        </small>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <!-- Comparativa de formatos -->
                                        <div class="mt-4 pt-3 border-top">
                                            <h6 class="fw-bold mb-3">
                                                <i class="fas fa-balance-scale me-2"></i>
                                                Comparativa de Formatos:
                                            </h6>
                                            <div class="row">
                                                <div class="col-md-6">
                                                    <div class="card border-success border-1 mb-3">
                                                        <div class="card-header bg-success bg-opacity-10 border-success">
                                                            <h6 class="mb-0 fw-bold text-success">
                                                                <i class="fas fa-file-csv me-2"></i>Ventajas CSV
                                                            </h6>
                                                        </div>
                                                        <div class="card-body">
                                                            <ul class="mb-0">
                                                                <li>✅ Datos editables y procesables</li>
                                                                <li>✅ Compatible con Excel, Google Sheets</li>
                                                                <li>✅ Ideal para análisis y cálculos</li>
                                                                <li>✅ Archivos más pequeños</li>
                                                                <li>✅ Fácil importación a bases de datos</li>
                                                            </ul>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="col-md-6">
                                                    <div class="card border-danger border-1 mb-3">
                                                        <div class="card-header bg-danger bg-opacity-10 border-danger">
                                                            <h6 class="mb-0 fw-bold text-danger">
                                                                <i class="fas fa-file-pdf me-2"></i>Ventajas PDF
                                                            </h6>
                                                        </div>
                                                        <div class="card-body">
                                                            <ul class="mb-0">
                                                                <li>✅ Formato profesional para presentar</li>
                                                                <li>✅ Diseño fijo, no se altera</li>
                                                                <li>✅ Ideal para imprimir y archivar</li>
                                                                <li>✅ Compatible universalmente</li>
                                                                <li>✅ Incluye gráficos y diseño visual</li>
                                                            </ul>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="card-footer bg-white border-0 pt-3">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <small class="text-muted">
                                                <i class="fas fa-clock me-1"></i>
                                                Última actualización: 
                                                <span id="currentDateTime"></span>
                                            </small>
                                            <div>
                                                <small class="text-success me-3">
                                                    <i class="fas fa-file-csv me-1"></i>
                                                    CSV: Análisis de datos
                                                </small>
                                                <small class="text-danger">
                                                    <i class="fas fa-file-pdf me-1"></i>
                                                    PDF: Presentación profesional
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Establecer fecha actual para los nombres de archivo
            const currentDate = new Date().toISOString().slice(0, 10).replace(/-/g, '');
            document.getElementById('currentDate').textContent = currentDate;
            document.getElementById('currentDatePDF').textContent = currentDate;
            
            // Mostrar fecha y hora actual
            function updateDateTime() {
                const now = new Date();
                const options = { 
                    weekday: 'long', 
                    year: 'numeric', 
                    month: 'long', 
                    day: 'numeric',
                    hour: '2-digit', 
                    minute: '2-digit',
                    second: '2-digit'
                };
                document.getElementById('currentDateTime').textContent = 
                    now.toLocaleDateString('es-ES', options);
            }
            updateDateTime();
            setInterval(updateDateTime, 1000);
            
            // ========== FUNCIÓN PARA BOTÓN CSV ==========
            document.getElementById('btnExportarCSV').addEventListener('click', function() {
                const btn = this;
                const btnText = document.getElementById('btnTextCSV');
                const btnSpinner = document.getElementById('btnSpinnerCSV');
                const progressBar = document.getElementById('progressBarCSV');
                const downloadProgress = document.getElementById('downloadProgressCSV');
                
                // Deshabilitar botón y mostrar spinner
                btn.disabled = true;
                btnText.textContent = 'Generando CSV...';
                btnSpinner.style.display = 'inline-block';
                
                // Mostrar barra de progreso
                downloadProgress.style.display = 'block';
                
                // Simular progreso
                let progress = 0;
                const progressInterval = setInterval(() => {
                    progress += 10;
                    progressBar.style.width = progress + '%';
                    
                    if (progress >= 90) {
                        clearInterval(progressInterval);
                    }
                }, 100);
                
                // Iniciar descarga
                const downloadUrl = '${pageContext.request.contextPath}/exportExcel';
                window.location.href = downloadUrl;
                
                // Restaurar botón después de 3 segundos
                setTimeout(() => {
                    clearInterval(progressInterval);
                    progressBar.style.width = '100%';
                    
                    setTimeout(() => {
                        btn.disabled = false;
                        btnText.textContent = 'Descargar CSV';
                        btnSpinner.style.display = 'none';
                        downloadProgress.style.display = 'none';
                        progressBar.style.width = '0%';
                        
                        // Mostrar notificación de éxito
                        showNotification('✅ Reporte CSV generado exitosamente', 'success');
                    }, 500);
                }, 3000);
            });
            
            // ========== FUNCIÓN PARA BOTÓN PDF ==========
            document.getElementById('btnExportarPDF').addEventListener('click', function() {
                const btn = this;
                const btnText = document.getElementById('btnTextPDF');
                const btnSpinner = document.getElementById('btnSpinnerPDF');
                const progressBar = document.getElementById('progressBarPDF');
                const downloadProgress = document.getElementById('downloadProgressPDF');
                
                // Deshabilitar botón y mostrar spinner
                btn.disabled = true;
                btnText.textContent = 'Generando PDF...';
                btnSpinner.style.display = 'inline-block';
                
                // Mostrar barra de progreso
                downloadProgress.style.display = 'block';
                
                // Simular progreso
                let progress = 0;
                const progressInterval = setInterval(() => {
                    progress += 8; // Más lento para PDF
                    progressBar.style.width = progress + '%';
                    
                    if (progress >= 90) {
                        clearInterval(progressInterval);
                    }
                }, 150);
                
                // Iniciar descarga
                const downloadUrl = '${pageContext.request.contextPath}/exportPdf';
                window.location.href = downloadUrl;
                
                // Restaurar botón después de 4 segundos (PDF es más lento)
                setTimeout(() => {
                    clearInterval(progressInterval);
                    progressBar.style.width = '100%';
                    
                    setTimeout(() => {
                        btn.disabled = false;
                        btnText.textContent = 'Descargar PDF';
                        btnSpinner.style.display = 'none';
                        downloadProgress.style.display = 'none';
                        progressBar.style.width = '0%';
                        
                        // Mostrar notificación de éxito
                        showNotification('📄 Reporte PDF generado exitosamente', 'success');
                    }, 500);
                }, 4000);
            });
            
            // ========== FUNCIÓN PARA MOSTRAR NOTIFICACIONES ==========
            function showNotification(message, type) {
                // Eliminar notificaciones anteriores
                const oldNotifications = document.querySelectorAll('.custom-notification');
                oldNotifications.forEach(notif => notif.remove());
                
                const notification = document.createElement('div');
                notification.className = `custom-notification alert alert-${type} alert-dismissible fade show position-fixed`;
                notification.style.cssText = `
                    top: 20px;
                    right: 20px;
                    z-index: 1050;
                    min-width: 300px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
                `;
                
                let title = 'Información';
                let icon = 'ℹ️';
                if (type === 'success') {
                    title = '¡Éxito!';
                    icon = '✅';
                } else if (type === 'danger') {
                    title = 'Error';
                    icon = '❌';
                } else if (type === 'warning') {
                    title = 'Advertencia';
                    icon = '⚠️';
                }
                
                notification.innerHTML = `
                    <div class="d-flex align-items-center">
                        <span style="font-size: 1.5rem; margin-right: 10px;">${icon}</span>
                        <div>
                            <strong>${title}</strong><br>
                            <span>${message}</span>
                        </div>
                        <button type="button" class="btn-close ms-auto" onclick="this.parentElement.parentElement.remove()"></button>
                    </div>
                `;
                
                document.body.appendChild(notification);
                
                // Auto-remover después de 5 segundos
                setTimeout(() => {
                    if (notification.parentNode) {
                        notification.remove();
                    }
                }, 5000);
            }
            
            // ========== EFECTOS ADICIONALES PARA TARJETAS ==========
            document.addEventListener('DOMContentLoaded', function() {
                // Efecto para tarjetas de estadísticas
                const cards = document.querySelectorAll('.stat-card');
                cards.forEach(card => {
                    card.addEventListener('mouseenter', function() {
                        this.style.transform = 'translateY(-5px)';
                    });
                    card.addEventListener('mouseleave', function() {
                        this.style.transform = 'translateY(0)';
                    });
                });
                
                // Efectos para botones de exportación
                const exportButtons = document.querySelectorAll('.export-btn-intelligent, .export-btn-pdf');
                exportButtons.forEach(btn => {
                    btn.addEventListener('mouseenter', function() {
                        if (!this.disabled) {
                            this.style.transform = 'translateY(-3px) scale(1.02)';
                        }
                    });
                    btn.addEventListener('mouseleave', function() {
                        if (!this.disabled) {
                            this.style.transform = 'translateY(0) scale(1)';
                        }
                    });
                });
                
                // Efecto de pulso para llamar la atención
                setTimeout(() => {
                    const csvBtn = document.getElementById('btnExportarCSV');
                    const pdfBtn = document.getElementById('btnExportarPDF');
                    
                    [csvBtn, pdfBtn].forEach(btn => {
                        btn.style.animation = 'pulse 2s infinite';
                    });
                    
                    // Agregar animación CSS para pulso
                    const style = document.createElement('style');
                    style.textContent = `
                        @keyframes pulse {
                            0% { box-shadow: 0 0 0 0 rgba(40, 167, 69, 0.7); }
                            70% { box-shadow: 0 0 0 10px rgba(40, 167, 69, 0); }
                            100% { box-shadow: 0 0 0 0 rgba(40, 167, 69, 0); }
                        }
                    `;
                    document.head.appendChild(style);
                    
                    // Quitar animación después de 6 segundos
                    setTimeout(() => {
                        [csvBtn, pdfBtn].forEach(btn => {
                            btn.style.animation = '';
                        });
                    }, 6000);
                }, 1000);
            });
        </script>
    </body>
</html>