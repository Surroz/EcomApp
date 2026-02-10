package org.surro.ecomapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.surro.ecomapp.model.Order;

import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {
    public Optional<Order> findByOrderId(String orderId);
}
