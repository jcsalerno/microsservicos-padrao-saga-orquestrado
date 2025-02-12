package br.com.microservices.orchestrated.paymentservice.core.model;
import br.com.microservices.orchestrated.paymentservice.core.enums.EPaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private int totalItems;

    @Column(nullable = false)
    private double totalAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EPaymentStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt;

    @Column(nullable = false)
    private LocalDateTime updateAt;

    @PrePersist
    public void prePersist() {
        var now = LocalDateTime.now();
        createAt = now;
        updateAt = now;
        status = EPaymentStatus.PENDING;
    }

    @PrePersist
    public void preUpdate() {
        updateAt = LocalDateTime.now();
    }
}
