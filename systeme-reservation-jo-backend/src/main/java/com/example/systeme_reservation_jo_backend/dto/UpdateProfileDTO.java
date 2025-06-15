package com.example.systeme_reservation_jo_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateProfileDTO {

    @NotBlank @Email
    private String email;

    @NotBlank
    private String username;

    /**
     * Mot de passe actuel : obligatoire si on fournit newPassword
     */
    private String currentPassword;

    /**
     * Nouveau mot de passe ; si null ou vide, on ne le modifie pas
     */
    private String newPassword;

    // getters & setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
