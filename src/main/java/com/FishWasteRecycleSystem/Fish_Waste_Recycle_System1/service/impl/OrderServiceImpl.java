//////package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.impl;
//////
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.exception.ResourceNotFoundException;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.exception.BadRequestException;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.OrderDto;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.OrderRequestDto;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Order;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Requirement;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Seller;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.WasteListing;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.FishWasteStatus;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.OrderStatus;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.RequirementStatus;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.OrderRepository;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.RequirementRepository;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.SellerRepository;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.WasteListingRepository;
//////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.OrderService;
//////import jakarta.transaction.Transactional;
//////import lombok.RequiredArgsConstructor;
//////import org.modelmapper.ModelMapper;
//////import org.springframework.stereotype.Service;
//////
//////import java.util.List;
//////import java.util.Map;
//////import java.math.BigDecimal;
//////import java.time.LocalDate;
//////
//////@Service
//////@RequiredArgsConstructor
//////public class OrderServiceImpl implements OrderService {
//////
//////    private final OrderRepository orderRepository;
//////    private final RequirementRepository requirementRepository;
//////    private final WasteListingRepository wasteListingRepository;
//////    private final SellerRepository sellerRepository;
//////    private final ModelMapper modelMapper;
//////
//////    @Override
//////    public List<OrderDto> getAllOrders()
//////    {
//////         return orderRepository.findAll()
//////                 .stream()
//////                 .map(order-> {
//////                     OrderDto dto = modelMapper.map(order, OrderDto.class);
//////                     dto.setWasteListingId(order.getWasteListing().getWasteListingId());
//////                     dto.setRequirementId(order.getRequirement().getRequirementId());
//////                     return dto;
//////                 }).toList();
//////    }
//////
//////    @Override
//////    public OrderDto getOrderById(Long orderId){
//////            Order order=orderRepository.findById(orderId).
//////                    orElseThrow(() ->
//////                new ResourceNotFoundException("Order not found with id: " + orderId));
//////            OrderDto dto=modelMapper.map(order,OrderDto.class);
//////            dto.setWasteListingId(order.getWasteListing().getWasteListingId());
//////            dto.setRequirementId(order.getRequirement().getRequirementId());
//////
//////            return  dto;
//////    }
//////
////////    @Override
////////    @Transactional
////////    public OrderDto createNewOrder(OrderRequestDto orderRequestDto)
////////    {
////////        WasteListing listing=wasteListingRepository.findById(orderRequestDto.getListingId())
////////                .orElseThrow(() ->
////////                        new ResourceNotFoundException("Listing not found"));
////////        Requirement requirement=requirementRepository.findById(orderRequestDto.getRequirementId()).
////////                orElseThrow(() ->
////////                        new ResourceNotFoundException("Requirement not found"));
////////
////////        if (orderRequestDto.getOrderQuantity().compareTo(listing.getQuantity()) > 0) {
////////            throw new BadRequestException("Ordered quantity exceeds available quantity");
////////        }
////////
////////
////////        Order neworder=new Order();
////////        neworder.setOrderQuantity(orderRequestDto.getOrderQuantity());
////////        neworder.setTotalAmount(
////////                listing.getPricePerKg().multiply(orderRequestDto.getOrderQuantity())
////////        );
////////        neworder.setPickupDate(orderRequestDto.getPickupDate());
////////        neworder.setStatus(OrderStatus.PENDING);
////////        neworder.setWasteListing(listing);
////////        neworder.setRequirement(requirement);
////////
////////        Order savedOrder=orderRepository.save(neworder);
////////        // =========================
////////        // 1. Update Waste Listing
////////        // =========================
////////        listing.setQuantity(
////////                listing.getQuantity().subtract(orderRequestDto.getOrderQuantity())
////////        );
////////
////////        if (listing.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
////////            listing.setStatus(FishWasteStatus.SOLD);
////////        } else {
////////            listing.setStatus(FishWasteStatus.AVAILABLE);
////////        }
////////
////////        wasteListingRepository.save(listing);
////////
////////        // =========================
////////        // 2. Update Requirement
////////        // =========================
//////////        requirement.setQuantity(
//////////                requirement.getQuantity() - orderRequestDto.getOrderQuantity().doubleValue()
//////////        );
//////////
//////////        if (requirement.getQuantity() <= 0) {
//////////            requirement.setQuantity(0.0);
//////////            requirement.setStatus(RequirementStatus.FULFILLED);
//////////        } else {
//////////            requirement.setStatus(RequirementStatus.OPEN);
//////////        }
//////////
//////////        requirementRepository.save(requirement);
//////////
//////////        // =========================
//////////        // 3. Update Seller
//////////        // =========================
//////////        Seller seller = listing.getSeller();
//////////
//////////        seller.setAvailableFishWasteKg(
//////////                seller.getAvailableFishWasteKg()
//////////                        - orderRequestDto.getOrderQuantity().intValue()
//////////        );
//////////
//////////        sellerRepository.save(seller);
//////////        OrderDto dto=modelMapper.map(savedOrder,OrderDto.class);
//////////        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
//////////        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
//////////        return  dto;
////////
////////
////////    }
//////
//////    @Override
//////    @Transactional
//////    public OrderDto createNewOrder(OrderRequestDto orderRequestDto) {
//////
//////        WasteListing listing = wasteListingRepository
//////                .findById(orderRequestDto.getListingId())
//////                .orElseThrow(() ->
//////                        new ResourceNotFoundException("Listing not found"));
//////
//////        Requirement requirement = requirementRepository
//////                .findById(orderRequestDto.getRequirementId())
//////                .orElseThrow(() ->
//////                        new ResourceNotFoundException("Requirement not found"));
//////
//////        // Check listing quantity
//////        if (orderRequestDto.getOrderQuantity()
//////                .compareTo(listing.getQuantity()) > 0) {
//////
//////            throw new BadRequestException(
//////                    "Ordered quantity exceeds available quantity");
//////        }
//////
//////        // Create Order
//////        Order newOrder = new Order();
//////
//////        newOrder.setOrderQuantity(orderRequestDto.getOrderQuantity());
//////
//////        newOrder.setTotalAmount(
//////                listing.getPricePerKg()
//////                        .multiply(orderRequestDto.getOrderQuantity())
//////        );
//////
//////        newOrder.setPickupDate(orderRequestDto.getPickupDate());
//////
//////        // Initially PENDING
//////        newOrder.setStatus(OrderStatus.PENDING);
//////
//////        newOrder.setWasteListing(listing);
//////        newOrder.setRequirement(requirement);
//////
//////
//////        // Save Order
//////        Order savedOrder = orderRepository.save(newOrder);
//////
//////        // IMPORTANT:
//////        // Quantity will NOT be reduced here.
//////        // Seller will reduce quantity when accepting the order.
//////// नवीन ऑर्डर आल्यावर लिस्टिंग 'RESERVED' करा
//////        listing.setStatus(FishWasteStatus.RESERVED);
//////        wasteListingRepository.save(listing);
//////        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
//////
//////        dto.setWasteListingId(
//////                savedOrder.getWasteListing().getWasteListingId()
//////        );
//////
//////        dto.setRequirementId(
//////                savedOrder.getRequirement().getRequirementId()
//////        );
//////
//////        return dto;
//////    }
//////
//////    @Override
//////    @Transactional
//////    public OrderDto acceptOrder(Long orderId) {
//////
//////        Order order = orderRepository.findById(orderId)
//////                .orElseThrow(() ->
//////                        new ResourceNotFoundException(
//////                                "Order not found with id: " + orderId));
//////
//////        if (order.getStatus() != OrderStatus.PENDING) {
//////            throw new BadRequestException(
//////                    "Only pending orders can be accepted");
//////        }
//////
//////        WasteListing listing = order.getWasteListing();
//////        Requirement requirement = order.getRequirement();
//////        Seller seller = listing.getSeller();
//////
//////        BigDecimal orderQuantity = order.getOrderQuantity();
//////
//////        // Check listing quantity
//////        if (orderQuantity.compareTo(listing.getQuantity()) > 0) {
//////            throw new BadRequestException(
//////                    "Insufficient waste quantity available");
//////        }
//////
//////        // Check requirement quantity
//////        if (orderQuantity.doubleValue() > requirement.getQuantity()) {
//////            throw new BadRequestException(
//////                    "Order quantity exceeds requirement quantity");
//////        }
//////
//////        // 1. Minus listing quantity
//////        listing.setQuantity(
//////                listing.getQuantity().subtract(orderQuantity)
//////        );
//////
//////        if (listing.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
//////            listing.setStatus(FishWasteStatus.SOLD);
//////        } else {
//////            listing.setStatus(FishWasteStatus.AVAILABLE);
//////        }
//////
//////        // 2. Minus requirement quantity
//////        requirement.setQuantity(
//////                requirement.getQuantity()
//////                        - orderQuantity.doubleValue()
//////        );
//////
//////        if (requirement.getQuantity() <= 0) {
//////            requirement.setQuantity(0.0);
//////            requirement.setStatus(RequirementStatus.FULFILLED);
//////        } else {
//////            requirement.setStatus(RequirementStatus.OPEN);
//////        }
//////
//////        // 3. Minus seller available waste
//////        seller.setAvailableFishWasteKg(
//////                seller.getAvailableFishWasteKg()
//////                        - orderQuantity.intValue()
//////        );
//////
//////        // 4. Accept order
//////        order.setStatus(OrderStatus.ACCEPTED);
//////
//////        wasteListingRepository.save(listing);
//////        requirementRepository.save(requirement);
//////        sellerRepository.save(seller);
//////
//////        Order savedOrder = orderRepository.save(order);
//////
//////        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
//////        dto.setWasteListingId(
//////                savedOrder.getWasteListing().getWasteListingId()
//////        );
//////        dto.setRequirementId(
//////                savedOrder.getRequirement().getRequirementId()
//////        );
//////
//////        return dto;
//////    }
//////
//////    @Override
//////    public void deleteOrderById(Long orderId){
//////
//////        if (!orderRepository.existsById(orderId)) {
//////            throw new ResourceNotFoundException("Order not found with id: " + orderId);
//////        }
//////
//////
//////      orderRepository.deleteById(orderId);
//////
//////
//////    }
//////
//////    @Override
//////    public OrderDto updateOrder(Long orderId,OrderRequestDto orderRequestDto) {
//////        Order order = orderRepository.findById(orderId)
//////                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
//////        WasteListing listing = wasteListingRepository.findById(orderRequestDto.getListingId()).
//////                orElseThrow(() ->  new ResourceNotFoundException("Listing not found"));
//////
//////        Requirement requirement = requirementRepository.findById(orderRequestDto.getRequirementId()).
//////                orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));
//////
//////        if (orderRequestDto.getOrderQuantity().compareTo(listing.getQuantity()) > 0) {
//////            throw new BadRequestException("Ordered quantity exceeds available quantity");
//////        }
//////
//////
//////        order.setOrderQuantity(orderRequestDto.getOrderQuantity());
//////        order.setTotalAmount(
//////                listing.getPricePerKg().multiply(orderRequestDto.getOrderQuantity())
//////        );
//////        order.setPickupDate(orderRequestDto.getPickupDate());
//////
//////        order.setWasteListing(listing);
//////        order.setRequirement(requirement);
//////
//////        Order savedOrder = orderRepository.save(order);
//////        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
//////        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
//////        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
//////        return dto;
//////
//////    }
//////
//////
//////
//////    @Override
//////    public OrderDto updatePartialOrder(Long orderId, Map<String, Object> updates) {
//////
//////        Order order = orderRepository.findById(orderId)
//////                .orElseThrow(() ->
//////                        new ResourceNotFoundException("Order not found with id: " + orderId));
//////        updates.forEach((field, value) -> {
//////
//////            switch (field) {
//////
//////                case "orderQuantity": {
//////
//////                    BigDecimal quantity = new BigDecimal(value.toString());
//////
//////                    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
//////                        throw new BadRequestException("Order quantity must be greater than 0.");
//////                    }
//////
//////                    WasteListing listing = order.getWasteListing();
//////
//////                    if (quantity.compareTo(listing.getQuantity()) > 0) {
//////                        throw new BadRequestException("Ordered quantity exceeds available quantity.");
//////                    }
//////
//////                    order.setOrderQuantity(quantity);
//////
//////                    order.setTotalAmount(
//////                            listing.getPricePerKg().multiply(quantity)
//////                    );
//////
//////                    break;
//////                }
//////                case "pickupDate": {
//////                    LocalDate pickupDate = LocalDate.parse(value.toString());
//////
//////                    if (pickupDate.isBefore(LocalDate.now())) {
//////                        throw new BadRequestException("Pickup date cannot be in the past.");
//////                    }
//////
//////                    order.setPickupDate(pickupDate);
//////                    break;
//////                }
//////
//////                case "status":
//////                    order.setStatus(OrderStatus.valueOf(value.toString()));
//////                    break;
//////
//////                case "listingId":
//////
//////                    WasteListing wasteListing = wasteListingRepository
//////                            .findById(Long.valueOf(value.toString()))
//////                            .orElseThrow(() ->
//////                                    new ResourceNotFoundException("Listing not found"));
//////                    order.setWasteListing(wasteListing);
//////
//////                    order.setTotalAmount(
//////                            wasteListing.getPricePerKg()
//////                                    .multiply(order.getOrderQuantity())
//////                    );
//////                    break;
//////
//////                case "requirementId":
//////
//////                    Requirement requirement = requirementRepository
//////                            .findById(Long.valueOf(value.toString()))
//////                            .orElseThrow(() ->
//////                                    new ResourceNotFoundException("Requirement not found"));
//////
//////                    order.setRequirement(requirement);
//////                    break;
//////
//////                default:
//////                    throw new BadRequestException("Invalid field: " + field);
//////            }
//////        });
//////
//////        Order savedOrder = orderRepository.save(order);
//////
//////        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
//////
//////        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
//////        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
//////
//////        return dto;
//////    }
//////
//////    @Override
//////    public OrderDto cancelOrder(Long orderId) {
//////
//////        Order order = orderRepository.findById(orderId)
//////                .orElseThrow(() ->
//////                           new ResourceNotFoundException("Order not found"));
//////
//////        if (order.getStatus() == OrderStatus.CANCELLED) {
//////            throw new BadRequestException("Order is already cancelled");
//////        }
//////
//////        WasteListing listing = order.getWasteListing();
//////        Requirement requirement = order.getRequirement();
//////        Seller seller = listing.getSeller();
//////
//////        // Restore Listing Quantity
//////        listing.setQuantity(
//////                listing.getQuantity().add(order.getOrderQuantity())
//////        );
//////
//////        // Restore Listing Status
//////        if (listing.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
//////            listing.setStatus(FishWasteStatus.AVAILABLE);
//////        }
//////
//////        // Restore Seller Available Waste
//////        seller.setAvailableFishWasteKg(
//////                seller.getAvailableFishWasteKg()
//////                        + order.getOrderQuantity().intValue()
//////        );
//////
//////        // Restore Requirement Quantity
//////        requirement.setQuantity(
//////                requirement.getQuantity()
//////                        + order.getOrderQuantity().doubleValue()
//////        );
//////
//////        // Restore Requirement Status
//////        if (requirement.getQuantity() > 0) {
//////            requirement.setStatus(RequirementStatus.OPEN);
//////        }
//////
//////        // Cancel Order
//////        order.setStatus(OrderStatus.CANCELLED);
//////
//////        wasteListingRepository.save(listing);
//////        requirementRepository.save(requirement);
//////        orderRepository.save(order);
//////
//////        OrderDto dto = modelMapper.map(order, OrderDto.class);
//////        dto.setWasteListingId(listing.getWasteListingId());
//////        dto.setRequirementId(requirement.getRequirementId());
//////
//////        return dto;
//////    }
//////
//////    @Override
//////    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
//////
//////        Order order = orderRepository.findById(orderId)
//////                        .orElseThrow(() ->
//////                                new ResourceNotFoundException("Order not found"));
//////
//////        order.setStatus(status);
//////
//////        Order savedOrder = orderRepository.save(order);
//////
//////        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
//////
//////        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
//////        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
//////
//////        return dto;
//////    }
//////}
////
////
////
////package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.impl;
////
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.exception.ResourceNotFoundException;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.exception.BadRequestException;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.OrderDto;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.OrderRequestDto;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Order;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Requirement;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Seller;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.WasteListing;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.FishWasteStatus;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.OrderStatus;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.RequirementStatus;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.OrderRepository;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.RequirementRepository;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.SellerRepository;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.WasteListingRepository;
////import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.OrderService;
////import jakarta.transaction.Transactional;
////import lombok.RequiredArgsConstructor;
////import org.modelmapper.ModelMapper;
////import org.springframework.stereotype.Service;
////
////import java.util.List;
////import java.util.Map;
////import java.math.BigDecimal;
////import java.time.LocalDate;
////
////@Service
////@RequiredArgsConstructor
////public class OrderServiceImpl implements OrderService {
////
////    private final OrderRepository orderRepository;
////    private final RequirementRepository requirementRepository;
////    private final WasteListingRepository wasteListingRepository;
////    private final SellerRepository sellerRepository;
////    private final ModelMapper modelMapper;
////
////    @Override
////    public List<OrderDto> getAllOrders() {
////        return orderRepository.findAll()
////                .stream()
////                .map(order-> {
////                    OrderDto dto = modelMapper.map(order, OrderDto.class);
////                    dto.setWasteListingId(order.getWasteListing().getWasteListingId());
////                    dto.setRequirementId(order.getRequirement().getRequirementId());
////                    return dto;
////                }).toList();
////    }
////
////    @Override
////    public OrderDto getOrderById(Long orderId) {
////        Order order = orderRepository.findById(orderId).
////                orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
////        OrderDto dto = modelMapper.map(order, OrderDto.class);
////        dto.setWasteListingId(order.getWasteListing().getWasteListingId());
////        dto.setRequirementId(order.getRequirement().getRequirementId());
////
////        return dto;
////    }
////
////    @Override
////    @Transactional
////    public OrderDto createNewOrder(OrderRequestDto orderRequestDto) {
////
////        WasteListing listing = wasteListingRepository
////                .findById(orderRequestDto.getListingId())
////                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
////
////        Requirement requirement = requirementRepository
////                .findById(orderRequestDto.getRequirementId())
////                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));
////
////        // Check listing quantity
////        if (orderRequestDto.getOrderQuantity().compareTo(listing.getQuantity()) > 0) {
////            throw new BadRequestException("Ordered quantity exceeds available quantity");
////        }
////
////        // Create Order
////        Order newOrder = new Order();
////        newOrder.setOrderQuantity(orderRequestDto.getOrderQuantity());
////        newOrder.setTotalAmount(
////                listing.getPricePerKg().multiply(orderRequestDto.getOrderQuantity())
////        );
////        newOrder.setPickupDate(orderRequestDto.getPickupDate());
////        newOrder.setStatus(OrderStatus.PENDING);
////        newOrder.setWasteListing(listing);
////        newOrder.setRequirement(requirement);
////
////        // Save Order
////        Order savedOrder = orderRepository.save(newOrder);
////
////        // नवीन ऑर्डर आल्यावर लिस्टिंग 'RESERVED' करा जेणेकरून डॅशबोर्डवर काउंट येईल
////        listing.setStatus(FishWasteStatus.RESERVED);
////        wasteListingRepository.save(listing);
////
////        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
////        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
////        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
////
////        return dto;
////    }
////
////    @Override
////    @Transactional
////    public OrderDto acceptOrder(Long orderId) {
////
////        Order order = orderRepository.findById(orderId)
////                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
////
////        if (order.getStatus() != OrderStatus.PENDING) {
////            throw new BadRequestException("Only pending orders can be accepted");
////        }
////
////        WasteListing listing = order.getWasteListing();
////        Requirement requirement = order.getRequirement();
////        Seller seller = listing.getSeller();
////
////        BigDecimal orderQuantity = order.getOrderQuantity();
////
////        if (orderQuantity.compareTo(listing.getQuantity()) > 0) {
////            throw new BadRequestException("Insufficient waste quantity available");
////        }
////
////        if (orderQuantity.doubleValue() > requirement.getQuantity()) {
////            throw new BadRequestException("Order quantity exceeds requirement quantity");
////        }
////
////        // 1. Minus listing quantity
////        listing.setQuantity(
////                listing.getQuantity().subtract(orderQuantity)
////        );
////
////        if (listing.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
////            listing.setStatus(FishWasteStatus.SOLD);
////        } else {
////            listing.setStatus(FishWasteStatus.AVAILABLE);
////        }
////
////        // 2. Minus requirement quantity
////        requirement.setQuantity(
////                requirement.getQuantity() - orderQuantity.doubleValue()
////        );
////
////        if (requirement.getQuantity() <= 0) {
////            requirement.setQuantity(0.0);
////            requirement.setStatus(RequirementStatus.FULFILLED);
////        } else {
////            requirement.setStatus(RequirementStatus.OPEN);
////        }
////
////        // 3. Minus seller available waste
////        seller.setAvailableFishWasteKg(
////                seller.getAvailableFishWasteKg() - orderQuantity.intValue()
////        );
////
////        // 4. Accept order
////        order.setStatus(OrderStatus.ACCEPTED);
////
////        wasteListingRepository.save(listing);
////        requirementRepository.save(requirement);
////        sellerRepository.save(seller);
////
////        Order savedOrder = orderRepository.save(order);
////
////        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
////        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
////        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
////
////        return dto;
////    }
////
////    @Override
////    public void deleteOrderById(Long orderId) {
////        if (!orderRepository.existsById(orderId)) {
////            throw new ResourceNotFoundException("Order not found with id: " + orderId);
////        }
////        orderRepository.deleteById(orderId);
////    }
////
////    @Override
////    public OrderDto updateOrder(Long orderId, OrderRequestDto orderRequestDto) {
////        Order order = orderRepository.findById(orderId)
////                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
////        WasteListing listing = wasteListingRepository.findById(orderRequestDto.getListingId())
////                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
////
////        Requirement requirement = requirementRepository.findById(orderRequestDto.getRequirementId())
////                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));
////
////        if (orderRequestDto.getOrderQuantity().compareTo(listing.getQuantity()) > 0) {
////            throw new BadRequestException("Ordered quantity exceeds available quantity");
////        }
////
////        order.setOrderQuantity(orderRequestDto.getOrderQuantity());
////        order.setTotalAmount(
////                listing.getPricePerKg().multiply(orderRequestDto.getOrderQuantity())
////        );
////        order.setPickupDate(orderRequestDto.getPickupDate());
////        order.setWasteListing(listing);
////        order.setRequirement(requirement);
////
////        Order savedOrder = orderRepository.save(order);
////        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
////        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
////        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
////        return dto;
////    }
////
////    @Override
////    public OrderDto updatePartialOrder(Long orderId, Map<String, Object> updates) {
////        Order order = orderRepository.findById(orderId)
////                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
////
////        updates.forEach((field, value) -> {
////            switch (field) {
////                case "orderQuantity": {
////                    BigDecimal quantity = new BigDecimal(value.toString());
////                    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
////                        throw new BadRequestException("Order quantity must be greater than 0.");
////                    }
////                    WasteListing listing = order.getWasteListing();
////                    if (quantity.compareTo(listing.getQuantity()) > 0) {
////                        throw new BadRequestException("Ordered quantity exceeds available quantity.");
////                    }
////                    order.setOrderQuantity(quantity);
////                    order.setTotalAmount(listing.getPricePerKg().multiply(quantity));
////                    break;
////                }
////                case "pickupDate": {
////                    LocalDate pickupDate = LocalDate.parse(value.toString());
////                    if (pickupDate.isBefore(LocalDate.now())) {
////                        throw new BadRequestException("Pickup date cannot be in the past.");
////                    }
////                    order.setPickupDate(pickupDate);
////                    break;
////                }
////                case "status":
////                    order.setStatus(OrderStatus.valueOf(value.toString()));
////                    break;
////                case "listingId":
////                    WasteListing wasteListing = wasteListingRepository
////                            .findById(Long.valueOf(value.toString()))
////                            .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
////                    order.setWasteListing(wasteListing);
////                    order.setTotalAmount(wasteListing.getPricePerKg().multiply(order.getOrderQuantity()));
////                    break;
////                case "requirementId":
////                    Requirement requirement = requirementRepository
////                            .findById(Long.valueOf(value.toString()))
////                            .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));
////                    order.setRequirement(requirement);
////                    break;
////                default:
////                    throw new BadRequestException("Invalid field: " + field);
////            }
////        });
////
////        Order savedOrder = orderRepository.save(order);
////        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
////        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
////        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
////
////        return dto;
////    }
////
////    @Override
////    @Transactional
////    public OrderDto cancelOrder(Long orderId) {
////        Order order = orderRepository.findById(orderId)
////                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
////
////        if (order.getStatus() == OrderStatus.CANCELLED) {
////            throw new BadRequestException("Order is already cancelled");
////        }
////
////        WasteListing listing = order.getWasteListing();
////        Requirement requirement = order.getRequirement();
////        Seller seller = listing.getSeller();
////
////        // Restore Listing Quantity
////        listing.setQuantity(
////                listing.getQuantity().add(order.getOrderQuantity())
////        );
////
////        // Restore Listing Status to AVAILABLE
////        if (listing.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
////            listing.setStatus(FishWasteStatus.AVAILABLE);
////        }
////
////        // Restore Seller Available Waste
////        seller.setAvailableFishWasteKg(
////                seller.getAvailableFishWasteKg() + order.getOrderQuantity().intValue()
////        );
////
////        // Restore Requirement Quantity
////        requirement.setQuantity(
////                requirement.getQuantity() + order.getOrderQuantity().doubleValue()
////        );
////
////        if (requirement.getQuantity() > 0) {
////            requirement.setStatus(RequirementStatus.OPEN);
////        }
////
////        // Cancel Order
////        order.setStatus(OrderStatus.CANCELLED);
////
////        wasteListingRepository.save(listing);
////        requirementRepository.save(requirement);
////        orderRepository.save(order);
////
////        OrderDto dto = modelMapper.map(order, OrderDto.class);
////        dto.setWasteListingId(listing.getWasteListingId());
////        dto.setRequirementId(requirement.getRequirementId());
////
////        return dto;
////    }
////
////    @Override
////    @Transactional
////    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
////        Order order = orderRepository.findById(orderId)
////                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
////
////        order.setStatus(status);
////
////        // Order complete ya accept zali ki listing SOLD keli pahije
////        if (status == OrderStatus.ACCEPTED || status == OrderStatus.COMPLETED) {
////            WasteListing listing = order.getWasteListing();
////            listing.setStatus(FishWasteStatus.SOLD);
////            wasteListingRepository.save(listing);
////        }
////
////        Order savedOrder = orderRepository.save(order);
////
////        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
////        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
////        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
////
////        return dto;
////    }
////}
//
//
//package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.impl;
//
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.exception.ResourceNotFoundException;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.exception.BadRequestException;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.OrderDto;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.OrderRequestDto;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Order;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Requirement;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Seller;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.WasteListing;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.FishWasteStatus;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.OrderStatus;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.RequirementStatus;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.OrderRepository;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.RequirementRepository;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.SellerRepository;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.WasteListingRepository;
//import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.OrderService;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//@Service
//@RequiredArgsConstructor
//public class OrderServiceImpl implements OrderService {
//
//    private final OrderRepository orderRepository;
//    private final RequirementRepository requirementRepository;
//    private final WasteListingRepository wasteListingRepository;
//    private final SellerRepository sellerRepository;
//    private final ModelMapper modelMapper;
//
//    @Override
//    public List<OrderDto> getAllOrders() {
//        return orderRepository.findAll()
//                .stream()
//                .map(order -> {
//                    OrderDto dto = modelMapper.map(order, OrderDto.class);
//                    dto.setWasteListingId(order.getWasteListing().getWasteListingId());
//                    dto.setRequirementId(order.getRequirement().getRequirementId());
//                    return dto;
//                }).toList();
//    }
//
//    @Override
//    public OrderDto getOrderById(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
//        OrderDto dto = modelMapper.map(order, OrderDto.class);
//        dto.setWasteListingId(order.getWasteListing().getWasteListingId());
//        dto.setRequirementId(order.getRequirement().getRequirementId());
//
//        return dto;
//    }
//
//    @Override
//    @Transactional
//    public OrderDto createNewOrder(OrderRequestDto orderRequestDto) {
//
//        WasteListing listing = wasteListingRepository
//                .findById(orderRequestDto.getListingId())
//                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
//
//        Requirement requirement = requirementRepository
//                .findById(orderRequestDto.getRequirementId())
//                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));
//
//        if (orderRequestDto.getOrderQuantity().compareTo(listing.getQuantity()) > 0) {
//            throw new BadRequestException("Ordered quantity exceeds available quantity");
//        }
//
//        Order newOrder = new Order();
//        newOrder.setOrderQuantity(orderRequestDto.getOrderQuantity());
//        newOrder.setTotalAmount(
//                listing.getPricePerKg().multiply(orderRequestDto.getOrderQuantity())
//        );
//        newOrder.setPickupDate(orderRequestDto.getPickupDate());
//        newOrder.setStatus(OrderStatus.PENDING);
//        newOrder.setWasteListing(listing);
//        newOrder.setRequirement(requirement);
//
//        Order savedOrder = orderRepository.save(newOrder);
//
//        listing.setStatus(FishWasteStatus.RESERVED);
//        wasteListingRepository.save(listing);
//
//        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
//        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
//        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
//
//        return dto;
//    }
//
//    @Override
//    @Transactional
//    public OrderDto acceptOrder(Long orderId) {
//
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
//
//        if (order.getStatus() != OrderStatus.PENDING) {
//            throw new BadRequestException("Only pending orders can be accepted");
//        }
//
//        WasteListing listing = order.getWasteListing();
//        Requirement requirement = order.getRequirement();
//        Seller seller = listing.getSeller();
//
//        BigDecimal orderQuantity = order.getOrderQuantity();
//
//        if (orderQuantity.compareTo(listing.getQuantity()) > 0) {
//            throw new BadRequestException("Insufficient waste quantity available");
//        }
//
//        if (orderQuantity.doubleValue() > requirement.getQuantity()) {
//            throw new BadRequestException("Order quantity exceeds requirement quantity");
//        }
//
//        // 1. Minus listing quantity
//        listing.setQuantity(
//                listing.getQuantity().subtract(orderQuantity)
//        );
//
//        if (listing.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
//            listing.setStatus(FishWasteStatus.SOLD);
//        } else {
//            listing.setStatus(FishWasteStatus.AVAILABLE);
//        }
//
//        // 2. Minus requirement quantity
//        requirement.setQuantity(
//                requirement.getQuantity() - orderQuantity.doubleValue()
//        );
//
//        if (requirement.getQuantity() <= 0) {
//            requirement.setQuantity(0.0);
//            requirement.setStatus(RequirementStatus.FULFILLED);
//        } else {
//            requirement.setStatus(RequirementStatus.OPEN);
//        }
//
//        // 3. Minus seller available waste (Ensure non-negative logic)
//        int currentSellerWaste = seller.getAvailableFishWasteKg() != null ? seller.getAvailableFishWasteKg() : 0;
//        int updatedSellerWaste = currentSellerWaste - orderQuantity.intValue();
//        seller.setAvailableFishWasteKg(Math.max(0, updatedSellerWaste));
//
//        // 4. Accept order
//        order.setStatus(OrderStatus.ACCEPTED);
//
//        wasteListingRepository.save(listing);
//        requirementRepository.save(requirement);
//        sellerRepository.save(seller);
//
//        Order savedOrder = orderRepository.save(order);
//
//        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
//        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
//        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
//
//        return dto;
//    }
//
//    @Override
//    public void deleteOrderById(Long orderId) {
//        if (!orderRepository.existsById(orderId)) {
//            throw new ResourceNotFoundException("Order not found with id: " + orderId);
//        }
//        orderRepository.deleteById(orderId);
//    }
//
//    @Override
//    public OrderDto updateOrder(Long orderId, OrderRequestDto orderRequestDto) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
//        WasteListing listing = wasteListingRepository.findById(orderRequestDto.getListingId())
//                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
//
//        Requirement requirement = requirementRepository.findById(orderRequestDto.getRequirementId())
//                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));
//
//        if (orderRequestDto.getOrderQuantity().compareTo(listing.getQuantity()) > 0) {
//            throw new BadRequestException("Ordered quantity exceeds available quantity");
//        }
//
//        order.setOrderQuantity(orderRequestDto.getOrderQuantity());
//        order.setTotalAmount(
//                listing.getPricePerKg().multiply(orderRequestDto.getOrderQuantity())
//        );
//        order.setPickupDate(orderRequestDto.getPickupDate());
//        order.setWasteListing(listing);
//        order.setRequirement(requirement);
//
//        Order savedOrder = orderRepository.save(order);
//        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
//        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
//        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
//        return dto;
//    }
//
//    @Override
//    public OrderDto updatePartialOrder(Long orderId, Map<String, Object> updates) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
//
//        updates.forEach((field, value) -> {
//            switch (field) {
//                case "orderQuantity": {
//                    BigDecimal quantity = new BigDecimal(value.toString());
//                    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
//                        throw new BadRequestException("Order quantity must be greater than 0.");
//                    }
//                    WasteListing listing = order.getWasteListing();
//                    if (quantity.compareTo(listing.getQuantity()) > 0) {
//                        throw new BadRequestException("Ordered quantity exceeds available quantity.");
//                    }
//                    order.setOrderQuantity(quantity);
//                    order.setTotalAmount(listing.getPricePerKg().multiply(quantity));
//                    break;
//                }
//                case "pickupDate": {
//                    LocalDate pickupDate = LocalDate.parse(value.toString());
//                    if (pickupDate.isBefore(LocalDate.now())) {
//                        throw new BadRequestException("Pickup date cannot be in the past.");
//                    }
//                    order.setPickupDate(pickupDate);
//                    break;
//                }
//                case "status":
//                    order.setStatus(OrderStatus.valueOf(value.toString()));
//                    break;
//                case "listingId":
//                    WasteListing wasteListing = wasteListingRepository
//                            .findById(Long.valueOf(value.toString()))
//                            .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
//                    order.setWasteListing(wasteListing);
//                    order.setTotalAmount(wasteListing.getPricePerKg().multiply(order.getOrderQuantity()));
//                    break;
//                case "requirementId":
//                    Requirement requirement = requirementRepository
//                            .findById(Long.valueOf(value.toString()))
//                            .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));
//                    order.setRequirement(requirement);
//                    break;
//                default:
//                    throw new BadRequestException("Invalid field: " + field);
//            }
//        });
//
//        Order savedOrder = orderRepository.save(order);
//        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
//        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
//        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
//
//        return dto;
//    }
//
//    @Override
//    @Transactional
//    public OrderDto cancelOrder(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
//
//        if (order.getStatus() == OrderStatus.CANCELLED) {
//            throw new BadRequestException("Order is already cancelled");
//        }
//
//        WasteListing listing = order.getWasteListing();
//        Requirement requirement = order.getRequirement();
//        Seller seller = listing.getSeller();
//
//        // Restore Listing Quantity
//        listing.setQuantity(
//                listing.getQuantity().add(order.getOrderQuantity())
//        );
//
//        // Restore Listing Status to AVAILABLE
//        if (listing.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
//            listing.setStatus(FishWasteStatus.AVAILABLE);
//        }
//
//        // Restore Seller Available Waste (with Null Safety)
//        int currentSellerWaste = seller.getAvailableFishWasteKg() != null ? seller.getAvailableFishWasteKg() : 0;
//        seller.setAvailableFishWasteKg(
//                currentSellerWaste + order.getOrderQuantity().intValue()
//        );
//
//        // Restore Requirement Quantity
//        requirement.setQuantity(
//                requirement.getQuantity() + order.getOrderQuantity().doubleValue()
//        );
//
//        // Restore Requirement Status
//        if (requirement.getQuantity() > 0) {
//            requirement.setStatus(RequirementStatus.OPEN);
//        }
//
//        // Cancel Order
//        order.setStatus(OrderStatus.CANCELLED);
//
//        wasteListingRepository.save(listing);
//        requirementRepository.save(requirement);
//        sellerRepository.save(seller);
//        orderRepository.save(order);
//
//        OrderDto dto = modelMapper.map(order, OrderDto.class);
//        dto.setWasteListingId(listing.getWasteListingId());
//        dto.setRequirementId(requirement.getRequirementId());
//
//        return dto;
//    }
//
//    @Override
//    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
//
//        order.setStatus(status);
//
//        Order savedOrder = orderRepository.save(order);
//
//        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
//        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
//        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
//
//        return dto;
//    }
//}


package com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.impl;

import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.exception.ResourceNotFoundException;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.exception.BadRequestException;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.OrderDto;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.dto.OrderRequestDto;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Order;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Requirement;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.Seller;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.entity.WasteListing;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.FishWasteStatus;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.OrderStatus;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.enums.RequirementStatus;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.OrderRepository;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.RequirementRepository;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.SellerRepository;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.repository.WasteListingRepository;
import com.FishWasteRecycleSystem.Fish_Waste_Recycle_System1.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RequirementRepository requirementRepository;
    private final WasteListingRepository wasteListingRepository;
    private final SellerRepository sellerRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> {
                    OrderDto dto = modelMapper.map(order, OrderDto.class);
                    dto.setWasteListingId(order.getWasteListing().getWasteListingId());
                    dto.setRequirementId(order.getRequirement().getRequirementId());
                    return dto;
                }).toList();
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        OrderDto dto = modelMapper.map(order, OrderDto.class);
        dto.setWasteListingId(order.getWasteListing().getWasteListingId());
        dto.setRequirementId(order.getRequirement().getRequirementId());

        return dto;
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(OrderRequestDto orderRequestDto) {

        WasteListing listing = wasteListingRepository
                .findById(orderRequestDto.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        Requirement requirement = requirementRepository
                .findById(orderRequestDto.getRequirementId())
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));

        if (orderRequestDto.getOrderQuantity().compareTo(listing.getQuantity()) > 0) {
            throw new BadRequestException("Ordered quantity exceeds available quantity");
        }

        Order newOrder = new Order();
        newOrder.setOrderQuantity(orderRequestDto.getOrderQuantity());
        newOrder.setTotalAmount(
                listing.getPricePerKg().multiply(orderRequestDto.getOrderQuantity())
        );
        newOrder.setPickupDate(orderRequestDto.getPickupDate());
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setWasteListing(listing);
        newOrder.setRequirement(requirement);

        Order savedOrder = orderRepository.save(newOrder);

        listing.setStatus(FishWasteStatus.RESERVED);
        wasteListingRepository.save(listing);

        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());

        return dto;
    }

    @Override
    @Transactional
    public OrderDto acceptOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only pending orders can be accepted");
        }

        WasteListing listing = order.getWasteListing();
        Requirement requirement = order.getRequirement();
        Seller seller = listing.getSeller();

        BigDecimal orderQuantity = order.getOrderQuantity();

        if (orderQuantity.compareTo(listing.getQuantity()) > 0) {
            throw new BadRequestException("Insufficient waste quantity available");
        }

        if (orderQuantity.doubleValue() > requirement.getQuantity()) {
            throw new BadRequestException("Order quantity exceeds requirement quantity");
        }

        // 1. Listing quantity कमी करा
        listing.setQuantity(
                listing.getQuantity().subtract(orderQuantity)
        );

        if (listing.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
            listing.setStatus(FishWasteStatus.SOLD);
        } else {
            listing.setStatus(FishWasteStatus.AVAILABLE);
        }

        // 2. Requirement quantity कमी करा
        requirement.setQuantity(
                requirement.getQuantity() - orderQuantity.doubleValue()
        );

        if (requirement.getQuantity() <= 0) {
            requirement.setQuantity(0.0);
            requirement.setStatus(RequirementStatus.FULFILLED);
        } else {
            requirement.setStatus(RequirementStatus.OPEN);
        }

        // 3. Seller available waste (0 पेक्षा खाली जाऊ नये म्हणून Math.max)
        int currentSellerWaste = seller.getAvailableFishWasteKg() != null ? seller.getAvailableFishWasteKg() : 0;
        int updatedSellerWaste = currentSellerWaste - orderQuantity.intValue();
        seller.setAvailableFishWasteKg(Math.max(0, updatedSellerWaste));

        // 4. Status ACCEPTED करा
        order.setStatus(OrderStatus.ACCEPTED);

        wasteListingRepository.save(listing);
        requirementRepository.save(requirement);
        sellerRepository.save(seller);

        Order savedOrder = orderRepository.save(order);

        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());

        return dto;
    }

    @Override
    public void deleteOrderById(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    @Override
    public OrderDto updateOrder(Long orderId, OrderRequestDto orderRequestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        WasteListing listing = wasteListingRepository.findById(orderRequestDto.getListingId())
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        Requirement requirement = requirementRepository.findById(orderRequestDto.getRequirementId())
                .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));

        if (orderRequestDto.getOrderQuantity().compareTo(listing.getQuantity()) > 0) {
            throw new BadRequestException("Ordered quantity exceeds available quantity");
        }

        order.setOrderQuantity(orderRequestDto.getOrderQuantity());
        order.setTotalAmount(
                listing.getPricePerKg().multiply(orderRequestDto.getOrderQuantity())
        );
        order.setPickupDate(orderRequestDto.getPickupDate());
        order.setWasteListing(listing);
        order.setRequirement(requirement);

        Order savedOrder = orderRepository.save(order);
        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());
        return dto;
    }

    @Override
    public OrderDto updatePartialOrder(Long orderId, Map<String, Object> updates) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        updates.forEach((field, value) -> {
            switch (field) {
                case "orderQuantity": {
                    BigDecimal quantity = new BigDecimal(value.toString());
                    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BadRequestException("Order quantity must be greater than 0.");
                    }
                    WasteListing listing = order.getWasteListing();
                    if (quantity.compareTo(listing.getQuantity()) > 0) {
                        throw new BadRequestException("Ordered quantity exceeds available quantity.");
                    }
                    order.setOrderQuantity(quantity);
                    order.setTotalAmount(listing.getPricePerKg().multiply(quantity));
                    break;
                }
                case "pickupDate": {
                    LocalDate pickupDate = LocalDate.parse(value.toString());
                    if (pickupDate.isBefore(LocalDate.now())) {
                        throw new BadRequestException("Pickup date cannot be in the past.");
                    }
                    order.setPickupDate(pickupDate);
                    break;
                }
                case "status":
                    order.setStatus(OrderStatus.valueOf(value.toString()));
                    break;
                case "listingId":
                    WasteListing wasteListing = wasteListingRepository
                            .findById(Long.valueOf(value.toString()))
                            .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
                    order.setWasteListing(wasteListing);
                    order.setTotalAmount(wasteListing.getPricePerKg().multiply(order.getOrderQuantity()));
                    break;
                case "requirementId":
                    Requirement requirement = requirementRepository
                            .findById(Long.valueOf(value.toString()))
                            .orElseThrow(() -> new ResourceNotFoundException("Requirement not found"));
                    order.setRequirement(requirement);
                    break;
                default:
                    throw new BadRequestException("Field '" + field + "' is not supported");
            }
        });

        Order savedOrder = orderRepository.save(order);
        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());

        return dto;
    }

    @Override
    @Transactional
    public OrderDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }

        WasteListing listing = order.getWasteListing();
        Requirement requirement = order.getRequirement();
        Seller seller = listing.getSeller();

        // Restore Listing Quantity
        listing.setQuantity(
                listing.getQuantity().add(order.getOrderQuantity())
        );

        if (listing.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
            listing.setStatus(FishWasteStatus.AVAILABLE);
        }

        // Restore Seller Available Waste
        int currentSellerWaste = seller.getAvailableFishWasteKg() != null ? seller.getAvailableFishWasteKg() : 0;
        seller.setAvailableFishWasteKg(
                currentSellerWaste + order.getOrderQuantity().intValue()
        );

        // Restore Requirement Quantity
        requirement.setQuantity(
                requirement.getQuantity() + order.getOrderQuantity().doubleValue()
        );

        if (requirement.getQuantity() > 0) {
            requirement.setStatus(RequirementStatus.OPEN);
        }

        // Cancel Order
        order.setStatus(OrderStatus.CANCELLED);

        wasteListingRepository.save(listing);
        requirementRepository.save(requirement);
        sellerRepository.save(seller);
        orderRepository.save(order);

        OrderDto dto = modelMapper.map(order, OrderDto.class);
        dto.setWasteListingId(listing.getWasteListingId());
        dto.setRequirementId(requirement.getRequirementId());

        return dto;
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(status);

        Order savedOrder = orderRepository.save(order);

        OrderDto dto = modelMapper.map(savedOrder, OrderDto.class);
        dto.setWasteListingId(savedOrder.getWasteListing().getWasteListingId());
        dto.setRequirementId(savedOrder.getRequirement().getRequirementId());

        return dto;
    }
}