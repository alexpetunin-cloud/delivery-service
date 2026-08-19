package com.petunincloud.delivery.service.deliveries.courier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CourierRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourierRepository courierRepository;

    @BeforeEach
    void setUp() {
        createCourier("Михаил", "+79001234567", CourierStatus.AVAILABLE);
        createCourier("Леонид", "+79231454567", CourierStatus.BUSY);
        createCourier("Андрей", "+79131634923", CourierStatus.AVAILABLE);

        entityManager.flush();
        entityManager.clear();
    }

    private void createCourier(
            String name,
            String phone,
            CourierStatus status
    ) {
        CourierEntity courier = new CourierEntity();

        courier.setName(name);
        courier.setPhone(phone);
        courier.setStatus(status);

        entityManager.persist(courier);
    }

    @Test
    void searchAllByFilter_ShouldReturnCourierByName() {
        Pageable pageable = PageRequest.of(0, 5);

        List<CourierEntity> couriers = courierRepository.searchAllByFilter(
                "Михаил",
                null,
                null,
                pageable
        );

        assertThat(couriers).hasSize(1);
        assertThat(couriers).allMatch(
                courier -> courier.getName().equals("Михаил"));
    }

    @Test
    void searchAllByFilter_ShouldReturnCourierByPhone() {
        Pageable pageable = PageRequest.of(0, 5);

        List<CourierEntity> couriers = courierRepository.searchAllByFilter(
                null,
                "+79231454567",
                null,
                pageable
        );

        assertThat(couriers).hasSize(1);
        assertThat(couriers).allMatch(
                courier -> courier.getPhone().equals("+79231454567"));
    }

    @Test
    void searchAllByFilter_ShouldReturnCourierByStatus() {
        Pageable pageable = PageRequest.of(0, 5);

        List<CourierEntity> couriers = courierRepository.searchAllByFilter(
                null,
                null,
                "AVAILABLE",
                pageable
        );

        assertThat(couriers).hasSize(2);
        assertThat(couriers).allMatch(
                courier -> courier.getStatus().equals(CourierStatus.AVAILABLE));
    }

    @Test
    void searchAllByFilter_ShouldApplyPagination() {
        Pageable pageable = PageRequest.of(0, 2);

        List<CourierEntity> couriers = courierRepository.searchAllByFilter(
                null,
                null,
                null,
                pageable
        );

        assertThat(couriers).hasSize(2);
    }

    @Test
    void findTopByStatus_ShouldReturnFirstAvailableCourier() {
        Optional<CourierEntity> courier = courierRepository.findTopByStatus(CourierStatus.AVAILABLE);

        assertThat(courier).isPresent();
        assertThat(courier.get().getStatus()).isEqualTo(CourierStatus.AVAILABLE);
        assertThat(courier.get().getName()).isEqualTo("Михаил");
    }

    @Test
    void findTopByStatus_ShouldReturnEmpty_WhenNoAvailableCouriers() {
        Optional<CourierEntity> courier = courierRepository.findTopByStatus(CourierStatus.OFFLINE);

        assertThat(courier).isEmpty();
    }

    @Test
    void findByPhone_ShouldReturnCourier() {
        String phone = "+79231454567";

        Optional<CourierEntity> courier = courierRepository.findByPhone(phone);

        assertThat(courier).isPresent();
        assertThat(courier.get().getPhone()).isEqualTo(phone);
        assertThat(courier.get().getName()).isEqualTo("Леонид");
    }

    @Test
    void findByPhone_ShouldReturnEmpty_WhenPhoneNotFound() {
        Optional<CourierEntity> courier = courierRepository.findByPhone("+79999999999");

        assertThat(courier).isEmpty();
    }
}
