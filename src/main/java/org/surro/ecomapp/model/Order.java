package org.surro.ecomapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    @Column(unique = true)
    private String orderId;
    private String customerName;
    private String email;
    private java.time.LocalDate orderDate;
    private String status;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<org.surro.ecomapp.model.OrderItem> orderItems;


}
