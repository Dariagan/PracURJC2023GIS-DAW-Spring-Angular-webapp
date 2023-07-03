package es.codeurjc.nexusapp.utilities.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;
import lombok.Getter;

@Getter
public class TweetDto {
    
    private final long id;
    private final UserDto author;
    private final String text;
    private final LocalDateTime date;
    private final boolean hasMedia;
    private final Set<String> reporters = new HashSet<>(), likes = new HashSet<>(), tags;

    private final List<Long> children = new ArrayList<>();

    public TweetDto(Tweet tweet){
        id = tweet.getId();
        date = tweet.getDate();
        hasMedia = tweet.hasMedia();
        author = new UserDto(tweet.getAuthor());
        text = tweet.getText();
        for (User user: tweet.getReporters())
            reporters.add(user.getUsername());
        for (User user: tweet.getLikes())
            likes.add(user.getUsername());
        for (Tweet child: tweet.getChildren())
            children.add(child.getId());
        tags = tweet.getTags();
    }

}
