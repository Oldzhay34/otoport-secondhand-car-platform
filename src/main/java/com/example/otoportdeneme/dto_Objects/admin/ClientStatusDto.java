package com.example.otoportdeneme.dto_Objects.admin;

import com.example.otoportdeneme.Enums.AccountStatus;

public class ClientStatusDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private AccountStatus status;

    public ClientStatusDto() {}

    public ClientStatusDto(Long id, String firstName, String lastName, String email, AccountStatus status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
}
