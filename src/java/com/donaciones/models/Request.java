package com.donaciones.models;

import java.util.Date;

public class Request {
    private int id;
    private String type;
    private String description;
    private String location;
    private String status; // pending, in_progress, completed, cancelled
    private String requestedBy;
    private Date requestDate;
    private String assignedTo;
    private Date completionDate;
    private String notes;
    private int priority; // 1-5, donde 5 es más urgente

    // Constructores
    public Request() {
        this.requestDate = new Date();
        this.status = "pending";
        this.priority = 3; // Media por defecto
    }

    public Request(String type, String description, String location, String requestedBy) {
        this();
        this.type = type;
        this.description = description;
        this.location = location;
        this.requestedBy = requestedBy;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if ("completed".equals(status)) {
            this.completionDate = new Date();
        }
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    // Métodos auxiliares para la vista - CORREGIDOS
    public String getFormattedStatus() {
        if (status == null) return "Pendiente";
        switch (status.toLowerCase()) {
            case "pending": return "Pendiente";
            case "in_progress": return "En Proceso";
            case "completed": return "Completada";
            case "cancelled": return "Cancelada";
            default: return status;
        }
    }

    public String getPriorityBadge() {
        switch (priority) {
            case 1: return "bg-secondary";
            case 2: return "bg-info";
            case 3: return "bg-primary";
            case 4: return "bg-warning";
            case 5: return "bg-danger";
            default: return "bg-secondary";
        }
    }

    public String getPriorityText() {
        switch (priority) {
            case 1: return "Muy Baja";
            case 2: return "Baja";
            case 3: return "Media";
            case 4: return "Alta";
            case 5: return "Urgente";
            default: return "Media";
        }
    }

    public String getStatusBadgeColor() {
        if (status == null) return "warning";
        switch (status.toLowerCase()) {
            case "pending": return "warning";
            case "in_progress": return "info";
            case "completed": return "success";
            case "cancelled": return "danger";
            default: return "secondary";
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", requestedBy='" + requestedBy + '\'' +
                ", priority=" + priority +
                '}';
    }
}