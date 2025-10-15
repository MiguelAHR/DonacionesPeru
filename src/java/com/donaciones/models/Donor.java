package com.donaciones.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Donor extends User {
    private List<String> donationTypes;
    private String comments;
    private int totalDonations;
    private Date lastDonationDate;
    private double rating;
    private int completedDonations;

    // Constructors
    public Donor() {
        super();
        this.donationTypes = new ArrayList<>();
        this.totalDonations = 0;
        this.completedDonations = 0;
        this.rating = 0.0;
        setUserType("donador");
    }

    public Donor(String username, String password) {
        super(username, password, "donador");
        this.donationTypes = new ArrayList<>();
        this.totalDonations = 0;
        this.completedDonations = 0;
        this.rating = 0.0;
    }

    public Donor(int id, String username, String password, String firstName, String lastName, 
                 String email, String phone, String dni, Date birthDate, String region, 
                 String district, String address, Date registrationDate, boolean active, 
                 boolean notificationsEnabled, List<String> donationTypes, String comments, 
                 int totalDonations, Date lastDonationDate, double rating, int completedDonations) {
        super(id, username, password, "donador", firstName, lastName, email, phone, dni, 
              birthDate, region, district, address, registrationDate, active, notificationsEnabled);
        this.donationTypes = donationTypes != null ? donationTypes : new ArrayList<>();
        this.comments = comments;
        this.totalDonations = totalDonations;
        this.lastDonationDate = lastDonationDate;
        this.rating = rating;
        this.completedDonations = completedDonations;
    }

    // Getters and Setters
    public List<String> getDonationTypes() {
        return donationTypes;
    }

    public void setDonationTypes(List<String> donationTypes) {
        this.donationTypes = donationTypes != null ? donationTypes : new ArrayList<>();
    }

    public void addDonationType(String donationType) {
        if (!this.donationTypes.contains(donationType)) {
            this.donationTypes.add(donationType);
        }
    }

    public boolean removeDonationType(String donationType) {
        return this.donationTypes.remove(donationType);
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getTotalDonations() {
        return totalDonations;
    }

    public void setTotalDonations(int totalDonations) {
        this.totalDonations = totalDonations;
    }

    public Date getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(Date lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getCompletedDonations() {
        return completedDonations;
    }

    public void setCompletedDonations(int completedDonations) {
        this.completedDonations = completedDonations;
    }

    // Business methods
    public void incrementDonations() {
        this.totalDonations++;
        this.lastDonationDate = new Date();
    }

    public void incrementCompletedDonations() {
        this.completedDonations++;
        this.totalDonations++;
        this.lastDonationDate = new Date();
    }

    public String getDonationTypesAsString() {
        return String.join(", ", donationTypes);
    }

    public String getFormattedDonationTypes() {
        if (donationTypes == null || donationTypes.isEmpty()) {
            return "Sin tipos especificados";
        }
        return String.join(", ", donationTypes);
    }

    public boolean canDonate(String donationType) {
        return donationTypes.contains(donationType);
    }

    public double getSuccessRate() {
        if (totalDonations == 0) return 0.0;
        return (completedDonations * 100.0) / totalDonations;
    }

    public boolean isActiveDonor() {
        // Considerar activo si ha donado en los últimos 6 meses
        if (lastDonationDate == null) return false;
        long sixMonthsAgo = System.currentTimeMillis() - (180L * 24L * 60L * 60L * 1000L);
        return lastDonationDate.getTime() > sixMonthsAgo;
    }

    public String getDonorLevel() {
        if (completedDonations >= 10) return "Oro";
        if (completedDonations >= 5) return "Plata";
        if (completedDonations >= 1) return "Bronce";
        return "Nuevo";
    }

    @Override
    public String toString() {
        return "Donor{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", donationTypes=" + donationTypes +
                ", totalDonations=" + totalDonations +
                ", completedDonations=" + completedDonations +
                ", rating=" + rating +
                ", region='" + getRegion() + '\'' +
                ", activeDonor=" + isActiveDonor() +
                '}';
    }
}