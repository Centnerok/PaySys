package centnerok.paysys.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
    @NotBlank(message = "first name is mandatory")
    String firstName,

    @NotBlank(message = "last name is mandatory")
    String lastName,

    @NotBlank(message = "email is mandatory")
    @Email(message = "invalid email")
    String email,

    @NotBlank(message = "password is mandatory")
    @Size(min=8, message="password must be at least 8 characters")
    String password
) {

}
