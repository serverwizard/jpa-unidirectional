package entity;

import annotation.JoinColumn;
import annotation.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private Long id;

    private String productName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
