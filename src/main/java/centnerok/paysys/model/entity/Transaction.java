package centnerok.paysys.model.entity;

import java.time.Instant;

import centnerok.paysys.model.enums.TransactionStatus;
import centnerok.paysys.model.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name="transactions")
@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class Transaction {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="type", nullable=false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name="status", nullable=false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name="created_at", nullable=false, updatable=false)
    private Instant createdAt;

    @PrePersist 
    public void prePersist() {
        createdAt = Instant.now();
    }
}
