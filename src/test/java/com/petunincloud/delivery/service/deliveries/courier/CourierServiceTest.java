package com.petunincloud.delivery.service.deliveries.courier;

import com.petunincloud.delivery.service.TestSecurityConfig;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierRequest;
import com.petunincloud.delivery.service.deliveries.courier.dto.CourierResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Import(TestSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class CourierServiceTest {

    @Mock
    private CourierMapper courierMapper;

    @Mock
    private CourierRepository courierRepository;

    @InjectMocks
    private CourierService courierService;

    @Test
    void createCourier_ShouldCreateCourier() {
        CourierRequest request = new CourierRequest(
                "Алексей",
                "+77779992345"
        );

        CourierEntity courierEntity = new CourierEntity(
                1L,
                "Алексей",
                "+77779992345",
                CourierStatus.AVAILABLE
        );

        CourierResponse courierResponse = new CourierResponse(
                1L,
                "Алексей",
                "+77779992345",
                CourierStatus.AVAILABLE
        );

        when(courierRepository.findByPhone("+77779992345"))
                .thenReturn(Optional.empty());
        when(courierMapper.toEntity(request))
                .thenReturn(courierEntity);
        when(courierRepository.save(any(CourierEntity.class)))
                .thenReturn(courierEntity);
        when(courierMapper.toResponse(courierEntity))
                .thenReturn(courierResponse);

        CourierResponse result = courierService.createCourier(request);

        assertNotNull(result);
        assertEquals(CourierStatus.AVAILABLE, result.status());

        verify(courierRepository, times(1))
                .findByPhone("+77779992345");
        verify(courierMapper, times(1))
                .toEntity(request);
        verify(courierMapper, times(1))
                .toResponse(courierEntity);
        verify(courierRepository, times(1))
                .save(any(CourierEntity.class));
    }

    @Test
    void createCourier_ShouldThrowException_WithExistsPhoneNumber() {
        CourierRequest request = new CourierRequest(
                "Алексей",
                "+77779992345"
        );

        CourierEntity courierEntity = new CourierEntity(
                300L,
                "Константин",
                "+77779992345",
                CourierStatus.AVAILABLE
        );

        when(courierRepository.findByPhone("+77779992345"))
                .thenReturn(Optional.of(courierEntity));

        assertThrows(IllegalArgumentException.class,
                () -> courierService.createCourier(request));

        verify(courierRepository, never())
                .save(any(CourierEntity.class));
    }

    @Test
    void findAvailableCourier_ShouldReturnAvailableCourier() {
        CourierEntity courierEntity = new CourierEntity(
                1L,
                "Михаил",
                "+77779992345",
                CourierStatus.AVAILABLE
        );

        CourierResponse courierResponse = new CourierResponse(
                1L,
                "Михаил",
                "+77779992345",
                CourierStatus.AVAILABLE
        );

        when(courierRepository.findTopByStatus(CourierStatus.AVAILABLE))
                .thenReturn(Optional.of(courierEntity));
        when(courierMapper.toResponse(courierEntity))
                .thenReturn(courierResponse);

        CourierResponse result = courierService.findAvailableCourier();

        assertNotNull(result);
        assertEquals(CourierStatus.AVAILABLE, result.status());

        verify(courierRepository, times(1))
                .findTopByStatus(CourierStatus.AVAILABLE);
        verify(courierMapper, times(1))
                .toResponse(courierEntity);
    }

    @Test
    void findAvailableCourier_ShouldThrowException_WhenNoAvailableCouriers() {
        when(courierRepository.findTopByStatus(CourierStatus.AVAILABLE))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> courierService.findAvailableCourier());
    }
}
