package es.codeurjc.backend.controller;


import java.io.IOException;
import java.sql.Blob;
import java.util.Collections;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import es.codeurjc.backend.utilities.OptTwo;
import es.codeurjc.backend.utilities.responseentity.ResourcesBuilder;
import io.vavr.control.Try;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.repository.TweetRepository;
import es.codeurjc.backend.repository.UserRepository;
import es.codeurjc.backend.service.UserService;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

// All methods/functionality programmed entirely by group 13-A
@Controller
public class ProfileController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TweetRepository tweetRepository;

    private Optional<User> loggedUserOpt;
    private User profileUser;

    private boolean following;

    @RequestMapping("/u/{username}")
    public String redirect(Model model, @PathVariable String username, HttpServletRequest req)
    {
        OptTwo<User> users = userService.getUserBy(username, req);

        // If URL-user exists
        if (users.isLeft()) {
            profileUser = users.getLeft();
            loggedUserOpt = users.getOptRight();

            model.addAttribute("authenticated", loggedUserOpt.isPresent());

            following = Try
                .of(() -> loggedUserOpt.get().getFollowing().contains(profileUser))
                .getOrElse(false);

            modelProfile(model, UserService.isOwnResource(username, loggedUserOpt));

            return "profile";
        } else 
            return "error";
    }

    @GetMapping("/u/{username}/profilepicture")
	public ResponseEntity<Resource> downloadImage(@PathVariable String username) 
    {
		Optional<User> user = userService.getUserBy(username);
        if (user.isEmpty()) return getAnonImage();

        Optional<Blob> profilePicture = Optional.ofNullable(user.get().getProfilePicture());
        if (profilePicture.isEmpty()) return getAnonImage();

        return ResourcesBuilder
            .tryBuildImgResponse(profilePicture)
            .getOrElse(getAnonImage());
	}

    private ResponseEntity<Resource> getAnonImage() 
    {
        Resource img = new ClassPathResource("static/images/anonpfp.jpg");
        return ResourcesBuilder.buildImgResponseOrNotFound(img);
    }

    @GetMapping("/u/{username}/write")
    public String startWritingTweet(@PathVariable String username, Model model) 
    {
        if (UserService.isOwnResource(username, loggedUserOpt)) 
        {
            model.addAttribute("posting", true);
            modelProfile(model, true);
            return "profile";
        } 
        else return "error";
    }

    @PostMapping("/u/{username}/posttweet")
    public String startWritingTweet(Model model, @PathVariable String username, 
    @RequestParam String text, @RequestParam MultipartFile image) 
    {
        if (UserService.isOwnResource(username, loggedUserOpt)) 
        {
            Tweet.Builder builder = 
            new Tweet.Builder()
                .setAuthor(loggedUserOpt.get())
                .setText(text);

            if (image.isEmpty())
                builder.setMedia(null);   
            else
                try {
                    builder.setMedia(BlobProxy.generateProxy(image.getInputStream(), image.getSize()));
                } catch (IOException e) {
                    builder.setMedia(null);
                }
            
            tweetRepository.save(builder.build());
            userService.save(profileUser);
            return "redirect:/u/" + username;
        } 
        else return "error";
    }

    @PostMapping("/u/{username}/profilepicture/update")
    public String uploadProfilePicture(@RequestParam MultipartFile image, @PathVariable String username)
    {     
        if (UserService.isOwnResource(username, loggedUserOpt))
        {
            if (!image.isEmpty()) 
            {
                Try.run(() -> profileUser.setProfilePicture(
                    BlobProxy.generateProxy(image.getInputStream(), image.getSize()),
                    userRepository
                ));
            }
           
            return "redirect:/u/" + loggedUserOpt.get().getUsername();
        } 
        else return "error";
    }

    @RequestMapping("/u/{username}/profilepicture/remove")
    public String removeProfilePicture(@PathVariable String username) 
    {
        if (UserService.isOwnResource(username, loggedUserOpt)) 
        {
            profileUser.setProfilePicture(null, userRepository);
            return "profile";
        } 
        else return "error";
    }

    private void modelProfile (Model model, boolean ownProfile) 
    {
        Collections.sort(profileUser.getTweets(), Collections.reverseOrder());
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("followerCount", profileUser.getFollowers(userService).size());
        model.addAttribute("ownProfile", ownProfile);
        model.addAttribute("following", following);
        if (loggedUserOpt.isEmpty()) return;
        User loggedUser = loggedUserOpt.get();
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("blocked", loggedUser.getBlocked().contains(profileUser));
    }
}
