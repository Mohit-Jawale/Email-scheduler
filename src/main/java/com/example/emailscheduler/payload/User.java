package com.example.emailscheduler.payload;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   
    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;
    
    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String password;




    @ManyToOne
    @JoinColumn(name = "template_id")
    private EmailTemplate emailTemplate;

    public EmailTemplate getEmailTemplate() {
        return emailTemplate;
    }

    public void setEmailTemplate(EmailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
