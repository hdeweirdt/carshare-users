package be.harm.carshare.users.user;

import be.harm.carshare.users.security.authentication.AuthenticatedUser;
import be.harm.carshare.users.security.authentication.token.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Set;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("users")
public class UserRestController {
    private final UserService userService;
    private final TokenService tokenService;

    public UserRestController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping("")
    public ResponseEntity<Set<User>> getUsers() {
        return ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ok(userService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }


    @PutMapping("/{id}")
    @PreAuthorize("#user.getId() == #id")
    public ResponseEntity<String> updateUser(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable Long id,
            @RequestBody User updatedUser,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        var savedUser = userService.updateUser(updatedUser);

        return ok(ServletUriComponentsBuilder
                .fromContextPath(request)
                .path("users/{id}")
                .buildAndExpand(savedUser.getId().toString())
                .toUri().toString());

    }

    @PostMapping("")
    public ResponseEntity<String> registerUser(
            @Valid @RequestBody User user,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            var savedUser = userService.saveUser(user);

            return created(ServletUriComponentsBuilder
                    .fromContextPath(request)
                    .path("users/{id}")
                    .buildAndExpand(savedUser.getId().toString())
                    .toUri())
                    .build();
        }
    }

    @RequestMapping("/verify")
    /*
     * Checks if the given [token] is currently valid.
     */
    public ResponseEntity<?> verify(@RequestParam String token) {
        if (tokenService.verify(token).isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
