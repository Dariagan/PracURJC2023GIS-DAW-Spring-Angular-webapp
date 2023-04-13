package es.codeurjc.backend.controller.deprecated;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import es.codeurjc.backend.repository.TweetRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.backend.model.Tweet;
import es.codeurjc.backend.model.User;
import es.codeurjc.backend.repository.UserRepository;
import es.codeurjc.backend.service.UserService;

import java.util.function.Function;

@RestController
@RequiredArgsConstructor
public class DataBaseController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TweetRepository tweetRepository;
	private final PasswordEncoder passwordEncoder;

    //Method body programmed by group 13-A
    @PostConstruct
    public void init() 
    {
        User.Builder builder = new User.Builder();
        Tweet.Builder tweetBuilder = new Tweet.Builder();

        // Building users
        builder
            .setUsername("a")
            .setEmail("a@a.com")
            .setEncodedPassword(passwordEncoder.encode("a"))
            .setIsAdmin();

        Function<String, User> userBuildAux = (c) -> builder
            .setUsername(c)
            .setEmail(String.format("%s@%s.com", c, c))
            .build();

        userService
            .save(builder.build())
            .save(userBuildAux.apply("b"))
            .save(userBuildAux.apply("c"))
            .save(userBuildAux.apply("d"))
            .save(userBuildAux.apply("e"));

        userRepository.flush();

        /*
        // /Building users

        userA.switchFollow(userB);
        userService.save(userA);
        
        // Building tweets
        tweetBuilder
            .setAuthor(userA)
            .setText("I like fat cats")
            .addTag("chonkers").addTag("cats").addTag("pets");
        
        Tweet tweet1 = tweetBuilder.build();

        
        tweet1.switchLike(userB);
        tweet1.report(userB);
   
        tweetService.save(tweet1);

        //-------------------------------------
        tweetBuilder.setAuthor(userB).setText("I dislike fat cats");
        
        Tweet tweet2 = tweetBuilder.build();
        Tweet tweet3 = tweetBuilder.setAuthor(userC).setText("amogus").clearTags().build();

        tweet2.report(userA);

        tweet2.report(userC);
      
        tweet2.switchLike(userD);
        
        tweet2.switchLike(userE);
        tweet3.switchLike(userD);
        tweet3.switchLike(userE);

        tweetService.save(tweet2).save(tweet3);

        tweetBuilder.setAuthor(userB);

        for (int i = 0; i < 20; i++)
        {
            tweetService.save(tweetBuilder.setText("tweet " + i).build());
            if (i == 6)
                tweetBuilder.clearTags().addTag("sports").setAuthor(userC);
            if (i == 10)
                tweetBuilder.clearTags().addTag("pets").setAuthor(userA);
            if (i == 12)
                tweetBuilder.clearTags().addTag("cars").addTag("pets").setAuthor(userB);
            if (i == 16)
                tweetBuilder.clearTags().addTag("cars").addTag("sports").setAuthor(userB);
        }
        */
    }
}
