package gift.service;

import gift.dto.OrderRequestDTO;
import gift.dto.OrderResponseDTO;
import gift.entity.*;
import gift.repository.OptionRepository;
import gift.repository.OrderRepository;
import gift.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final OptionService optionService;
    private final MemberService memberService;
    private final ProductService productService;
    private final OptionRepository optionRepository;
    private final WishlistRepository wishlistRepository;

    public OrderService(OrderRepository orderRepository, OptionService optionService, MemberService memberService, ProductService productService, OptionRepository optionRepository, WishlistRepository wishlistRepository) {
        this.orderRepository = orderRepository;
        this.optionService = optionService;
        this.memberService = memberService;
        this.productService = productService;
        this.optionRepository = optionRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO, Integer memberId) {
        Integer optionId = orderRequestDTO.optionId();
        Integer quantity = orderRequestDTO.quantity();
        String message = orderRequestDTO.message();

        optionService.subtractQuantity(optionId, quantity);

        Order order = new Order(optionId, quantity, message);
        Order created = orderRepository.save(order);

        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다."));
        Integer productId = option.getProduct().getId();

        Member member = memberService.getMemberEntityById(memberId);
        Product product = productService.getEntityById(productId);

        Optional<WishList> wishList = wishlistRepository.findByMemberAndProduct(member, product);
        wishList.ifPresent(wishlistRepository::delete);

        return new OrderResponseDTO(
                created.getId(),
                created.getOptionId(),
                created.getQuantity(),
                created.getOrderDateTime(),
                created.getMessage()
        );
    }
}
