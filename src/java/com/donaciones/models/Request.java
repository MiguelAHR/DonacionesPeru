package com.donaciones.models;

import java.util.Date;

public class Request {
    private int id;
    private String type;
    private String description;
    private String location;
    private String status;
    private String requestedBy;
    private Date requestDate;
    private String assignedTo;
    private Date completionDate;
    private String notes;
    private int priority;

    // Constructores
    public Request() {
    }

    public Request(int id, String type, String description, String location, String status, 
                  String requestedBy, Date requestDate, String assignedTo, Date completionDate, 
                  String notes, int priority) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.location = location;
        this.status = status;
        this.requestedBy = requestedBy;
        this.requestDate = requestDate;
        this.assignedTo = assignedTo;
        this.completionDate = completionDate;
        this.notes = notes;
        this.priority = priority;
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

    // Métodos auxiliares para la vista
    public String getFormattedStatus() {
        switch (status) {
            case "pending": return "Pendiente";
            case "in_progress": return "En Progreso";
            case "completed": return "Completada";
            case "cancelled": return "Cancelada";
            default: return status;
        }
    }

    public String getPriorityBadge() {
        switch (priority) {
            case 1: return "bg-danger";
            case 2: return "bg-warning";
            case 3: return "bg-info";
            case 4: return "bg-primary";
            case 5: return "bg-secondary";
            default: return "bg-secondary";
        }
    }

    public String getPriorityText() {
        switch (priority) {
            case 1: return "Muy Alta";
            case 2: return "Alta";
            case 3: return "Media";
            case 4: return "Baja";
            case 5: return "Muy Baja";
            default: return "No definida";
        }
    }
}