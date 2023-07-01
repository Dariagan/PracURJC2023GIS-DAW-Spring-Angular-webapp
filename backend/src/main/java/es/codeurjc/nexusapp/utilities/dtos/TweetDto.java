package es.codeurjc.nexusapp.utilities.dtos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private final List<String> reporters, likes;

    private final List<Long> children = new ArrayList<>();

    public TweetDto(Tweet tweet){
        id = tweet.getId();
        date = tweet.getDate();
        hasMedia = tweet.hasMedia();
        author = new TweetDtoUser(tweet.getAuthor());
        text = tweet.getText();
        reporters = new ArrayList<>();
        likes = new ArrayList<>();
        for (User user: tweet.getReporters())
            reporters.add(user.getUsername());
        for (User user: tweet.getLikes())
            likes.add(user.getUsername());
        for (Tweet child: tweet.getChildren())
            children.add(child.getId());
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
