package com.donaciones.models;

import java.util.Date;

public class User {
    private int id;
    private String username;
    private String password;
    private String userType; // admin, empleado, donador, receptor, usuario
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String dni;
    private Date birthDate;
    private String region;
    private String district;
    private String address;
    private Date registrationDate;
    private boolean active;
    private boolean notificationsEnabled;
    private Integer rolId;
    
    // NUEVO CAMPO PARA IMAGEN DE PERFIL
    private String profileImage;

    // Constructors
    public User() {
        this.registrationDate = new Date();
        this.active = true;
        this.notificationsEnabled = true;
        this.profileImage = "/images/default-profile.png"; // Imagen por defecto
    }

    public User(String username, String password, String userType) {
        this();
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    public User(int id, String username, String password, String userType, 
                String firstName, String lastName, String email, String phone, 
                String dni, Date birthDate, String region, String district, 
                String address, Date registrationDate, boolean active, 
                boolean notificationsEnabled, Integer rolId, String profileImage) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dni = dni;
        this.birthDate = birthDate;
        this.region = region;
        this.district = district;
        this.address = address;
        this.registrationDate = registrationDate;
        this.active = active;
        this.notificationsEnabled = notificationsEnabled;
        this.rolId = rolId;
        this.profileImage = profileImage != null ? profileImage : "/images/default-profile.png";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public Integer getRolId() {
        return rolId;
    }

    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }
    
    // NUEVOS GETTERS Y SETTERS PARA IMAGEN DE PERFIL
    public String getProfileImage() { 
        if (profileImage == null || profileImage.trim().isEmpty()) {
            return "/images/default-profile.png";
        }
        return profileImage; 
    }
    
    public void setProfileImage(String profileImage) { 
        this.profileImage = profileImage; 
    }
    
    public boolean hasCustomProfileImage() {
        return profileImage != null && !profileImage.trim().isEmpty() && 
               !profileImage.equals("/images/default-profile.png");
    }

    // Utility methods
    public String getFullName() {
        if (firstName == null && lastName == null) return username;
        if (firstName == null) return lastName;
        if (lastName == null) return firstName;
        return firstName + " " + lastName;
    }

    public String getFormattedUserType() {
        if (userType == null) return "Usuario";
        
        switch (userType.toLowerCase()) {
            case "admin":
                return "Administrador";
            case "empleado":
                return "Empleado";
            case "donador":
                return "Donador";
            case "receptor":
                return "Receptor";
            case "usuario":
                return "Usuario Regular";
            default:
                return userType;
        }
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(userType);
    }

    public boolean isEmployee() {
        return "empleado".equalsIgnoreCase(userType);
    }

    public boolean isDonor() {
        return "donador".equalsIgnoreCase(userType);
    }

    public boolean isReceiver() {
        return "receptor".equalsIgnoreCase(userType);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", userType='" + userType + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", region='" + region + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}