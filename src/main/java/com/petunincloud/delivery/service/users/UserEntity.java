package com.petunincloud.delivery.service.users;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "apartment")
    private String apartment; // Квартира/офис

    @Column(name = "delivery_instructions")
    private String deliveryInstructions; // Домофон, этаж, ориентиры

    public UserEntity() {
    }

    public UserEntity(
            Long id,
            String email,
            String phone,
            String name,
            String address,
            String apartment,
            String deliveryInstructions
    ) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.apartment = apartment;
        this.deliveryInstructions = deliveryInstructions;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getApartment() {
        return apartment;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }
}
