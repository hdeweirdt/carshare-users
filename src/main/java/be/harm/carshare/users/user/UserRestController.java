package be.harm.carshare.users.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public UserRestController(UserService userService) {
        this.userService = userService;
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
}
