package be.harm.carshare.users.user;

import be.harm.carshare.users.user.security.ApplicationRole;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class User {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MIN_USERNAME_LENGTH = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    @Column(unique = true)
    @Size(min = 1)
    @NotNull
    private String userName;

    @Getter
    @NotNull
    @Size(min = 8, message = "Password must contain at least " + MIN_PASSWORD_LENGTH + " characters")
    @Pattern.List({
            @Pattern(regexp = ".*\\d.*", message = "Password must contain a number."),
            @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one capital letter")})
    private String password;

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private String drivingLicenseNumber;

    @Getter
    @Setter
    private String telephoneNumber;


    @Getter
    @Setter
    @Convert(converter = ApplicationRole.ApplicationRoleConverter.class)
    private Set<ApplicationRole> roles = new HashSet<>();

    public User(String userName, String password) {
        setUserName(userName);
        setPassword(password);
    }

    public void setUserName(String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("Empty username");
        }
        if (userName.length() < MIN_USERNAME_LENGTH) {
            throw new IllegalArgumentException("Username must be " + MIN_USERNAME_LENGTH + " characters long");
        }
        this.userName = userName;
    }

    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password must be " + MIN_PASSWORD_LENGTH + " characters long");
        }
        if (password.toLowerCase().equals(password)) {
            throw new IllegalArgumentException("Password must contain at least 1 upper case character");
        }
        if (password.toUpperCase().equals(password)) {
            throw new IllegalArgumentException("Password must contain at least 1 lower case character");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least 1 number");
        }
        this.password = password;
    }
}
