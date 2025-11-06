package com.donaciones.models;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Catalogo {
    private int id;
    private int donacionId;
    private String titulo;
    private String descripcion;
    private String tipo;
    private int cantidad;
    private String condicion;
    private String ubicacion;
    private String donante;
    private Date fechaIngreso;
    private Date fechaDisponible;
    private String estado;
    private String imagen;
    private List<String> tags;
    private int prioridad;

    public Catalogo() {
        this.tags = new ArrayList<>();
        this.estado = "disponible";
        this.fechaIngreso = new Date();
        this.fechaDisponible = new Date();
        this.prioridad = 3;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDonacionId() { return donacionId; }
    public void setDonacionId(int donacionId) { this.donacionId = donacionId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getCondicion() { return condicion; }
    public void setCondicion(String condicion) { this.condicion = condicion; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getDonante() { return donante; }
    public void setDonante(String donante) { this.donante = donante; }

    public Date getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Date fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public Date getFechaDisponible() { return fechaDisponible; }
    public void setFechaDisponible(Date fechaDisponible) { this.fechaDisponible = fechaDisponible; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    public void addTag(String tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
        }
    }

    public String getTagsAsString() {
        return String.join(",", tags);
    }

    public void setTagsFromString(String tagsStr) {
        if (tagsStr != null && !tagsStr.isEmpty()) {
            this.tags = List.of(tagsStr.split(","));
        }
    }

    public boolean isDisponible() {
        return "disponible".equals(estado);
    }

    public String getFormattedEstado() {
        switch (estado) {
            case "disponible": return "Disponible";
            case "asignado": return "Asignado";
            case "entregado": return "Entregado";
            default: return estado;
        }
    }

    @Override
    public String toString() {
        return "Catalogo{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", tipo='" + tipo + '\'' +
                ", estado='" + estado + '\'' +
                ", donante='" + donante + '\'' +
                '}';
    }
}