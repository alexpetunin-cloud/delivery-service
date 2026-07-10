package com.petunincloud.delivery.service.deliveries.courier;

import jakarta.persistence.*;

@Entity
@Table(name = "couriers")
public class CourierEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;

    @Enumerated(EnumType.STRING)
    private CourierStatus status;

    public CourierEntity() {
    }

    public CourierEntity(Long id, String name, String phone, CourierStatus status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public CourierStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatus(CourierStatus status) {
        this.status = status;
    }
}
