package centnerok.paysys.model.dto;

import java.math.BigDecimal;
import java.time.Instant;

import centnerok.paysys.model.enums.TransactionStatus;
import centnerok.paysys.model.enums.TransactionType;

public record TransactionResponse(
    TransactionType type,

    TransactionStatus status,

    BigDecimal amount,

    Instant createdAt
) {

}
