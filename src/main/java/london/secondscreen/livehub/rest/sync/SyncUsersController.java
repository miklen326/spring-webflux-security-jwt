package london.secondscreen.livehub.rest.sync;

import london.secondscreen.livehub.models.User;
import london.secondscreen.livehub.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync/api/user")
public class SyncUsersController {
    private final UserService userService;

    public SyncUsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public User user(@PathVariable Long userId) {
        return userService.findSyncOne(userId);
    }
}
