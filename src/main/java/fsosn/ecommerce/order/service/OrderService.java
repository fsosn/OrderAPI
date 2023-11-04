package fsosn.ecommerce.order.service;

import fsosn.ecommerce.order.exception.OrderNotFoundException;
import fsosn.ecommerce.order.model.Order;
import fsosn.ecommerce.order.repository.OrderRepository;
import fsosn.ecommerce.product.model.Product;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order getOrderById(Long id) {
        validateId(id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Page<Order> getAllOrders(int page, int size) {
        PageRequest pr = PageRequest.of(page, size);
        return orderRepository.findAll(pr);
    }

    public List<Product> getProductListById(Long id) {
        validateId(id);
        return orderRepository.getOrderProductListById(id);
    }

    public Order createOrder(@Valid Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrder(Long id, @Valid Order updatedOrder) {
        validateId(id);
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(updatedOrder.getStatus());
                    order.setCustomerId(updatedOrder.getCustomerId());
                    order.setOrderDate(updatedOrder.getOrderDate());
                    order.setAddress(updatedOrder.getAddress());
                    order.setProductList(updatedOrder.getProductList());
                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public void deleteOrder(Long id) {
        validateId(id);
        orderRepository.findById(id).ifPresentOrElse(
                orderRepository::delete, () -> {
                    throw new OrderNotFoundException(id);
                }
        );
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Order ID cannot be null.");
        }
    }
}