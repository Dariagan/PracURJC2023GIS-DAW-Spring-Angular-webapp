package es.codeurjc.nexusapp.controller.deprecated;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.repository.TweetRepository;
import es.codeurjc.nexusapp.service.UserService;
import es.codeurjc.nexusapp.utilities.OptTwo;

// All methods/functionality programmed entirely by group 13 A
@Controller
public class UserInteractionController {

    @Autowired
    UserService userService;
    @Autowired
    TweetRepository tweetRepository;

    @RequestMapping("/u/{username}/follow")
    public String handleFollow(
        @PathVariable String username, HttpServletRequest req
    ) {
        OptTwo<User> users = userService.getUserBy(username, req);
        
        if (UserService.isVisitorAuthenticated(users)){
            
            if (UserService.urlUserExists(users) && !UserService.isSelfAction(users)){
            
                users.getRight().toggleFollow(users.getLeft());

                userService.save(users.getLeft()).save(users.getRight());

                return UserService.redirectToReferer(req);
            } else 
                return "error";
        }   
        else return "redirect:/login";
    }

    @RequestMapping("/u/{username}/ban")
    public String handleBan(
        @PathVariable String username, HttpServletRequest req
    ) {
        OptTwo<User> users = userService.getUserBy(username, req);
        
        if (UserService.isVisitorAuthenticated(users) &&
            UserService.urlUserExists(users) && !UserService.isSelfAction(users)) {
            
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

    @RequestMapping("/u/{username}/block")
    public String handleBlock(
        @PathVariable String username, HttpServletRequest req
    ) {
        OptTwo<User> users = userService.getUserBy(username, req);
        
        if (UserService.isVisitorAuthenticated(users) &&
            UserService.urlUserExists(users) && !UserService.isSelfAction(users)) {
            
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

    @RequestMapping("/u/{username}/delete")
    public String deleteUser(
        @PathVariable String username, HttpServletRequest req
    ) {
        OptTwo<User> users = userService.getUserBy(username, req);
        
        if (UserService.isVisitorAuthenticated(users) &&
            UserService.urlUserExists(users) &&
            (UserService.isVisitorAdmin(users) || UserService.isSelfAction(users))) {

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
}
