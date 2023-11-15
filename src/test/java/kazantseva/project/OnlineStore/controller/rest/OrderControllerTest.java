package kazantseva.project.OnlineStore.controller.rest;

import kazantseva.project.OnlineStore.model.entity.Customer;
import kazantseva.project.OnlineStore.model.entity.Order;
import kazantseva.project.OnlineStore.model.entity.enums.Status;
import kazantseva.project.OnlineStore.model.entity.enums.Type;
import kazantseva.project.OnlineStore.model.response.ShortOrderDTO;
import kazantseva.project.OnlineStore.service.OrderService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


class OrderControllerTest {

    private static OrderController cut;
    private static OrderService orderService;
    private static Authentication authentication;
    private static Page<ShortOrderDTO> orders;
    private static Pageable pageable;

    @BeforeAll
    static void initialize() {
        orderService = Mockito.mock(OrderService.class);

        authentication = Mockito.mock(Authentication.class);

        cut = new OrderController(orderService);

        pageable = Pageable.ofSize(5);

        orders = new PageImpl<>(List.of(
                new ShortOrderDTO(
                        new Order(5L, LocalDateTime.now(), Type.PUBLISHED, Status.UNPAID,
                                "address", "description", BigDecimal.ONE,
                                Customer.builder().build(), List.of()))), pageable, 4);
    }


    static Stream<Arguments> provideCorrectParameters() {
        return Stream.of(
                Arguments.arguments(
                        authentication,
                        2,
                        pageable
                ));
    }

    @ParameterizedTest
    @MethodSource("provideCorrectParameters")
    void shouldGetOrders(Authentication auth,
                         long customerId,
                         Pageable pageable) throws Exception {
        when(orderService.getOrders(auth.getName(), customerId, pageable)).thenReturn(orders);

        Page<ShortOrderDTO> actual = cut.getOrders(auth, customerId, pageable);

        assertEquals(orders, actual);
    }

}
