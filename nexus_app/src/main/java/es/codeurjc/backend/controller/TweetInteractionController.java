package es.codeurjc.backend.controller;

import java.sql.Blob;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import es.codeurjc.backend.service.TweetService;
import es.codeurjc.backend.utilities.OptPair;
import es.codeurjc.backend.utilities.responseentity.ResourcesBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    TweetService tweetService;

    @Autowired
    UserService userService;
    
    @RequestMapping("/tweet/{id}/like")
    public String handleLike(HttpServletRequest request, @PathVariable long id) 
    {
        OptPair<Tweet, User> tweetAndUser = OptPair.of(
            tweetRepository.findById(id), userService.getUserBy(request)
        );

        if (!tweetAndUser.isRight()) return "redirect:/login";
        if (!tweetAndUser.isLeft()) return "error";

        tweetService.switchLike(tweetAndUser);
        return UserService.redirectToReferer(request);
    }

    @RequestMapping("/tweet/{id}/report")
    public String reportTweet(HttpServletRequest request, @PathVariable Long id) 
    {
        OptPair<Tweet, User> tweetAndReportingUser = OptPair.of(
            tweetRepository.findById(id), userService.getUserBy(request)
        );

        if (!tweetAndReportingUser.isRight()) return "redirect:/login";

        if (TweetService.readIfPostShouldGetDeleted(id))
            tweetService.delete(tweetAndReportingUser.getOptLeft());
        else tweetService.report(tweetAndReportingUser);

        return UserService.redirectToReferer(request);
    }

    @RequestMapping("/tweet/{id}/delete")
    public String deleteTweet(@PathVariable long id, HttpServletRequest request) 
    {
        Optional<User> deletingUserOpt = userService.getUserBy(request);
        Optional<Tweet> tweetOpt = tweetRepository.findById(id);

        if (
            TweetService.isOwnTweet(tweetOpt, request) ||
            UserService.isAdmin(deletingUserOpt) ||
            TweetService.readIfPostShouldGetDeleted(id)
        ) {
            tweetService.delete(tweetOpt);
            return "redirect:" + request.getHeader("referer");
        } 
        return "error";
    }

    @GetMapping("/tweet/{id}/media")
	public ResponseEntity<Resource> downloadImage(@PathVariable long id) 
    {
		Optional<Tweet> tweetOpt = tweetRepository.findById(id);
        Optional<Blob> mediaOpt = tweetOpt.map(Tweet::getMedia);

        return ResourcesBuilder
            .tryBuildImgResponse(mediaOpt)
            .getOrElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
