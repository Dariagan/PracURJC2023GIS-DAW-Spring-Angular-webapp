package es.codeurjc.nexusapp.controller.deprecated;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;
import es.codeurjc.nexusapp.service.TweetService;
import es.codeurjc.nexusapp.service.UserService;

@RestController
public class DataBaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private TweetService tweetService;
    @Autowired
	private PasswordEncoder passwordEncoder;
   
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
            .setAdmin();
        
        User userA = builder.build();
        User userB = builder.setUsername("b").setEmail("b@b.com").setBasicUser().setDescription("hola soy b").build();
        User userC = builder.setUsername("c").setEmail("c@c.com").setDescription("hola soy c").build();
        User userD = builder.setUsername("d").setEmail("d@d.com").setDescription("hola soy d").build();
        User userE = builder.setUsername("e").setEmail("e@e.com").setDescription("hola soy e").build();
        
        userService.save(userA).save(userB).save(userC).save(userD).save(userE);

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

        for (int i = 0; i < 50; i++)
        {
            tweetService.save(tweetBuilder.setText("tweet " + (i + 4)).build());
            if (i == 6)
                tweetBuilder.clearTags().addTag("sports").setAuthor(userC);
            if (i == 10)
                tweetBuilder.clearTags().addTag("pets").setAuthor(userA);
            if (i == 12)
                tweetBuilder.clearTags().addTag("cars").addTag("pets").setAuthor(userB);
            if (i == 16)
                tweetBuilder.clearTags().addTag("cars").addTag("sports").setAuthor(userB);
        }

    }
}
