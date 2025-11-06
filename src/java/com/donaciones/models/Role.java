package com.donaciones.models;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Role {
    private int id;
    private String nombre;
    private String descripcion;
    private List<String> permisos;
    private boolean activo;
    private Date createdAt;

    public Role() {
        this.permisos = new ArrayList<>();
        this.activo = true;
        this.createdAt = new Date();
    }

    public Role(String nombre, String descripcion) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public List<String> getPermisos() { return permisos; }
    public void setPermisos(List<String> permisos) { this.permisos = permisos; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public void addPermiso(String permiso) {
        if (!this.permisos.contains(permiso)) {
            this.permisos.add(permiso);
        }
    }

    public boolean hasPermiso(String permiso) {
        return permisos.contains(permiso) || permisos.contains("all");
    }

    public String getPermisosAsString() {
        return String.join(",", permisos);
    }

    public void setPermisosFromString(String permisosStr) {
        if (permisosStr != null && !permisosStr.isEmpty()) {
            this.permisos = List.of(permisosStr.split(","));
        }
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", permisos=" + permisos +
                ", activo=" + activo +
                '}';
    }
}