package pl.edu.pk.gamelibrary.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminCreateUserRequest {

    @NotBlank(message = "Username jest wymagany")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
    private String password;

    /** "USER" albo "ADMIN" (opcjonalnie, domyślnie USER). */
    private String role;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

