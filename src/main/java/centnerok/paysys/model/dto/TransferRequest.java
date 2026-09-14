package centnerok.paysys.model.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferRequest(
    @NotNull 
    Long senderAccountId,

    @NotNull 
    Long recipientAccountId,

    @NotNull 
    @Positive 
    BigDecimal amount
) {

}
