package es.codeurjc.nexusapp.controller.deprecated;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.service.TweetService;
import es.codeurjc.nexusapp.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

// All methods/functionality programmed entirely by group 13-A

@Controller
public class FeedController {
    
    @Autowired
    private TweetService tweetService;

    @Autowired
    private UserService userService;

    @RequestMapping("/feed")
    public String showFeed(Model model, HttpServletRequest request)
    {
        Optional<User> loggedUser = userService.getUserBy(request);

        Page<Tweet> tweetsToDisplay;

        if (loggedUser.isPresent() && loggedUser.get().getFollowing().size() > 0)
            tweetsToDisplay = tweetService.getFollowedUsersTweets(loggedUser, PageRequest.of(0, 10));
        else {
            Sort sort = Sort.by("date").descending();
            tweetsToDisplay = tweetService.getAll(PageRequest.of(0, 10, sort));
        }
        updateFeedModel(model, loggedUser, tweetsToDisplay.getContent());

        return "feed";
    }

    @GetMapping("/feed/search")
    public String searchBytags(Model model, HttpServletRequest req, @RequestParam String tags) 
    {
        Optional<User> loggedUser = userService.getUserBy(req);
        Set<String> inputTags = Set.of(tags.split("\\s+"));
        List<Tweet> foundTweets = 
        tweetService.getTweetsByTags(inputTags, PageRequest.of(0, 10)).getContent();
            
        updateFeedModel(model, loggedUser, foundTweets);
        return "feed";
    }

    @RequestMapping("/feed/moderator")
    public String showModFeed(Model model, HttpServletRequest req) 
    {
        Optional<User> loggedUser = userService.getUserBy(req);
        if (loggedUser.isPresent() && loggedUser.get().isAdmin())
        {
            updateFeedModel(model, loggedUser, tweetService.getMostReportedTweets(PageRequest.of(0, 10)));
            return "feed";
        } 
        else return "error";
    }

    @RequestMapping("/tomyprofile")
    public String redirectToProfile(final Model model, HttpServletRequest req) 
    {
        Optional<User> loggedUser = userService.getUserBy(req);

        if (loggedUser.isPresent())
            return "redirect:/u/" + loggedUser.get().getUsername();
        else
            return "error";
    }

    private void updateFeedModel(Model model, Optional<User> loggedUser, List<Tweet> displayedTweets) 
    {
        if (loggedUser.isPresent())
        {
            model.addAttribute("loggedUser", loggedUser.get());
            model.addAttribute("username", loggedUser.get().getUsername());
        } else 
            model.addAttribute("username", "");

        model.addAttribute("authenticated", loggedUser.isPresent());
        model.addAttribute("inLogin", false);
        model.addAttribute("tweets", displayedTweets); 
    }
}
 