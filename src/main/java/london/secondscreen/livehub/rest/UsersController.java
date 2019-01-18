package london.secondscreen.livehub.rest;

import london.secondscreen.livehub.InvalidParamsException;
import london.secondscreen.livehub.models.User;
import london.secondscreen.livehub.service.UserService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public Mono<User> user(@PathVariable Long userId) {
        return userService.findOne(userId).switchIfEmpty(Mono.defer(this::raiseNotFound));
    }

    private <T> Mono<T> raiseNotFound() {
        return Mono.error(new InvalidParamsException("Not found"));
    }
}
