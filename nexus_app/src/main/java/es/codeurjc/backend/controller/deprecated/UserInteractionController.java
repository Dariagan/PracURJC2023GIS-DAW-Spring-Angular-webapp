package es.codeurjc.backend.controller.deprecated;

import javax.servlet.http.HttpServletRequest;

import es.codeurjc.backend.repository.TweetRepository;
import es.codeurjc.backend.service.FollowingService;
import es.codeurjc.backend.service.UserQuerierService;
import es.codeurjc.backend.utilities.OptTwo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.service.UserService;

// All methods/functionality programmed entirely by group 13 A
@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInteractionController {

    private final UserService userService;
    private final TweetRepository tweetRepository;
    private final UserQuerierService userQuerierService;
    private final FollowingService followingService;

    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
        @RequestParam int page, @RequestParam int size
    ) {
        return userQuerierService.getUsers(page, size);
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<Void> handleFollow(
        Authentication auth, @PathVariable String username
    ) {
        return followingService.follow(auth.getName(), username);
    }

    @GetMapping("/{username}/follow")
    public ResponseEntity<Page<User>> getFollowers(
        @PathVariable String username,
        @RequestParam int page,
        @RequestParam int size
    ) {
        return followingService.getFollowersOf(
            username, PageRequest.of(page, size)
        );
    }

    /*
    @RequestMapping("/u/{username}/ban")
    public String handleBan(
        @PathVariable String username, HttpServletRequest req
    ) {
        OptTwo<User> users = userService.getUserBy(username, req);
        
        if (UserService.isVisitorAuthenticated(users) &&
            UserService.urlUserExistsAndNotSelfAction(users)) {
            
            User bannedUser = users.getLeft();
            
            if (!bannedUser.isBanned())
                bannedUser.ban();
            else
                bannedUser.unban();

            userService.save(bannedUser);

            return UserService.redirectToReferer(req);
    
        }   
        return "error";
    }
     */

    /*
    @RequestMapping("/u/{username}/block")
    public String handleBlock(
        @PathVariable String username, HttpServletRequest req
    ) {
        OptTwo<User> users = userService.getUserBy(username, req);
        
        if (UserService.isVisitorAuthenticated(users) &&
            UserService.urlUserExistsAndNotSelfAction(users)) {
            
            User blockingUser = users.getRight();
            User blockedUser = users.getLeft();
            
            if (!blockingUser.getBlocked().contains(blockedUser))
                blockingUser.block(blockedUser);
            else
                blockingUser.unblock(blockedUser);

            userService.save(blockingUser);

            return UserService.redirectToReferer(req);
    
        }   
        return "error";
    }
     */

    /*
    @RequestMapping("/u/{username}/delete")
    public String deleteUser(
        @PathVariable String username, HttpServletRequest req
    ) {
        OptTwo<User> users = userService.getUserBy(username, req);
        
        if (UserService.isVisitorAuthenticated(users) &&
            UserService.urlUserExists(users) &&
            (UserService.isVisitorAdmin(users)||UserService.isSelfAction(users))) {

            User deletedUser = users.getLeft();

            for (Tweet tweet: deletedUser.getTweets()){
                tweet.setNullAuthor();
                tweetRepository.save(tweet);
            }

            // FIXME handle all SQL foreign keys which reference the user 
            // (either delete entities which are referencing the deleted user or set the user F.K. to null)
            userService.delete(deletedUser);

            return UserService.redirectToReferer(req);
        }   
        return "error";
    }
     */
}
