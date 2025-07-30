package gift.service;

import gift.dto.OrderRequestDTO;
import gift.dto.OrderResponseDTO;
import gift.entity.Order;
import gift.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        Order order = new Order(
                orderRequestDTO.optionId(),
                orderRequestDTO.quantity(),
                orderRequestDTO.message()
        );

        Order created = orderRepository.save(order);

        return new OrderResponseDTO(
                created.getId(),
                created.getOptionId(),
                created.getQuantity(),
                created.getOrderDateTime(),
                created.getMessage()
        );
    }
}
