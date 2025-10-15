package com.donaciones.models;

import java.util.Date;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class Donation {
    private int id;
    private String donorUsername;
    private String donorName; // Nuevo campo para mostrar nombre del donante
    private String type;
    private String description;
    private int quantity;
    private String condition;
    private String location;
    private String region; // Nuevo campo separado para región
    private String district; // Nuevo campo separado para distrito
    private String status;
    private Date createdDate;
    private Date updatedDate;
    private String employeeUsername;
    private String employeeName; // Nuevo campo para mostrar nombre del empleado
    private String receiverUsername;
    private String receiverName; // Nuevo campo para mostrar nombre del receptor
    private Date deliveryDate;
    private String notes;
    private List<String> images; // Cambiado a List para mejor manejo
    private boolean urgent;
    private String size;
    private double estimatedValue;
    private String pickupAddress;
    private Date pickupDate;
    private String pickupTime;
    private boolean pickupConfirmed;
    private String donationQuality;
    private int rating;
    private String feedback;
    private List<String> tags; // Nuevo campo para etiquetas
    private Date donationDate;
    private String address;

    // Constructors
    public Donation() {
        this.createdDate = new Date();
        this.updatedDate = new Date();
        this.status = "pending";
        this.images = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.quantity = 1;
        this.urgent = false;
        this.pickupConfirmed = false;
        this.rating = 0;
    }

    public Donation(String donorUsername, String type, String description, String location) {
        this();
        this.donorUsername = donorUsername;
        this.type = type;
        this.description = description;
        this.location = location;
    }

    public Donation(int id, String donorUsername, String type, String description, 
                   int quantity, String condition, String location, String status, 
                   Date createdDate, Date updatedDate, String employeeUsername, 
                   String receiverUsername, Date deliveryDate, String notes, 
                   String images, boolean urgent, String size, double estimatedValue) {
        this.id = id;
        this.donorUsername = donorUsername;
        this.type = type;
        this.description = description;
        this.quantity = quantity;
        this.condition = condition;
        this.location = location;
        this.status = status;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.employeeUsername = employeeUsername;
        this.receiverUsername = receiverUsername;
        this.deliveryDate = deliveryDate;
        this.notes = notes;
        setImagesFromString(images);
        this.urgent = urgent;
        this.size = size;
        this.estimatedValue = estimatedValue;
        this.tags = new ArrayList<>();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDonorUsername() {
        return donorUsername;
    }

    public void setDonorUsername(String donorUsername) {
        this.donorUsername = donorUsername;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedDate = new Date();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images != null ? images : new ArrayList<>();
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getEstimatedValue() {
        return estimatedValue;
    }

    public void setEstimatedValue(double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public boolean isPickupConfirmed() {
        return pickupConfirmed;
    }

    public void setPickupConfirmed(boolean pickupConfirmed) {
        this.pickupConfirmed = pickupConfirmed;
    }

    public String getDonationQuality() {
        return donationQuality;
    }

    public void setDonationQuality(String donationQuality) {
        this.donationQuality = donationQuality;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }
    
    // Getters y Setters
    public Date getDonationDate() {
        return donationDate;
    }
    
    public void setDonationDate(Date donationDate) {
        this.donationDate = donationDate;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }

    // Utility methods for images
    public void addImage(String imagePath) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            this.images.add(imagePath);
        }
    }

    public void removeImage(String imagePath) {
        if (this.images != null) {
            this.images.remove(imagePath);
        }
    }

    public String getImagesAsString() {
        if (images == null || images.isEmpty()) {
            return "";
        }
        return String.join(",", images);
    }

    public void setImagesFromString(String imagesString) {
        if (imagesString != null && !imagesString.trim().isEmpty()) {
            this.images = new ArrayList<>(Arrays.asList(imagesString.split(",")));
        } else {
            this.images = new ArrayList<>();
        }
    }

    // Utility methods for tags
    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        if (tag != null && !tag.trim().isEmpty() && !this.tags.contains(tag)) {
            this.tags.add(tag);
        }
    }

    public void removeTag(String tag) {
        if (this.tags != null) {
            this.tags.remove(tag);
        }
    }

    public String getTagsAsString() {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return String.join(",", tags);
    }

    public void setTagsFromString(String tagsString) {
        if (tagsString != null && !tagsString.trim().isEmpty()) {
            this.tags = new ArrayList<>(Arrays.asList(tagsString.split(",")));
        } else {
            this.tags = new ArrayList<>();
        }
    }

    // Business logic methods
    public boolean isAvailable() {
        return "available".equals(status) || "active".equals(status);
    }

    public boolean isPending() {
        return "pending".equals(status);
    }

    public boolean isCompleted() {
        return "completed".equals(status) || "delivered".equals(status);
    }

    public boolean isInProgress() {
        return "in_progress".equals(status) || "assigned".equals(status);
    }

    public boolean isCancelled() {
        return "cancelled".equals(status) || "rejected".equals(status);
    }

    public boolean canBeEdited() {
        return isPending() || isAvailable();
    }

    public boolean canBeAssigned() {
        return isAvailable() || isPending();
    }

    public boolean canBeCompleted() {
        return isInProgress() && receiverUsername != null && !receiverUsername.isEmpty();
    }

    public boolean requiresPickup() {
        return "furniture".equalsIgnoreCase(type) || 
               "electronics".equalsIgnoreCase(type) ||
               "large".equalsIgnoreCase(size) ||
               quantity > 5;
    }

    public String getFormattedStatus() {
        switch (status.toLowerCase()) {
            case "pending":
                return "Pendiente";
            case "available":
                return "Disponible";
            case "active":
                return "Activa";
            case "in_progress":
                return "En Progreso";
            case "assigned":
                return "Asignada";
            case "completed":
                return "Completada";
            case "delivered":
                return "Entregada";
            case "cancelled":
                return "Cancelada";
            case "rejected":
                return "Rechazada";
            case "expired":
                return "Expirada";
            default:
                return status;
        }
    }

    public String getFormattedType() {
        switch (type.toLowerCase()) {
            case "clothes":
                return "Ropa";
            case "food":
                return "Alimentos";
            case "books":
                return "Libros";
            case "toys":
                return "Juguetes";
            case "furniture":
                return "Muebles";
            case "electronics":
                return "Electrónicos";
            case "school_supplies":
                return "Útiles Escolares";
            case "medical_supplies":
                return "Insumos Médicos";
            case "hygiene_products":
                return "Productos de Higiene";
            case "money":
                return "Dinero";
            default:
                return type;
        }
    }

    public String getFormattedCondition() {
        switch (condition.toLowerCase()) {
            case "new":
                return "Nuevo";
            case "like_new":
                return "Como Nuevo";
            case "good":
                return "Buen Estado";
            case "fair":
                return "Estado Regular";
            case "poor":
                return "Necesita Reparación";
            default:
                return condition;
        }
    }

    public String getStatusColor() {
        switch (status.toLowerCase()) {
            case "pending":
                return "warning";
            case "available":
                return "info";
            case "active":
                return "primary";
            case "in_progress":
                return "primary";
            case "assigned":
                return "success";
            case "completed":
                return "success";
            case "delivered":
                return "success";
            case "cancelled":
                return "danger";
            case "rejected":
                return "danger";
            case "expired":
                return "secondary";
            default:
                return "secondary";
        }
    }

    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }

    public String getFirstImage() {
        if (hasImages()) {
            return images.get(0);
        }
        return null;
    }

    public int getDaysSinceCreation() {
        if (createdDate == null) return 0;
        long diff = System.currentTimeMillis() - createdDate.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public boolean isExpired() {
        return getDaysSinceCreation() > 30 && !isCompleted() && !isCancelled();
    }

    public String getUrgencyLabel() {
        if (urgent) {
            return "Urgente";
        }
        if (getDaysSinceCreation() > 20) {
            return "Próximo a expirar";
        }
        return "Normal";
    }

    public String getFormattedEstimatedValue() {
        return String.format("S/ %.2f", estimatedValue);
    }

    public String getQuantityDescription() {
        if (quantity == 1) {
            return "1 unidad";
        } else {
            return quantity + " unidades";
        }
    }

    // Validation methods
    public boolean isValid() {
        return donorUsername != null && !donorUsername.trim().isEmpty() &&
               type != null && !type.trim().isEmpty() &&
               description != null && !description.trim().isEmpty() &&
               quantity > 0;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        
        if (donorUsername == null || donorUsername.trim().isEmpty()) {
            errors.add("El nombre de usuario del donante es requerido");
        }
        
        if (type == null || type.trim().isEmpty()) {
            errors.add("El tipo de donación es requerido");
        }
        
        if (description == null || description.trim().isEmpty()) {
            errors.add("La descripción es requerida");
        }
        
        if (quantity <= 0) {
            errors.add("La cantidad debe ser mayor a 0");
        }
        
        if (location == null || location.trim().isEmpty()) {
            errors.add("La ubicación es requerida");
        }
        
        return errors;
    }

    @Override
    public String toString() {
        return "Donation{" +
                "id=" + id +
                ", donorUsername='" + donorUsername + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", condition='" + condition + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", urgent=" + urgent +
                ", estimatedValue=" + estimatedValue +
                '}';
    }
}