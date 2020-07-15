package be.harm.carshare.users.user.commands;

import be.harm.carshare.users.user.User;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Builder
public class CreateUserCommand {
    @Size(min = 1)
    @NotNull
    private String userName;

    @NotNull
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Pattern.List({
            @Pattern(regexp = ".*\\d.*", message = "Password must contain a number."),
            @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one capital letter")})
    private String password;

    private String firstName;

    private String lastName;

    public User mapToUser() {
        return User.builder()
                .userName(userName)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
}
