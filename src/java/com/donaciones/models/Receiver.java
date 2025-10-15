package com.donaciones.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Receiver extends User {
    private int familySize;
    private int children;
    private int adults;
    private int elderly;
    private String economicSituation;
    private List<String> needs;
    private String needsDescription;
    private boolean dataConsent;
    private boolean verified;
    private String verificationStatus; 
    private Date verificationDate;
    private String verifiedBy;
    private int receivedDonations;
    private int pendingRequests;
    private String specialNeeds;
    private String housingSituation;

    // Constructors
    public Receiver() {
        super();
        this.needs = new ArrayList<>();
        this.verified = false;
        this.verificationStatus = "pending";
        this.receivedDonations = 0;
        this.pendingRequests = 0;
        setUserType("receptor");
    }

    public Receiver(String username, String password) {
        super(username, password, "receptor");
        this.needs = new ArrayList<>();
        this.verified = false;
        this.verificationStatus = "pending";
        this.receivedDonations = 0;
        this.pendingRequests = 0;
    }

    public Receiver(int id, String username, String password, String firstName, String lastName, 
                    String email, String phone, String dni, Date birthDate, String region, 
                    String district, String address, Date registrationDate, boolean active, 
                    boolean notificationsEnabled, int familySize, int children, int adults, 
                    int elderly, String economicSituation, List<String> needs, String needsDescription, 
                    boolean dataConsent, boolean verified, String verificationStatus, 
                    Date verificationDate, String verifiedBy, int receivedDonations, 
                    int pendingRequests, String specialNeeds, String housingSituation) {
        super(id, username, password, "receptor", firstName, lastName, email, phone, dni, 
              birthDate, region, district, address, registrationDate, active, notificationsEnabled);
        this.familySize = familySize;
        this.children = children;
        this.adults = adults;
        this.elderly = elderly;
        this.economicSituation = economicSituation;
        this.needs = needs != null ? needs : new ArrayList<>();
        this.needsDescription = needsDescription;
        this.dataConsent = dataConsent;
        this.verified = verified;
        this.verificationStatus = verificationStatus;
        this.verificationDate = verificationDate;
        this.verifiedBy = verifiedBy;
        this.receivedDonations = receivedDonations;
        this.pendingRequests = pendingRequests;
        this.specialNeeds = specialNeeds;
        this.housingSituation = housingSituation;
    }

    // Getters and Setters
    public int getFamilySize() {
        return familySize;
    }

    public void setFamilySize(int familySize) {
        this.familySize = familySize;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getElderly() {
        return elderly;
    }

    public void setElderly(int elderly) {
        this.elderly = elderly;
    }

    public String getEconomicSituation() {
        return economicSituation;
    }

    public void setEconomicSituation(String economicSituation) {
        this.economicSituation = economicSituation;
    }

    public List<String> getNeeds() {
        return needs;
    }

    public void setNeeds(List<String> needs) {
        this.needs = needs != null ? needs : new ArrayList<>();
    }

    public void addNeed(String need) {
        if (!this.needs.contains(need)) {
            this.needs.add(need);
        }
    }

    public boolean removeNeed(String need) {
        return this.needs.remove(need);
    }

    public String getNeedsDescription() {
        return needsDescription;
    }

    public void setNeedsDescription(String needsDescription) {
        this.needsDescription = needsDescription;
    }

    public boolean isDataConsent() {
        return dataConsent;
    }

    public void setDataConsent(boolean dataConsent) {
        this.dataConsent = dataConsent;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
        if (verified) {
            this.verificationStatus = "verified";
            this.verificationDate = new Date();
        }
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
        if ("verified".equals(verificationStatus)) {
            this.verified = true;
            this.verificationDate = new Date();
        } else {
            this.verified = false;
        }
    }

    public Date getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(Date verificationDate) {
        this.verificationDate = verificationDate;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public int getReceivedDonations() {
        return receivedDonations;
    }

    public void setReceivedDonations(int receivedDonations) {
        this.receivedDonations = receivedDonations;
    }

    public int getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(int pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public String getSpecialNeeds() {
        return specialNeeds;
    }

    public void setSpecialNeeds(String specialNeeds) {
        this.specialNeeds = specialNeeds;
    }

    public String getHousingSituation() {
        return housingSituation;
    }

    public void setHousingSituation(String housingSituation) {
        this.housingSituation = housingSituation;
    }

    // Business methods
    public void incrementReceivedDonations() {
        this.receivedDonations++;
    }

    public void incrementPendingRequests() {
        this.pendingRequests++;
    }

    public void decrementPendingRequests() {
        if (this.pendingRequests > 0) {
            this.pendingRequests--;
        }
    }

    public String getNeedsAsString() {
        return String.join(", ", needs);
    }

    public String getFormattedNeeds() {
        if (needs == null || needs.isEmpty()) {
            return "Sin necesidades especificadas";
        }
        return String.join(", ", needs);
    }

    public boolean needsType(String donationType) {
        return needs.contains(donationType);
    }

    public String getFormattedEconomicSituation() {
        switch (economicSituation) {
            case "muy_baja":
                return "Muy Baja - Sin ingresos fijos";
            case "baja":
                return "Baja - Ingresos menores a S/500";
            case "media_baja":
                return "Media Baja - Ingresos S/500-S/1000";
            case "media":
                return "Media - Ingresos S/1000-S/1500";
            case "media_alta":
                return "Media Alta - Ingresos S/1500-S/3000";
            case "alta":
                return "Alta - Ingresos mayores a S/3000";
            default:
                return economicSituation != null ? economicSituation : "No especificado";
        }
    }

    public String getFormattedVerificationStatus() {
        switch (verificationStatus) {
            case "pending":
                return "Pendiente";
            case "verified":
                return "Verificado";
            case "rejected":
                return "Rechazado";
            case "in_review":
                return "En Revisión";
            default:
                return verificationStatus != null ? verificationStatus : "Pendiente";
        }
    }

    public String getFormattedHousingSituation() {
        switch (housingSituation) {
            case "own":
                return "Casa propia";
            case "rented":
                return "Alquilada";
            case "borrowed":
                return "Prestada";
            case "precarious":
                return "Vivienda precaria";
            default:
                return housingSituation != null ? housingSituation : "No especificado";
        }
    }

    public boolean hasSpecialNeeds() {
        return specialNeeds != null && !specialNeeds.trim().isEmpty();
    }

    public boolean isUrgent() {
        return "muy_baja".equals(economicSituation) && 
               (children > 0 || elderly > 0) && 
               receivedDonations == 0;
    }

    public int calculatePriorityScore() {
        int score = 0;
        
        // Economic situation
        if ("muy_baja".equals(economicSituation)) score += 10;
        else if ("baja".equals(economicSituation)) score += 7;
        else if ("media_baja".equals(economicSituation)) score += 4;
        
        // Family composition
        score += children * 2;
        score += elderly * 3;
        
        // Special needs
        if (hasSpecialNeeds()) score += 5;
        
        // Housing situation
        if ("precarious".equals(housingSituation)) score += 4;
        else if ("borrowed".equals(housingSituation)) score += 2;
        
        // Previous help
        score -= receivedDonations; // Less priority if already received help
        
        return Math.max(0, score);
    }

    @Override
    public String toString() {
        return "Receiver{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", familySize=" + familySize +
                ", children=" + children +
                ", economicSituation='" + economicSituation + '\'' +
                ", needs=" + needs +
                ", verified=" + verified +
                ", receivedDonations=" + receivedDonations +
                ", priorityScore=" + calculatePriorityScore() +
                '}';
    }
}