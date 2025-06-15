package com.example.systeme_reservation_jo_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UpdateProfileDTO {

    @NotBlank @Email
    private String email;

    @NotBlank
    private String username;

    /**
     * Ancien mot de passe, obligatoire si on veut changer le mot de passe
     */
    private String currentPassword;

    /**
     * Nouveau mot de passe ; si vide ou absent, on conserve l’ancien
     */
    private String newPassword;

    // getters / setters
    public String getEmail()                       { return email; }
    public void setEmail(String email)             { this.email = email; }
    public String getUsername()                    { return username; }
    public void setUsername(String username)       { this.username = username; }
    public String getCurrentPassword()             { return currentPassword; }
    public void setCurrentPassword(String pwd)     { this.currentPassword = pwd; }
    public String getNewPassword()                 { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
