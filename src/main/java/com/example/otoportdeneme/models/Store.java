package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.Enums.RecipientType;
import com.example.otoportdeneme.Interfaces.AuditableActor;
import com.example.otoportdeneme.Interfaces.NotifiableRecipient;
import jakarta.persistence.*;

@Entity
@Table(
        name = "stores",
        indexes = {
                @Index(name = "ix_store_name", columnList = "storeName"),
                @Index(name = "ix_store_city", columnList = "city"),
                @Index(name = "ix_store_building", columnList = "building_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ux_store_building_shop",
                        columnNames = {"building_id", "shopNo"}
                )
        }
)
public class Store extends UserAccount
        implements AuditableActor, NotifiableRecipient {

    @Column(nullable = false, length = 140)
    private String storeName;

    @Column(length = 80)
    private String authorizedPerson;

    @Column(length = 40, unique = true)
    private String taxNo;

    @Column(length = 120)
    private String website;

    @Column(length = 60)
    private String city;

    @Column(length = 255)
    private String logoUrl;

    @Column(length = 60)
    private String district;

    @Column(length = 255)
    private String addressLine;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private Integer listingLimit = 50;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    private Integer floor;
    @Column(length = 20)
    private String phone;

    @Column(length = 20)
    private String shopNo;

    @Column(length = 255)
    private String directionNote;

    public Store() {}


    @Override
    public ActorType getActorType() {
        return ActorType.STORE;
    }

    @Override
    public RecipientType getRecipientType() {
        return RecipientType.STORE;
    }

    // ===== getters / setters (mevcutların aynısı) =====
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public void setAuthorizedPerson(String authorizedPerson) {
        this.authorizedPerson = authorizedPerson;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public void setDirectionNote(String directionNote) {
        this.directionNote = directionNote;
    }

    public void setPhone(String phone) {
        this.phone = phone;

    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public Integer getFloor() {
        return floor;
    }

    public String getShopNo() {
        return shopNo;
    }

    public String getDirectionNote() {
        return directionNote;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }


    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    @Transient
    public String getPassword() { return getPasswordHash(); }

}
