package com.security.authentication.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SignupRequestDTO {

    @NotNull(message = "Email is required")
    @Email(message = "Provide a valid email")
    private String email;

    @NotNull(message = "Password is required")
    private String password;

//    @Size(max = 100,message = "User name cannot exceed 100 characters")
//    private String userName;
    


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
}
