package es.codeurjc.nexusapp.controller.deprecated;


import java.io.IOException;
import java.sql.Blob;
import java.util.Collections;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import io.vavr.control.Try;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.service.TweetService;
import es.codeurjc.nexusapp.service.UserService;
import es.codeurjc.nexusapp.utilities.OptTwo;
import es.codeurjc.nexusapp.utilities.responseentity.ResourcesBuilder;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

// All methods/functionality programmed entirely by group 13-A
@Controller
public class ProfileController {
    
    @Autowired
    private UserService userService;
    @Autowired 
    private TweetService tweetService;

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

            modelProfile(model, UserService.isAllowed(username, loggedUserOpt));

            return "profile";
        } else 
            return "error";
    }

    @GetMapping("/u/{username}/profilepicture")
	public ResponseEntity<Resource> downloadImage(@PathVariable String username) 
    {
		Optional<User> user = userService.getUserBy(username);
        if (user.isEmpty()) return getAnonImage();

        Optional<Blob> profilePicture = Optional.ofNullable(user.get().getImage());
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
        if (UserService.isAllowed(username, loggedUserOpt)) 
        {
            model.addAttribute("posting", true);
            modelProfile(model, true);
            return "profile";
        } 
        else return "error";
    }

    @PostMapping("/u/{username}/posttweet")
    public String postTweet(Model model, @PathVariable String username, 
    @RequestParam String text, @RequestParam MultipartFile image) 
    {
        if (UserService.isAllowed(username, loggedUserOpt)) 
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
            
            tweetService.save(builder.build());
            userService.save(profileUser);
            return "redirect:/u/" + username;
        } 
        else return "error";
    }

    @PostMapping("/u/{username}/profilepicture/update")
    public String uploadProfilePicture(@RequestParam MultipartFile image, @PathVariable String username)
    {     
        if (UserService.isAllowed(username, loggedUserOpt))
        {
            if (!image.isEmpty()) 
            {
                Try.run(() -> profileUser.setImage(
                    BlobProxy.generateProxy(image.getInputStream(), image.getSize())
                ));

            }
           
            return "redirect:/u/" + loggedUserOpt.get().getUsername();
        } 
        else return "error";
    }

    @RequestMapping("/u/{username}/profilepicture/remove")
    public String removeProfilePicture(@PathVariable String username) 
    {
        if (UserService.isAllowed(username, loggedUserOpt)) 
        {
            profileUser.setImage(null);
            userService.save(profileUser);
            return "profile";
        } 
        else return "error";
    }

    private void modelProfile (Model model, boolean ownProfile) 
    {
        Collections.sort(profileUser.getTweets(), Collections.reverseOrder());
        model.addAttribute("profileUser", profileUser);
        model.addAttribute("followerCount", 
        userService.getFollowers(profileUser, Pageable.unpaged()).size());
        model.addAttribute("ownProfile", ownProfile);
        model.addAttribute("following", following);
        if (loggedUserOpt.isEmpty()) return;
        User loggedUser = loggedUserOpt.get();
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("blocked", loggedUser.getBlocked().contains(profileUser));
    }
}
