package com.donaciones.models;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class Reporte {
    private int id;
    private String titulo;
    private String descripcion;
    private String tipoReporte;
    private Date fechaGeneracion;
    private String generadoPor;
    private Map<String, Object> parametros;
    private String datos;
    private String formato;
    private String estado;
    private String rutaArchivo;

    public Reporte() {
        this.parametros = new HashMap<>();
        this.fechaGeneracion = new Date();
        this.formato = "html";
        this.estado = "pendiente";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipoReporte() { return tipoReporte; }
    public void setTipoReporte(String tipoReporte) { this.tipoReporte = tipoReporte; }

    public Date getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(Date fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public String getGeneradoPor() { return generadoPor; }
    public void setGeneradoPor(String generadoPor) { this.generadoPor = generadoPor; }

    public Map<String, Object> getParametros() { return parametros; }
    public void setParametros(Map<String, Object> parametros) { this.parametros = parametros; }

    public String getDatos() { return datos; }
    public void setDatos(String datos) { this.datos = datos; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public void addParametro(String key, Object value) {
        this.parametros.put(key, value);
    }

    public Object getParametro(String key) {
        return this.parametros.get(key);
    }

    public String getFormattedEstado() {
        switch (estado) {
            case "pendiente": return "Pendiente";
            case "generado": return "Generado";
            case "error": return "Error";
            default: return estado;
        }
    }

    public String getFormattedTipoReporte() {
        switch (tipoReporte) {
            case "donaciones": return "Donaciones";
            case "solicitudes": return "Solicitudes";
            case "usuarios": return "Usuarios";
            case "inventario": return "Inventario";
            case "estadisticas": return "Estadísticas";
            default: return tipoReporte;
        }
    }

    @Override
    public String toString() {
        return "Reporte{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", tipoReporte='" + tipoReporte + '\'' +
                ", estado='" + estado + '\'' +
                ", fechaGeneracion=" + fechaGeneracion +
                '}';
    }
}