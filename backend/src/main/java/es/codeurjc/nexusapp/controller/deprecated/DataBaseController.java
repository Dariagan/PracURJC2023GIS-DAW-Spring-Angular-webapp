package es.codeurjc.nexusapp.controller.deprecated;

import java.util.HashSet;

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

        userA.toggleFollow(userB);
        userService.save(userA);
        
        // Building tweets
        tweetBuilder
            .setAuthor(userA)
            .setText("I like cats")
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

        HashSet<String> tags = new HashSet<>();

        String format = "about %s tweet nº%d";
        String text = "nothing";
        
        for (int i = 4; i < 50; i++)
        {
            if (i == 14){
                text = "cars";
                tags.add("cars");
            }if (i == 17){     
                text = "cars and sports";
                tags.add("sports");
            }if (i == 21){
                text = "sports cars and pets";
                tags.add("pets");
            }if (i == 25){
                tags.clear();
                text = "sports";
                tags.add("sports");
            }
            if (i == 29){
                tags.clear();
                text = "pets";
                tags.add("pets");
            }
            if (i == 34){
                text = "sports and pets";
                tags.add("sports");
            }
            tweetBuilder.setText(String.format(format, text, i));
            tweetBuilder.setTags(tags);

            tweetService.save(tweetBuilder.build());


        }

    }
}
