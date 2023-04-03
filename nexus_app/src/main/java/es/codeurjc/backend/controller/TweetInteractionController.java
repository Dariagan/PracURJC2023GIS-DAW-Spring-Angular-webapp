package es.codeurjc.backend.controller;

import java.sql.Blob;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import es.codeurjc.backend.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;


import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.repository.TweetRepository;
import es.codeurjc.backend.service.UserService;

// All methods/functionality programmed entirely by group 13 A
@Controller
public class TweetInteractionController {
    
    @Autowired
    TweetRepository tweetRepository;

    @Autowired
    UserService userService;
    
    @RequestMapping("/tweet/{id}/like")
    public String handleLike(HttpServletRequest req, @PathVariable long id) 
    {
        Optional<User> userOpt = userService.getUserBy(req);
        Optional<Tweet> tweetOpt = tweetRepository.findById(id);

        if (userOpt.isPresent()) 
        {
            if (tweetOpt.isPresent()) 
            {
                tweetOpt.get().switchLike(userOpt.get());
                tweetRepository.save(tweetOpt.get());
            }
            return UserService.redirectToReferer(req);
        }
        else return "redirect:/login";
    }

    @RequestMapping("/tweet/{id}/report")
    public String reportTweet(Model model, HttpServletRequest req, @PathVariable Long id) 
    {
        Optional<User> reportingUserOpt = userService.getUserBy(req);
        Optional<Tweet> tweetOpt = tweetRepository.findById(id);

        if (reportingUserOpt.isPresent())
        {
            if (tweetOpt.isPresent()) 
            {
                Tweet tweet = tweetOpt.get();

                if (TweetService.readIfPostShouldGetdeleted(id))
                    tweetRepository.delete(tweet);
                else {
                    tweet.report(reportingUserOpt.get());     
                    tweetRepository.save(tweet);      
                }
            }
            return UserService.redirectToReferer(req);
        }
        else return "redirect:/login";
    }

    @RequestMapping("/tweet/{id}/delete")
    public String deleteTweet(@PathVariable long id, HttpServletRequest req) 
    {
        Optional<User> deletingUserOpt = userService.getUserBy(req);
        Optional<Tweet> tweetOpt = tweetRepository.findById(id);

        if (tweetOpt.isPresent() && 
        (TweetService.isOwnTweet(tweetOpt.get(), req) ||
        UserService.isAdmin(deletingUserOpt) || 
        TweetService.readIfPostShouldGetdeleted(id))) 
        {
            tweetRepository.delete(tweetOpt.get());
            return "redirect:" + req.getHeader("referer");
        } 
        else return "error";
    }

    @GetMapping("/tweet/{id}/media")
	public ResponseEntity<Resource> downloadImage(@PathVariable long id) 
    {
		Optional<Tweet> tweetOpt = tweetRepository.findById(id);

        Optional<Blob> mediaOpt = Optional.ofNullable(tweetOpt.get().getMedia());
  
        return ResourcesBuilder
            .tryBuildImgResponse(mediaOpt)
            .getOrElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
