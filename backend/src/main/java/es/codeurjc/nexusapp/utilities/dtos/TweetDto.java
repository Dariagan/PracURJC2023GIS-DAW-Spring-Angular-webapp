package es.codeurjc.nexusapp.utilities.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.codeurjc.nexusapp.model.Tweet;
import es.codeurjc.nexusapp.model.User;
import lombok.Getter;

@Getter
public class TweetDto {
    
    private final long id;
    private final TweetDtoUser author;
    private final String text;
    private final LocalDateTime date;
    private final boolean hasMedia;
    private final Set<String> reporters = new HashSet<>(), likes = new HashSet<>(), tags;

    private final List<Long> children = new ArrayList<>();

    public TweetDto(Tweet tweet){
        id = tweet.getId();
        date = tweet.getDate();
        hasMedia = tweet.hasMedia();
        author = new TweetDtoUser(tweet.getAuthor());
        text = tweet.getText();
        for (User user: tweet.getReporters())
            reporters.add(user.getUsername());
        for (User user: tweet.getLikes())
            likes.add(user.getUsername());
        for (Tweet child: tweet.getChildren())
            children.add(child.getId());
        tags = tweet.getTags();
    }

    @Getter
    private class TweetDtoUser{
        private final String username, description, role;
        private final boolean banned, hasImage;

        public TweetDtoUser(User user){
            username = user.getUsername();
            description = user.getDescription();
            role = user.getRole().toString();
            banned = user.isBanned();
            hasImage = user.hasImage();
        }
    }
}
