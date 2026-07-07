package com.petunincloud.delivery.service.restaurants.restaurant;

import com.petunincloud.delivery.service.restaurants.dish.DishEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "restaurants")
@Entity
public class RestaurantEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DishEntity> menu = new ArrayList<>();

    public RestaurantEntity() {
    }

    public RestaurantEntity(Long id, String name, String address, List<DishEntity> menu) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.menu = menu;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<DishEntity> getMenu() {
        return menu;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMenu(List<DishEntity> menu) {
        this.menu = menu;
    }
}
