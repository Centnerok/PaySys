package centnerok.paysys.model.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name="accounts")
@Getter
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class Account {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(name="balance", nullable=false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name="created_at", nullable=false, updatable=false)
    private Instant createdAt;

    @PrePersist 
    public void prePersist() {
        createdAt = Instant.now();
    }
}
