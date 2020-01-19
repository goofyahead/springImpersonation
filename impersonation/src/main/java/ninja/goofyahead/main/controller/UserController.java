package ninja.goofyahead.main.controller;

import com.fasterxml.jackson.annotation.JsonView;
import ninja.goofyahead.main.annotations.AllowImpersonation;
import ninja.goofyahead.main.model.User;
import ninja.goofyahead.main.views.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/{id}")
    @AllowImpersonation
    @JsonView(View.Public.class)
    public User getUser(@PathVariable int id) {
        return new User(3, "Alex", "Vidal", "TribalScale");
    }

    @PostMapping
    public User saveUser(@JsonView(View.Public.class) @RequestBody User user){
        log.info(user.toString());
        return user;
    }
}
