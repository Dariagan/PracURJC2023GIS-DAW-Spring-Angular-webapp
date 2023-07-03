package es.codeurjc.nexusapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.utilities.OptTwo;

@Service
@RequiredArgsConstructor
public class FollowingService {

    private final UserService userService;

    public ResponseEntity<Void> follow(
        String followingUser, String followedUser
    ) {
        OptTwo<User> users = userService.getUserBy(followedUser, followingUser);

        if (users.isFull() && !UserService.isSelfAction(users))
        {
            users.getRight().toggleFollow(users.getLeft());
            userService.save(users);
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.badRequest().build();
    }

    // TODO
    public ResponseEntity<Page<User>> getFollowersOf(
        String username, PageRequest page
    ) {
        if (page.getPageSize() > 10)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.badRequest().build();
    }
}
