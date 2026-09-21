package centnerok.paysys.model.dto;

import java.time.Instant;
import java.util.List;

public record ValidationErrorsResponse(
    List<String> errors,

    Instant timestamp
) {

}
