package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.Enums.RecipientType;
import com.example.otoportdeneme.Interfaces.AuditableActor;
import com.example.otoportdeneme.Interfaces.NotifiableRecipient;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients", indexes = {
        @Index(name = "ix_client_name", columnList = "firstName,lastName")
})
public class Client extends UserAccount
        implements AuditableActor, NotifiableRecipient {

    @Column(nullable = false, length = 60)
    private String firstName;

    @Column(nullable = false, length = 60)
    private String lastName;

    private LocalDate birthDate;     // opsiyonel

    @Column(nullable = false)
    private Boolean marketingConsent = false;

    @Column(length =20)
    private String phone;

    // ✅ Ters ilişki: Client'ın favorileri
    @OneToMany(
            mappedBy = "client",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Favorite> favorites = new ArrayList<>();

    public Client() {}

    // ===== Interface implementasyonları =====

    @Override
    public ActorType getActorType() {
        return ActorType.CLIENT;
    }

    @Override
    public RecipientType getRecipientType() {
        return RecipientType.CLIENT;
    }

    // ===== getters / setters =====

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Boolean getMarketingConsent() { return marketingConsent; }
    public void setMarketingConsent(Boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

    public List<Favorite> getFavorites() { return favorites; }
    public void setFavorites(List<Favorite> favorites) {
        this.favorites = favorites;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }

}
