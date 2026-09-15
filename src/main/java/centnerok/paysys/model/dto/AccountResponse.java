package centnerok.paysys.model.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
    Long id,
    BigDecimal balance,
    Instant createdAt
) {

}
