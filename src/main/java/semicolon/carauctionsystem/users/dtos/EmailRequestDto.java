package semicolon.carauctionsystem.users.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.File;

@Data
public class EmailRequestDto {

    @NotBlank(message = "Receiver email is required")
    @Email(message = "Receiver email must be valid")
    private String to;
    private String subject;
    @NotBlank(message = "body is required")
    private String body;
    private File attachment;

}
